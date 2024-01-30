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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import org.apache.cxf.attachment.Rfc5987Util;

import com.google.gson.JsonParseException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.util.FileHelper;
import us.freeandfair.corla.util.SparkHelper;

/**
 * The file download endpoint.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.ExcessiveImports"})
public class FileDownload extends AbstractEndpoint {

  /**
   * The download buffer size, in bytes.
   */
  private static final int BUFFER_SIZE = 1048576; // 1 MB

  /**
   * The maximum download size, in bytes.
   */
  private static final int MAX_DOWNLOAD_SIZE = 1073741824; // 1 GB

  /**
   * {@inheritDoc}
   */
  @Override
  public EndpointType endpointType() {
    return EndpointType.GET;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointName() {
    return "/download-file";
  }

  /**
   * This endpoint requires either authorization, but only allows downloads
   * by the county that made the upload, or by the state.
   * 
   * @return EITHER
   */
  @Override
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.EITHER;
  }
  
  /**
   * Validates the parameters of this request. The only requirement is that there be
   * a parameter with the name in QUERY_PARAMETER; its parsing happens later.
   */
  @Override
  public boolean validateParameters(final Request the_request) {
    return the_request.queryParams().contains("fileId");
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointBody(final Request the_request, final Response the_response) {
    // we know we have either state or county authentication; this will be null
    // for state authentication
    final County county = Main.authentication().authenticatedCounty(the_request);

    UploadedFile uploadedFile = null;

    try {
      final String fileId = the_request.queryParams("fileId");
      if (null != fileId) {
        uploadedFile = Persistence.getByID(Long.valueOf(fileId), UploadedFile.class);
      }
      if (uploadedFile == null) {
        badDataContents(the_response, "nonexistent file requested");
      } else if (county == null || county.id().equals(uploadedFile.county().id())) {
        the_response.type("text/csv");
        try {
          the_response.raw().setHeader("Content-Disposition", "attachment; filename=\"" + 
                                       Rfc5987Util.encode(uploadedFile.filename(), "UTF-8") + "\"");
        } catch (final UnsupportedEncodingException e) {
          serverError(the_response, "UTF-8 is unsupported (this should never happen)");
        }
        
        try (OutputStream os = SparkHelper.getRaw(the_response).getOutputStream()) {
          final int total =
              FileHelper.bufferedCopy(uploadedFile.file().getBinaryStream(), os,
                                      BUFFER_SIZE, MAX_DOWNLOAD_SIZE);
          Main.LOGGER.debug("sent file " + uploadedFile.filename() + " of size " + total);
          ok(the_response);
        } catch (final SQLException | IOException e) {
          serverError(the_response, "Unable to stream response");
        }
      } else {
        unauthorized(the_response, "county " + county.id() + " attempted to download " + 
                                   "file " + uploadedFile.filename() + " uploaded by county " +
                                   uploadedFile.county().id());
      }
    } catch (final JsonParseException e) {
      badDataContents(the_response, "malformed request: " + e.getMessage());
    }
    
    return my_endpoint_result.get();
  }
}
