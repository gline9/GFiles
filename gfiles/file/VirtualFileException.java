package gfiles.file;

import java.io.IOException;

public class VirtualFileException extends IOException {
	private static final long serialVersionUID = 1L;

	public VirtualFileException(String message) {
		super(message);
	}

	public VirtualFileException() {
		super();
	}
}
