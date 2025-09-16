/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.IMPORT_BALLOT_MANIFEST_EVENT;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;

import spark.Request;
import spark.Response;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;


import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.csv.ColoradoBallotManifestParser;
import us.freeandfair.corla.csv.Result;
import us.freeandfair.corla.json.UploadedFileDTO;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.model.UploadedFile.FileStatus;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.BallotManifestInfoQueries;

/**
 * The "ballot manifest import" endpoint.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.ExcessiveImports"})
public class BallotManifestImport extends AbstractCountyDashboardEndpoint {

  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
    LogManager.getLogger(BallotManifestImport.class);

  /**
   * The " (id " string.
   */
  private static final String PAREN_ID = " (id ";
  
  /**
   * {@inheritDoc}
   */
  @Override
  public EndpointType endpointType() {
    return EndpointType.POST;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointName() {
    return "/import-ballot-manifest";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return IMPORT_BALLOT_MANIFEST_EVENT;
  }

  /**
   * Updates the appropriate county dashboard to reflect a new 
   * ballot manifest upload.
   * @param the_response The response object (for error reporting).
   * @param the_file The uploaded file.
   * @param the_ballot_count The ballot count from the manifest.
   */
  private void updateCountyDashboard(final Response the_response, 
                                     final UploadedFile the_file,
                                     final int the_ballot_count) {
    final CountyDashboard cdb = 
        Persistence.getByID(the_file.county().id(), CountyDashboard.class);
    if (cdb == null) {
      serverError(the_response, "could not locate county dashboard");
    } else {
      // now set the new manifest info
      cdb.setManifestFile(the_file);
      cdb.setBallotsInManifest(the_ballot_count);
      try {
        Persistence.saveOrUpdate(cdb);
      } catch (final PersistenceException e) {
        serverError(the_response, "could not update county dashboard");
      }
    }
  }
  
  /**
   * Parses an uploaded ballot manifest and attempts to persist it to the database.
   * 
   * @param the_response The response (for error reporting).
   * @param the_file The uploaded file.
   */
  // the CSV parser can throw arbitrary runtime exceptions, which we must catch
  @SuppressWarnings({"PMD.AvoidCatchingGenericException"})
  private void parseFile(final Response the_response, final UploadedFile the_file) {  
    try (InputStream bmi_is = the_file.file().getBinaryStream()) {
      final InputStreamReader bmi_isr = new InputStreamReader(bmi_is, "UTF-8");
      final ColoradoBallotManifestParser parser =
          new ColoradoBallotManifestParser(bmi_isr, 
                                           the_file.county().id());
      final int deleted = BallotManifestInfoQueries.deleteMatching(the_file.county().id());
      Result result = parser.parse();
      if (result.success) {
        final int imported = result.importedCount;
        result = new Result();
        result.success = true;
        result.importedCount = imported;
        the_file.setResult(result);
        LOGGER.info(imported + " ballot manifest records parsed from file " +
                         the_file.filename() + PAREN_ID + the_file.id() + ") for county " + 
                         the_file.county().id());
        updateCountyDashboard(the_response, the_file,
                              parser.ballotCount().getAsInt());
        the_file.setStatus(FileStatus.IMPORTED);
        Persistence.saveOrUpdate(the_file);
        final Map<String, Integer> response = new HashMap<String, Integer>();
        response.put("records_imported", imported);
        if (deleted > 0) {
          response.put("records_deleted", deleted);
        }
        okJSON(the_response, Main.GSON.toJson(response));
      } else {
        the_file.setResult(result);
        Persistence.saveOrUpdate(the_file);
        LOGGER.info("could not parse malformed ballot manifest file " +
                         the_file.filename() + PAREN_ID + the_file.id() + ") for county " + 
                         the_file.county().id());
        badDataContents(the_response, "malformed ballot manifest file " + 
                                      the_file.filename() + PAREN_ID + the_file.id() + ")");
      }
    } catch (final RuntimeException | IOException e) {
      LOGGER.info("could not parse malformed ballot manifest file " +
                       the_file.filename() + PAREN_ID + the_file.id() + ") for county " + 
                       the_file.county().id() + ": " + e);
      badDataContents(the_response, "malformed ballot manifest file " + 
                                    the_file.filename() + PAREN_ID + the_file.id() + ")");
    } catch (final SQLException e) {
      LOGGER.info("could not read file " + the_file.filename() +
                       PAREN_ID + the_file.id() + ") from persistent storage");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings({"PMD.ConfusingTernary"})
  public String endpointBody(final Request the_request, final Response the_response) {    
    // we know we have county authorization, so let's find out which county
    final County county = Main.authentication().authenticatedCounty(the_request);

    if (county == null) {
      unauthorized(the_response, "unauthorized administrator for ballot manifest upload");
      return my_endpoint_result.get();
    }
    
    try {
      UploadedFileDTO upF = Main.GSON.fromJson(the_request.body(), UploadedFileDTO.class);
      if (null == upF.getFileId()) {
        LOGGER.error(the_request.body() + " did not have a fileId attribute");
        badDataContents(the_response, "missing fileId attribute");
        return my_endpoint_result.get();
      }
      final UploadedFile file = Persistence.getByID(upF.getFileId(), UploadedFile.class);
      if (file == null) {
        badDataContents(the_response, "nonexistent file");
      } else if (!file.county().equals(county)) {
        unauthorized(the_response, "county " + county.id() + " attempted to import " + 
                                   "file " + file.filename() + " uploaded by county " + 
                                   file.county().id());
      } else if (file.getStatus() == FileStatus.HASH_VERIFIED) {
        parseFile(the_response, file);
      } else {
        badDataContents(the_response, "attempt to import a file without a verified hash");
      }
    } catch (final JsonParseException e) {
      badDataContents(the_response, "malformed request: " + e.getMessage());
    }
    
    return my_endpoint_result.get();
  }
}
