package gfiles.file;

import java.io.OutputStream;

/**
 * output stream for the virtual file class.
 * 
 * @author Gavin
 *
 */
public class VirtualFileOutputStream extends OutputStream {

	// file that the output stream writes to.
	private final VirtualFile file;

	/**
	 * initializer for the output stream that takes the file to write to as a
	 * parameter.
	 * 
	 * @param f
	 *            file to write to.
	 */
	public VirtualFileOutputStream(VirtualFile f) {
		file = f;
	}

	@Override
	public void write(int data) {
		// just copies the data to the file.
		file.write(data); 
	}

}
