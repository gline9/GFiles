package gfiles.text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.regex.Pattern;

import gfiles.file.VirtualFile;

/**
 * class that takes in a csv file and can parse it and get the individual
 * values.
 * 
 * @author Gavin
 *
 */
public class CSVFile extends AsciiTextFile {

	// delimeter for the csv file.
	private String delimiter = ",";

	// boolean for if the file should ignore spaces next to the delimiter.
	private boolean ignoreSpaces = true;

	// currently read line of the csv file and pointer for the element in the
	// file.
	private String[] currentLine = new String[0];
	private int pointer = 0;

	// reader to read from the underlying ascii file
	private final AsciiTextFileReader reader = new AsciiTextFileReader(this);

	/**
	 * generic constructor for creating an empty csv file.
	 */
	public CSVFile() {}

	/**
	 * generates a csv file from an input stream.
	 * 
	 * @param in
	 *            input stream to read from.
	 * @throws IOException
	 */
	public CSVFile(InputStream in) throws IOException {
		super(in);
	}
	
	public CSVFile(byte[] data){
		super(data);
	}

	/**
	 * creates a csv file from a virtual file given to it.
	 * 
	 * @param vf
	 *            virtual file to make csv from
	 */
	public CSVFile(VirtualFile vf) {
		super(vf);
	}

	/**
	 * creates a csv file from an already existing csv file.
	 * 
	 * @param csvf
	 *            csv file to copy
	 */
	public CSVFile(CSVFile csvf) {
		super(csvf);

		// set the csv specific variables.
		this.delimiter = csvf.delimiter;
		this.ignoreSpaces = csvf.ignoreSpaces;
	}

	/**
	 * Sets the delimiter of the csv file. The delimiter isn't actually used
	 * until a next method is called so it is safe to set this as long as the
	 * file hasn't been accessed yet. The default delimiter of the file is a ','
	 * and this method doesn't need to be called if that is the delimiter you
	 * are using.
	 * 
	 * @param delimiter
	 *            delimiter for the csv file.
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * tells the csv whether to ignore spaces near the delimiters, default value
	 * is true. If this is set it will return a trimmed copy, i.e. Hello,
	 * World,test would return "Hello","World","test" if ignoring and "Hello",
	 * " World","test" if not.
	 * 
	 * @param ignore
	 *            whether or not to ignore spaces near the delimiter.
	 */
	public void ignoreSpacesNearDelimiter(boolean ignore) {
		ignoreSpaces = ignore;
	}

	/**
	 * returns the next value in the csv file where elements are separated by
	 * the delimiter.
	 * 
	 * @return next value in the csv file (char)65535 if end of file
	 */
	public String nextValue() {
		// get the next line of the csv file if at the end of the line
		if (pointer == currentLine.length) {
			advanceLine();
		}

		// return the next element and increment the pointer
		return currentLine[pointer++];
	}

	/**
	 * returns all of the values in the next line of the csv file.
	 * 
	 * @return array of values for next line and null for end of file
	 */
	public String[] nextLine() {
		// stores the results of the method.
		String[] results;

		// if the end of the line has alread been hit the results are the next
		// line. Otherwise return the rest of the current line.
		if (pointer == currentLine.length) {
			String line = reader.readLine();

			// check for end of line
			if (line == null)
				return null;

			results = line.split(Pattern.quote(delimiter));
		} else
			results = Arrays.copyOfRange(currentLine, pointer, currentLine.length);

		// advance the current line to the next line
		advanceLine();

		// return the results array
		return results;
	}

	/**
	 * advances the file to the next line of the csv file.
	 */
	public void advanceLine() {
		String line = reader.readLine();
		
		// set the current line to the next line of values.
		if (line == null)
			currentLine = new String[0];
		else
			currentLine = line.split(Pattern.quote(delimiter));

		// if ignore spaces is set trim the current line
		if (ignoreSpaces)
			trimArray(currentLine);

		// resset the pointer to the beginning.
		pointer = 0;
	}

	/**
	 * resets the csv file back to the first line.
	 */
	public void resetLine() {
		// reset the underlying reader.
		reader.resetRead();

		// set the current line to the next line.
		advanceLine();
	}

	/**
	 * trims all of the strings in the string array. Will change the array
	 * given.
	 * 
	 * @param strings
	 *            array to trim.
	 */
	private void trimArray(String[] strings) {
		for (int x = 0; x < strings.length; x++) {
			strings[x] = strings[x].trim();
		}
	}

}
