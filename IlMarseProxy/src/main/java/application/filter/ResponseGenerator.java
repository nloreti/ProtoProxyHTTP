package application.filter;

import model.HttpResponseImpl;
import exceptions.CloseException;

public class ResponseGenerator {

	public static HttpResponseImpl generateBlockedResponseByIp(
			final String destinationIp, final HttpResponseImpl response) {
		return generateBlockedResponse(
				"This ip has been blocked by the proxy administrator. IP: "
						+ destinationIp, response);
	}

	public static HttpResponseImpl generateBlockedResponseByUserAgent(
			HttpResponseImpl response) {
		return generateBlockedResponse("The user agent is blocked", response);
	}

	public static HttpResponseImpl generateBlockedResponseByMediaType(
			final HttpResponseImpl response) {

		response.setStatusCode(406);
		response.replaceHeader("Content-Length", "0");
		final String body = "";
		response.setBody(body.getBytes());
		return response;
	}

	public static HttpResponseImpl generateBlockedResponse(
			final HttpResponseImpl response) {
		return generateBlockedResponse(
				"The access has been completely blocked by the proxy administrator.",
				response);
	}

	public static HttpResponseImpl generateBlockedResponseByUri(
			final String requestURI, final HttpResponseImpl response) {
		return generateBlockedResponse(
				"This URI has been blocked by the proxy administrator. URI: "
						+ requestURI, response);
	}

	public static HttpResponseImpl generateBlockedResponse(final int size,
			final HttpResponseImpl response) {
		return generateBlockedResponse(
				"The resource you are trying to reach is too big. Size: "
						+ size, response);
	}

	private static HttpResponseImpl generateBlockedResponse(
			final String string, final HttpResponseImpl response) {
		try {
			final String body = "<title>Feedback Page</title><html><body><h1>"
					+ string + "<h1></body></html>";
			response.removeHeader("Content-Length");
			response.addHeader("Content-Length",
					String.valueOf(body.length()));
			response.removeHeader("Content-Encoding");
			response.setBody(body.getBytes());
		} catch (final Exception e) {
			throw new CloseException("Bad Blocked Response");
		}
		return response;
	}
}
