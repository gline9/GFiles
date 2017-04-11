package gfiles.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * a virtual file class that can be used to store a file in the memory of a
 * computer and the write or read from it without having to access the
 * harddrive. Has a max size of 2 ^ 31 - 1 bytes which is equivalent to 2 GB.
 * 
 * @author Gavin
 *
 */
public class VirtualFile {
	// the data that is in the file.
	private byte[] data;

	// pointer to the current location that needs to be written to.
	private int pointer = 0;

	// file size of an empty virtual file in kilobytes when created with the
	// default constructor.
	private static final int defaultFileSize = 1024;

	/**
	 * creates an empty virtual file that can be read from or written to.
	 */
	public VirtualFile() {
		data = new byte[defaultFileSize];
	}

	/**
	 * creates an empty virtual file with the given initial size for input. This
	 * is mainly used if you know how big your file will be before you want to
	 * write data to it. The file size will still increase if more data is
	 * written to it.
	 * 
	 * @param size
	 *            size to make the file.
	 */
	public VirtualFile(int size) {
		data = new byte[size];
	}

	/**
	 * creates a virtual file with the data given as the data inside the virtual
	 * file. This will save time when dealing with large byte arrays being put
	 * into a vitual file so they don't need to be written 1 byte at a time.
	 * 
	 * @param data
	 *            data for the file
	 */
	public VirtualFile(byte[] data) {
		this.data = Arrays.copyOf(data, data.length);
		pointer = data.length;
	}

	/**
	 * creates a virtual file from the given input stream. The constructor will
	 * continue to read from the input stream until the end has been reached and
	 * store all of the information.
	 * 
	 * @param in
	 *            input stream to read from.
	 */
	public VirtualFile(InputStream in) throws IOException {
		// get the output stream to a byte array to receive the data.
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();

		// transfer between the two streams.
		int amt;
		byte[] buffer = new byte[8128];
		while ((amt = in.read(buffer, 0, buffer.length)) != -1) {
			bOut.write(buffer, 0, amt);
		}

		// save the data into the internal buffer.
		data = bOut.toByteArray();
		pointer = bOut.size();
	}

	/**
	 * copy constructor for the virtual file class.
	 * 
	 * @param vf
	 *            virtual file to copy.
	 */
	public VirtualFile(VirtualFile vf) {
		// copy the data from the other virtual file.
		data = Arrays.copyOf(vf.data, vf.data.length);

		// copy the pointer location
		pointer = vf.pointer;
	}

	/**
	 * writes the given integer ignoring the leading bytes and only writing the
	 * last byte to the virtual file
	 * 
	 * @param info
	 *            data to be written to the virtual file.
	 */
	protected synchronized void write(int info) {
		// convert the data to a byte and then add to the buffer.
		byte writeData = (byte) info;
		data[pointer++] = writeData;

		// if the buffer is out of write room.
		if (pointer == data.length) {
			// expand the array.
			data = Arrays.copyOf(data, 3 * (data.length / 2) + 2);
		}
	}

	/**
	 * deletes all of the data from the file.
	 */
	public synchronized void clear() {
		pointer = 0;
	}

	/**
	 * reads the byte at the given index, if the index is out of range of the
	 * file a -1 is returned.
	 * 
	 * @param index
	 *            position to read byte from
	 * @return byte read at the index, -1 if no data there.
	 */
	protected int readAt(int index) {
		if (index >= pointer || index < 0)
			return -1;
		return data[index] & 0xFF;
	}

	/**
	 * gets the input stream for the virtual file, this will have all of the
	 * data so far but won't reflect changes in the file.
	 * 
	 * @return input stream for the virtual file.
	 */
	public synchronized InputStream getInputStream() {
		return new ByteArrayInputStream(Arrays.copyOf(data, pointer));
	}

	/**
	 * gets the output stream for the virtual file, this will give a portal to
	 * write to the file using other stream methods.
	 * 
	 * @return output stream for the virtual file.
	 */
	public OutputStream getOutputStream() {
		return new VirtualFileOutputStream(this);
	}

	/**
	 * saves the virtual file to the given file f.
	 * 
	 * @param f
	 *            location to save to.
	 */
	public synchronized void save(File f) throws IOException {
		// if the file doesn't exist, create it.
		if (!f.exists()) {
			f.createNewFile();
		}

		// create an output stream to the file given.
		OutputStream out = new FileOutputStream(f);

		// write the data up to pointer to the output stream.
		out.write(data, 0, pointer);

		// flush the output stream
		out.flush();

		// close the output stream
		out.close();
	}

	/**
	 * method to get how big the file is in bytes.
	 * 
	 * @return how many bytes the file is.
	 */
	public int getBytes() {
		return pointer;
	}

	/**
	 * loads in a virtual file from the file provided.
	 * 
	 * @param f
	 *            file to load
	 * @return
	 * @throws IOException
	 *             if file doesn't exist or is too big for the virtual file
	 *             class.
	 */
	public static VirtualFile load(File f) throws IOException {
		// make sure the file f is actually a file and not a directory.
		if (!f.isFile()) {
			throw new VirtualFileException("To load a file it needs to be a file, not a directory!!!");
		}

		// make sure the file is not too big for the virtualfile type
		if (f.length() > Integer.MAX_VALUE) {
			throw new VirtualFileException("File is too large to load in the virtual file data type, max size is 2 GB");
		}

		// make the virtual file to save the contents.

		VirtualFile vf = new VirtualFile((int) f.length());

		// load all of the data to the virtual file.
		InputStream in = new FileInputStream(f);

		// get the output stream to the virtual file.
		OutputStream vfOut = vf.getOutputStream();

		// transfer between the two streams.
		int amt;
		byte[] buffer = new byte[8128];
		while ((amt = in.read(buffer, 0, buffer.length)) != -1) {
			vfOut.write(buffer, 0, amt);
		}

		// flush the virtual file output stream
		vfOut.flush();

		// close the input stream
		in.close();

		// return the virtual file
		return vf;
	}

}
