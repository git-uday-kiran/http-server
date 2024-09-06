package server.http;

public enum MediaType {
	NONE("/"),
	ALL("*/*"),
	APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
	APPLICATION_JSON("application/json"),
	APPLICATION_OCTET_STREAM("application/octet-stream"),
	APPLICATION_PDF("application/pdf"),
	IMAGE_GIF("image/gif"),
	IMAGE_JPEG("image/jpeg"),
	IMAGE_PNG("image/png"),
	MULTIPART_FORM_DATA("multipart/form-data"),
	MULTIPART_MIXED("multipart/mixed"),
	MULTIPART_RELATED("multipart/related"),
	TEXT_("text/event-stream"),
	TEXT_HTML("text/html"),
	TEXT_PLAIN("text/plain"),
	TEXT_XML("text/xml");

	private final String value;

	MediaType(String value) {
		this.value = value;
	}

	public static MediaType fromString(String value) {
		if (value == null || value.isBlank()) return MediaType.NONE;
		String lowerCase = value.toLowerCase().trim();
		for (MediaType mediaType : MediaType.values()) {
			if (mediaType.value.equals(lowerCase)) {
				return mediaType;
			}
		}
		return MediaType.NONE;
	}

	public String string() {
		return value;
	}
}
