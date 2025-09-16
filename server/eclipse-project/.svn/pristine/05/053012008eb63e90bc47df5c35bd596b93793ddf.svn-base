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

import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.IMPORT_CVRS_EVENT;

import java.io.InputStreamReader;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.gson.JsonParseException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.controller.ImportFileController;
import us.freeandfair.corla.json.UploadedFileDTO;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.ImportStatus;
import us.freeandfair.corla.model.ImportStatus.ImportState;
import us.freeandfair.corla.model.UploadedFile.FileStatus;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.UploadedFileQueries;

/**
 * The "CVR export import" endpoint.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.DoNotUseThreads"})
public class CVRExportImport extends AbstractCountyDashboardEndpoint {

  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
    LogManager.getLogger(CVRExportImport.class);

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
    return "/import-cvr-export";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return IMPORT_CVRS_EVENT;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings({"PMD.ConfusingTernary"})
  public String endpointBody(final Request the_request, final Response the_response) {
    final County county = Main.authentication().authenticatedCounty(the_request);
    // check logged in as county admin
    if (county == null) {
      unauthorized(the_response, "unauthorized administrator for CVR import");
      return my_endpoint_result.get();
    }


    final CountyDashboard cdb = Persistence.getByID(county.id(), CountyDashboard.class);
    UploadedFileDTO upF = null;
    final Map<String, Instant> responseBody = new HashMap<>();


    // check valid json
    try {
      upF = Main.GSON.fromJson(the_request.body(), UploadedFileDTO.class);
    } catch (final JsonParseException e) {
      badDataContents(the_response, "malformed request: " + e.getMessage());
      return my_endpoint_result.get();
    }

    // check parameter presence
    if (null == upF.getFileId()) {
      badDataContents(the_response, "missing file_id attribute");
      return my_endpoint_result.get();
    }

    UploadedFileDTO uploadedFileAttrs = UploadedFileQueries.getAttrs(upF);

    // check presence
    if (null == uploadedFileAttrs) {
      badDataContents(the_response, "nonexistent file");

    // check county
    } else if (!county.id().equals(uploadedFileAttrs.getCountyId())) {
      badDataContents(the_response, "wrong file id, not for current county");

    // check status
    } else if (!"HASH_VERIFIED".equals(uploadedFileAttrs.getStatus())) {
      if (!"IMPORTING".equals(uploadedFileAttrs.getStatus())) {
        badDataContents(the_response, "currently importing a cvr file");
      } else {
        badDataContents(the_response, "submitted hash failed verification");
      }

    // ok, proceed with correct county, and status
    } else {
      upF.setStatus(FileStatus.IMPORTING.toString());
      upF.setCountyId(county.id());
      UploadedFileQueries.updateStatus(upF);
      cdb.setCVRImportStatus(new ImportStatus(ImportState.IN_PROGRESS));
      // spawn a thread to do the import; this endpoint always immediately
      // returns a successful result if we get to this point
      (new Thread(new ImportFileController(upF))).start();

      responseBody.put("import_start_time", Instant.now());
      okJSON(the_response, Main.GSON.toJson(responseBody));
    }

    return my_endpoint_result.get();
  }
}
