package gfiles.text;

/**
 * used to convert the ascii text file into a character sequence
 * 
 * @author Gavin
 *
 */
public class AsciiTextFileSequence implements CharSequence {

	private final int startIndex;
	private final int endIndex;

	private final AsciiTextFile file;

	protected AsciiTextFileSequence(int startIndex, int endIndex, AsciiTextFile file) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.file = file;
	}

	@Override
	public char charAt(int index) {
		// check the bounds
		if (index >= length())
			throw new IndexOutOfBoundsException();
		return file.readCharAt(startIndex + index);
	}

	@Override
	public int length() {
		return endIndex - startIndex;
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		if (start < 0 || end < 0)
			throw new IndexOutOfBoundsException();
		if (start >= end)
			throw new IndexOutOfBoundsException();
		if (end >= length())
			throw new IndexOutOfBoundsException();
		return new AsciiTextFileSequence(start + startIndex, end + startIndex, file);
	}

}
