package server.http;

import lombok.Getter;

@Getter
public enum HttpStatus {

	OK(200, "OK"), CREATED(201, "Created"), ACCEPTED(202, "Accepted"),
	BAD_REQUEST(400, "Bad Request"), INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
	NOT_FOUND(404, "Not Found"), METHOD_NOT_ALLOWED(405, "Method Not Allowed");

	private final int statusCode;
	private final String reasonPhrase;

	HttpStatus(int statusCode, String reasonPhrase) {
		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase;
	}
}
