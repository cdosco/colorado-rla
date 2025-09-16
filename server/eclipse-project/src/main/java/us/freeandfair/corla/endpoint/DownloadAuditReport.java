package us.freeandfair.corla.endpoint;


import com.google.gson.JsonParseException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.controller.AuditReport;
import us.freeandfair.corla.util.SparkHelper;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// endpoints don't need constructors
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class DownloadAuditReport extends AbstractEndpoint {

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
		return "/download-audit-report";
	}

	/**
	 * This endpoint requires either authorization, but only allows downloads
	 * by the county that made the upload, or by the state.
	 *
	 * @return EITHER
	 */
	@Override
	public AuthorizationType requiredAuthorization() {
		return AuthorizationType.STATE;
	}

	/**
	 * Validates the parameters of this request. The only requirement is that there be
	 * a parameter with the name in QUERY_PARAMETER; its parsing happens later.
	 */
	@Override
	public boolean validateParameters(final Request the_request) {
		return the_request.queryParams().contains("reports");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String endpointBody(final Request the_request, final Response the_response) {

		try {
			final String reports = the_request.queryParams("reports");
				if (reports == null) {
				badDataContents(the_response, "no report download requested");
			} else {
				List<String> selectedReports = Stream.of(reports.split(",", -1)).collect(Collectors.toList());

				try (OutputStream os = SparkHelper.getRaw(the_response).getOutputStream()) {
					the_response.header("Content-Type", "application/zip");
					the_response.header("Content-Disposition", "attachment; filename=Audit_Report.zip");
					AuditReport.generateZip(os, selectedReports);
					ok(the_response);
				} catch (final  IOException e) {
					serverError(the_response, "Unable to stream response");
				}
			}
		} catch (final JsonParseException e) {
			badDataContents(the_response, "malformed request: " + e.getMessage());
		}

		return my_endpoint_result.get();
	}

}

