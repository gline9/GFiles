package gfiles.text;

/**
 * helper class for handling basic reading and writing operations to an ascii
 * text file.
 * 
 * @author Gavin
 *
 */
public class AsciiTextFileReader {

	// read pointer for reading from a file.
	private int pointer = 0;

	// file to read from.
	private final AsciiTextFile file;

	/**
	 * constructs an ascii text file reader from an existing ascii text file.
	 * 
	 * @param atf
	 *            file to read.
	 */
	public AsciiTextFileReader(AsciiTextFile atf) {
		file = atf;
	}

	/**
	 * resets the read pointer back to 0 so you can read the beginning again.
	 * Same as calling setPointer(0)
	 */
	public void resetRead() {
		setPointer(0);
	}

	/**
	 * moves the pointer to the specified character
	 * 
	 * @param pos
	 *            character to move the pointer to
	 */
	public void setPointer(int pos) {
		this.pointer = pos;
	}

	/**
	 * moves the pointer back one position, if the pointer is already at the
	 * beginning it has no effect.
	 */
	public void decrementPointer() {
		if (pointer != 0)
			this.pointer--;
	}

	/**
	 * reads the next character in the ascii text file. 
	 * 
	 * @return the next character or (char) 65535 for end of file.
	 */
	public char readChar() {
		return file.readCharAt(pointer++);
	}

	/**
	 * reads the next word in the file, a word is denoted by separation by
	 * anything that isn't alphanumeric. If there are multiple spaces in a row
	 * this will skip over them to the next alphanumeric characters.
	 * 
	 * @return the next word or (char) 0 if end of file
	 */
	public String readWord() {
		// initialize the results to nothing
		StringBuilder results = new StringBuilder();

		// all of the characters that if read will be used as a word
		String possible = "ABCDEDFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

		// next character, set to space to begin with as that is not possible in
		// a word.
		char next = ' ';

		// variable to hold the end of file character.
		char endOfFile = (char) 65535;

		// loop through until you find the start of the next word or end if end
		// of file is found
		while (!possible.contains(String.valueOf(next))) {

			// read the next character each iteration.
			next = readChar();

			// if it is the end of file return the end of file string
			if (next == endOfFile)
				return null;
		}

		// once a word is found, do the same but loop through until a character
		// that isn't in a word is found.

		while (possible.contains(String.valueOf(next))) {
			// append the character to the end of the results string.
			results.append(next);

			// read the next character.
			next = readChar();
		}

		// after the next non alphanumeric character is found return the
		// results.
		return results.toString();
	}

	/**
	 * reads the next line in the file, uses a /n character as line breaks.
	 * 
	 * @return the next line or null if end of file
	 */
	public String readLine() {
		// initialize the results to nothing
		StringBuilder results = new StringBuilder();

		// variable to hold the line end characters
		char lineEnd = '\n';
		char carriageReturn = '\r';

		// read the next character;
		char next = readChar();
		
		// if already at the end of the file return the end of file string
		if (next == 65535) {
			return null;
		}

		// loop through until the next character is a new line character or the
		// end of the file is reached.
		while (next != lineEnd && next != carriageReturn && next != 65535) {
			// append the character to the end of the results string.
			results.append(next);

			// read the next character.
			next = readChar();
		}

		// if it was a carriage return check for a new line and remove if found.
		if (next == carriageReturn) {
			next = readChar();

			// if it wasn't a line end move the pointer back to the previous
			// position.
			if (next != lineEnd)
				decrementPointer();
		}

		// after the line end character is found return the
		// results.
		return results.toString();
	}

}
