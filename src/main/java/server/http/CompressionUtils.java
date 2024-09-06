package server.http;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
@RequiredArgsConstructor
public class CompressionUtils {

	private final IOStreamUtils ioStreamUtils;
	private final CompressionScheme[] SUPPORTED = {CompressionScheme.GZIP,};

	public File compress(CompressionScheme scheme, InputStream in) {
		if (!isSupported(scheme)) {
			throw new RuntimeException("%s compression scheme is not supported.".formatted(scheme));
		}
		return gzipCompress(in);
	}

	public File gzipCompress(InputStream in) {
		File tempFile = ioStreamUtils.createTempFile();
		try (var out = new FileOutputStream(tempFile); var gzipOut = new GZIPOutputStream(out)) {
			in.transferTo(gzipOut);
			gzipOut.flush();
			return tempFile;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public InputStream decompress(CompressionScheme scheme, InputStream in) {
		if (!isSupported(scheme)) {
			throw new RuntimeException("%s compression scheme is not supported.".formatted(scheme));
		}
		return gzipDecompress(in);
	}

	public InputStream gzipDecompress(InputStream in) {
		File tempFile = ioStreamUtils.createTempFile();
		try (var out = new FileOutputStream(tempFile); var gzipIn = new GZIPInputStream(in); var gzipOut = new GZIPOutputStream(out)) {
			gzipIn.transferTo(gzipOut);
			return new FileInputStream(tempFile);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public boolean isSupported(CompressionScheme scheme) {
		for (CompressionScheme supportedScheme : SUPPORTED)
			if (supportedScheme == scheme) return true;
		return false;
	}

}
