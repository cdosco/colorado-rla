package us.freeandfair.corla.endpoint;

import javax.persistence.PersistenceException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;

import us.freeandfair.corla.controller.DeleteFileController;
import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Administrator;
import us.freeandfair.corla.model.Administrator.AdministratorType;

/**
 * The endpoint for deleting a file or files for a county
 *
 * @author Democracy Works, Inc <dev@democracy.works>
 * @version 1.0.0
 */
// endpoints don't need constructors
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class DeleteFile extends AbstractEndpoint {
  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
    LogManager.getLogger(DeleteFile.class);

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
    return "/delete-file";
  }

  /**
   * This endpoint requires COUNTY authorization.
   */
  @Override
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.EITHER;
  }

  /**
   *
   */
  @Override
  public String endpointBody(final Request request,
                             final Response response) {
    final JsonParser parser = new JsonParser();
    final JsonObject o;
    Long countyId = null;
    String fileType = null;

    try {
      o = parser.parse(request.body()).getAsJsonObject();
      Administrator admin = request.session().attribute("admin");

      if (admin.type() == AdministratorType.STATE) {
        if (null != o.get("countyId")) {
          countyId = o.get("countyId").getAsLong();
        } else {
          // bad request
          badDataContents(response, "Missing countyId in post body");
        }
      } else if (admin.type() == AdministratorType.COUNTY) {
        countyId = Main.authentication().authenticatedCounty(request).id();
      }

      if (null != o.get("fileType")) {
        fileType = o.get("fileType").getAsString();
      } else {
        // bad request
        badDataContents(response, "Missing fileType in post body");
      }

      LOGGER.debug(String.format("[parsed request for deleting file: countyId=%d, fileType=%s",
                                 countyId, fileType));
      DeleteFileController.deleteFile(countyId, fileType);
    } catch (final PersistenceException | DeleteFileController.DeleteFileFail e) {
      // this will roll back the transaction in afterAfter()
      serverError(response, "could not delete file");
    }

    // for a full circle, we can return the data that was sent; the fileType
    okJSON(response, request.body());
    return my_endpoint_result.get();
  }
}
