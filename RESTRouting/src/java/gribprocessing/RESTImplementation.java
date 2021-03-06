package gribprocessing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import http.HTTPServer;
import http.HTTPServer.Operation;
import http.HTTPServer.Request;
import http.HTTPServer.Response;
import http.RESTProcessorUtil;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This class defines the REST operations supported by the HTTP Server.
 * <p>
 * This list is defined in the <code>List&lt;Operation&gt;</code> named <code>operations</code>.
 * <br>
 * The SunFlower will use the {@link #processRequest(Request)} method of this class to
 * have the required requests processed.
 * </p>
 */
public class RESTImplementation {

	private static boolean verbose = "true".equals(System.getProperty("grib.verbose", "false"));

	private GRIBRequestManager gribRequestManager;

	public RESTImplementation(GRIBRequestManager restRequestManager) {

		this.gribRequestManager = restRequestManager;
		// Check duplicates in operation list. Barfs if duplicate is found.
		RESTProcessorUtil.checkDuplicateOperations(operations);
	}

	/**
	 * Define all the REST operations to be managed
	 * by the HTTP server.
	 * <p>
	 * Frame path parameters with curly braces.
	 * <p>
	 * See {@link #processRequest(Request)}
	 * See {@link HTTPServer}
	 */
	private List<Operation> operations = Arrays.asList(
			new Operation(
					"GET",
					"/grib/oplist",
					this::getOperationList,
					"List of all available operations on the Img service."),
			new Operation(
					"POST",
					"/grib/get-data",
					this::requestGRIBData,
					"Request a GRIB download from the web, and return its json representation."));

	protected List<Operation> getOperations() {
		return this.operations;
	}

	/**
	 * This is the method to invoke to have a REST request processed as defined above.
	 *
	 * @param request as it comes from the client
	 * @return the actual result.
	 */
	public Response processRequest(Request request) throws UnsupportedOperationException {
		Optional<Operation> opOp = operations
				.stream()
				.filter(op -> op.getVerb().equals(request.getVerb()) && RESTProcessorUtil.pathMatches(op.getPath(), request.getPath()))
				.findFirst();
		if (opOp.isPresent()) {
			Operation op = opOp.get();
			request.setRequestPattern(op.getPath()); // To get the prms later on.
			Response processed = op.getFn().apply(request); // Execute here.
			return processed;
		} else {
			throw new UnsupportedOperationException(String.format("%s not managed", request.toString()));
		}
	}

	private Response getOperationList(Request request) {
		Response response = new Response(request.getProtocol(), Response.STATUS_OK);

		List<Operation> opList = this.getOperations();
		String content = new Gson().toJson(opList);
		RESTProcessorUtil.generateResponseHeaders(response, content.length());
		response.setPayload(content.getBytes());
		return response;
	}

	/**
	 * The payload is a list of requests, like this
	 *
	 * {}
	 *
	 * @param request
	 * @return
	 */
	private Response requestGRIBData(Request request) {
		Response response = new Response(request.getProtocol(), Response.STATUS_OK);

		if (request.getContent() != null && request.getContent().length > 0) {
			String payload = new String(request.getContent());
			if (!"null".equals(payload)) {
				if (verbose) {
					System.out.println(String.format("Tx Request: %s", payload));
				}
				Gson gson = new GsonBuilder().create();
				StringReader stringReader = new StringReader(payload);
				List<Object> txRequests = null;
				final List<Object> resultList = new ArrayList<>();
				try {
					txRequests = gson.fromJson(stringReader, List.class);

					// TODO Invoke GRIB utilities here. See Sample01

					String content = new Gson().toJson(resultList);
					RESTProcessorUtil.generateResponseHeaders(response, content.length());
					response.setPayload(content.getBytes());
				} catch (Exception ex) {
					ex.printStackTrace();
					response = HTTPServer.buildErrorResponse(response,
							Response.BAD_REQUEST,
							new HTTPServer.ErrorPayload()
									.errorCode("GRIB-0001")
									.errorMessage(ex.toString()));
					return response;
				}
			} else {
				response = HTTPServer.buildErrorResponse(response,
						Response.BAD_REQUEST,
						new HTTPServer.ErrorPayload()
								.errorCode("GRIB-0002")
								.errorMessage("Request payload not found"));
				return response;
			}
		} else {
			response = HTTPServer.buildErrorResponse(response,
					Response.BAD_REQUEST,
					new HTTPServer.ErrorPayload()
							.errorCode("GRIB-0002")
							.errorMessage("Request payload not found"));
			return response;
		}

		return response;
	}

	/**
	 * Can be used as a temporary placeholder when creating a new operation.
	 *
	 * @param request
	 * @return
	 */
	private Response emptyOperation(Request request) {
		Response response = new Response(request.getProtocol(), Response.NOT_IMPLEMENTED);
		return response;
	}
}
