package gfiles.text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import gfiles.file.VirtualFile;

/**
 * class that loads in a text file and can be viewed and read through.
 * 
 * @author Gavin
 *
 */
public class AsciiTextFile extends VirtualFile {

	// file size of an empty virtual file in kilobytes when created with the
	// default constructor.
	private static final int defaultFileSize = 1024;

	/**
	 * default constructor just makes a new blank text file with nothing in it.
	 */
	public AsciiTextFile() {
		this(defaultFileSize);
	}

	/**
	 * specifies how many characters the text file will have, used if the size
	 * of the file is known before it is read.
	 * 
	 * @param size
	 */
	public AsciiTextFile(int size) {
		// initialize the virtual file to have the same size as the text file.
		super(size);
	}
	
	public AsciiTextFile(byte[] data){
		super(data);
	}

	/**
	 * creates an ascii text file from a pre-loaded virtual file.
	 * 
	 * @param vf
	 *            virtual file to read from.
	 */
	public AsciiTextFile(VirtualFile vf) {
		// construct the underlying virtual file using the copy constructor.
		super(vf);

	}

	/**
	 * creates an ascii text file from an input stream.
	 * 
	 * @param in
	 *            input stream to read from until finish.
	 * @throws IOException
	 */
	public AsciiTextFile(InputStream in) throws IOException {
		super(in);
	}

	/**
	 * writes the given character to the stream.
	 * 
	 * @param c
	 *            character to write.
	 */
	public void writeChar(char c) {
		// write the data to the underlying virtual file.
		write(c);
	}

	public char readCharAt(int i) {
		return (char) readAt(i);
	}

	/**
	 * loads a text file using the ascii formatting and returns a new ascii text
	 * file containing its contents.
	 * 
	 * @param f
	 *            file to read
	 * @return file loaded into an ascii text file
	 */
	public static AsciiTextFile load(File f) throws IOException {
		VirtualFile vf = VirtualFile.load(f);
		return new AsciiTextFile(vf);
	}
}
