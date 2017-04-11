package gfiles.text.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import gcore.tuples.Pair;
import gfiles.file.VirtualFile;
import gfiles.text.AsciiTextFile;
import gfiles.text.AsciiTextFileReader;

/**
 * class that takes in an xml value and parses the tags as they are needed. To
 * use, an XMLReader class is needed as this class is for low level reading of
 * the document.
 * 
 * @author Gavin
 *
 */
public class XMLFile extends AsciiTextFile {

	// static variables that hold values for reference of different types
	public static final int TYPE_OPENING_TAG = 0;
	public static final int TYPE_CLOSING_TAG = 1;
	public static final int TYPE_SELF_CLOSING_TAG = 2;
	public static final int TYPE_SETTING = 3;
	public static final int TYPE_PROPERTY = 4;

	// stores the line that is currently being read from, when the line is empty
	// a new line is read.
	private String currentLine = "";

	// stores the current tag that the parser is on
	private String currentTag = "";

	private int tagType = -1;

	private String tagName = "";

	// true if the end of the file has been reached.
	private boolean endOfFile = false;

	@SuppressWarnings("unchecked")
	private Pair<String, String>[] attributes = new Pair[0];

	private String immediateText = "";

	// reader to read from the underlying ascii file
	private final AsciiTextFileReader reader = new AsciiTextFileReader(this);

	/**
	 * creates an empty xml file.
	 */
	public XMLFile() {}

	/**
	 * creates an xml file from the given input stream, will read until the
	 * input stream terminates.
	 * 
	 * @param in
	 *            input stream to read from
	 * @throws IOException
	 */
	public XMLFile(InputStream in) throws IOException {
		super(in);
	}

	public XMLFile(byte[] data) {
		super(data);
	}

	/**
	 * creates an xml file from a virtual file object.
	 * 
	 * @param vf
	 *            virtual file to create xml file from
	 */
	public XMLFile(VirtualFile vf) {
		super(vf);
	}

	/**
	 * advances the parser until it finds the next tag.
	 * 
	 * @since Mar 22, 2016
	 */
	public void nextTag() {
		// if at the end of the file do nothing
		if (endOfFile)
			return;

		// check for end of file
		if (currentLine == null) {
			currentLine = "";
			currentTag = "";
			tagType = -1;
			tagName = "";
			endOfFile = true;
		}
		// delete the immediate text
		immediateText = "";

		// advance the line until a tag is found
		while (!currentLine.contains("<")) {
			currentLine = reader.readLine();

			if (currentLine == null) {
				currentLine = "";
				currentTag = "";
				tagType = -1;
				tagName = "";
				endOfFile = true;
			}
		}

		// get the indices of the starting and ending tags
		int start = currentLine.indexOf("<");
		int end = currentLine.indexOf(">");

		// check if the tag is a comment, if it is change the end index
		// accordingly
		if (currentLine.substring(start + 1, start + 4).equals("!--")) {
			// remove the comment header
			currentLine = currentLine.substring(start + 4);

			// loop through until the end index is found
			while (currentLine.indexOf("--") == -1) {
				currentLine = reader.readLine();

				// check for end of file
				if (currentLine == null)
					throw new XMLSyntaxException("Unexpected end of file in xml document!");
			}

			// once it is found check to make sure there is a closing brace
			int commentEnd = currentLine.indexOf("--");
			int endBrace = currentLine.indexOf(">");

			if (commentEnd + 2 != endBrace)
				throw new XMLSyntaxException("Comment end in xml document was found before expected!");

			// remove everything upto the comment and remove it from the current
			// line
			currentLine = currentLine.substring(endBrace + 1);

			// loop back and try to find a tag again.
			nextTag();

			// return once found
			return;
		}

		// if no ending tag throw an xml exception.
		if (end == -1)
			throw new XMLSyntaxException("Not closed tag in xml document!");

		// get the text and put it in the tag string
		currentTag = currentLine.substring(start + 1, end);

		// parse the current tag into the corresponding variables
		parseTag(currentTag);

		// remove the tag from the currentLine and only view the text after the
		// tag
		currentLine = currentLine.substring(end + 1);

		// get the text following the tag
		while (currentLine.indexOf('<') == -1) {
			if (!currentLine.equals("")) {
				immediateText += currentLine.trim() + "\n";
			}
			currentLine = reader.readLine();

			// check for end of file
			if (currentLine == null && !immediateText.trim().equals(""))
				throw new XMLSyntaxException("Unexpected end of file in xml document!");

			// if at the end of the file exit
			if (currentLine == null) {
				endOfFile = true;
				break;
			}
		}

		// trim everything up to the bracket into the immediateText\

		// make sure the currentLine isn't null
		if (currentLine != null) {
			immediateText += currentLine.substring(0, currentLine.indexOf('<')).trim();

			currentLine = currentLine.substring(currentLine.indexOf('<'));
		}
	}

	@SuppressWarnings("unchecked")
	private void parseTag(String tag) {
		// check the first and last character for the tag type and trim off any
		// specifying characters.
		char firstChar = tag.charAt(0);
		char lastChar = tag.charAt(tag.length() - 1);

		// check for closing tag
		if (firstChar == '/') {
			tagType = TYPE_CLOSING_TAG;
			tag = tag.substring(1);
		}
		// check for property tag
		else if (firstChar == '!') {
			tagType = TYPE_PROPERTY;
			tag = tag.substring(1);
		}
		// check for setting tag
		else if (firstChar == '?' && lastChar == '?') {
			tagType = TYPE_SETTING;
			tag = tag.substring(1, tag.length() - 1);
		}
		// check for self closing tag
		else if (lastChar == '/') {
			tagType = TYPE_SELF_CLOSING_TAG;
			tag = tag.substring(0, tag.length() - 1);
		}
		// if all else fails it is a normal tag
		else {
			tagType = TYPE_OPENING_TAG;
		}

		// get the name by taking the first section before a space in the tag
		int nameIndex = tag.indexOf(' ');

		// if there is no space the whole thing is the name
		if (nameIndex == -1) {
			tagName = tag;

			// reset the other variables
			attributes = new Pair[0];

			// return as the parsing is done
			return;
		}

		// otherwise take the first word as the name
		tagName = tag.substring(0, nameIndex);

		// change the tag to not include the name
		tag = tag.substring(nameIndex + 1);

		// break up the tag on spaces so there will be words to parse
		String[] words = tag.split(" ");

		// to store the eventual attributes
		ArrayList<Pair<String, String>> attributes = new ArrayList<>();

		// loop through every element in the words array
		String name = "";
		String value = "";
		char breakChar = 0;
		boolean equalsFound = false;
		for (String word : words) {
			// check if there isn't an =
			if (!name.equals("") && word.indexOf("=") != 0 && !equalsFound && !word.equals("")) {
				throw new XMLSyntaxException("invalidly formatted attribute in xml document!");
			}

			// otherwise check if the name has been set yet
			if (name.equals("")) {
				// check if there is an equals in the string
				int equalsIndex = word.indexOf("=");
				if (equalsIndex != -1) {
					equalsFound = true;

					// set the name to everything before the equals
					name = word.substring(0, equalsIndex);

					// trim the word
					word = word.substring(equalsIndex + 1);

					// check if the word isn't empty
					if (!word.equals("")) {
						// if not check that the first character is a ' or a "
						char quote = word.charAt(0);

						if (quote != '\'' && quote != '"') {
							throw new XMLSyntaxException("invalid quotting of attribute in xml document!");
						}

						// if it passed set the break character to it and add
						// everything to the value string
						breakChar = quote;

						// trim the break character out of the word
						word = word.substring(1);

						// check for break character appearing again
						if (word.indexOf(breakChar) != -1) {
							// if the index isn't the last throw an exception
							if (word.indexOf(breakChar) != (word.length() - 1)) {
								throw new XMLSyntaxException("invalid quotting of attribute in xml document!");
							}

							// otherwise set the value to the word minus the
							// first and last characters and continue on to the
							// next attribute
							value += word.substring(0, word.length() - 1);
							attributes.add(new Pair<>(name, value));

							name = "";
							value = "";
							breakChar = 0;
							equalsFound = false;
							continue;
						}

						value += word.substring(0);

					}

					// continue on to the next word
					continue;
				} else {
					// if the equals index was -1, i.e. an equals wasn't found,
					// just set the name and continue on to the next
					name = word;
					continue;
				}
			}

			// check if there hasn't been an equals found
			if (!equalsFound) {
				// if the word is empty continue on to the next word
				if (word.equals(""))
					continue;

				// otherwise equals is the first character
				equalsFound = true;

				// parse through the rest for the attributes.
				word = word.substring(1);

				// check if the word isn't empty
				if (!word.equals("")) {
					// if not check that the first character is a ' or a "
					char quote = word.charAt(0);

					if (quote != '\'' && quote != '"') {
						throw new XMLSyntaxException("invalid quotting of attribute in xml document!");
					}

					// if it passed set the break character to it and add
					// everything to the value string
					breakChar = quote;

					// trim the break character out of the word
					word = word.substring(1);

					// check for break character appearing again
					if (word.indexOf(breakChar) != -1) {
						// if the index isn't the last throw an exception
						if (word.indexOf(breakChar) != (word.length() - 1)) {
							throw new XMLSyntaxException("invalid quotting of attribute in xml document!");
						}

						// otherwise set the value to the word minus the
						// first and last characters and continue on to the
						// next attribute
						value += word.substring(0, word.length() - 1);
						attributes.add(new Pair<>(name, value));

						name = "";
						value = "";
						breakChar = 0;
						equalsFound = false;
						continue;
					}

					value += word.substring(0);

				}

				// continue on to the next word
				continue;
			}

			// check if the break char hasn't been found yet
			if (breakChar == 0) {
				// if the word is empty continue on to the next word
				if (word.equals(""))
					continue;

				// if not check that the first character is a ' or a "
				char quote = word.charAt(0);

				if (quote != '\'' && quote != '"') {
					throw new XMLSyntaxException("invalid quotting of attribute in xml document!");
				}

				// if it passed set the break character to it and add
				// everything to the value string
				breakChar = quote;

				// trim the break character out of the word
				word = word.substring(1);

				// check for break character appearing again
				if (word.indexOf(breakChar) != -1) {
					// if the index isn't the last throw an exception
					if (word.indexOf(breakChar) != (word.length() - 1)) {
						throw new XMLSyntaxException("invalid quotting of attribute in xml document!");
					}

					// otherwise set the value to the word minus the
					// first and last characters and continue on to the
					// next attribute
					value += word.substring(0, word.length() - 1);
					attributes.add(new Pair<>(name, value));

					name = "";
					value = "";
					breakChar = 0;
					equalsFound = false;
					continue;
				}

				value += word.substring(0);

				// continue on to the next word
				continue;

			}

			// otherwise the break character has already been found and the last
			// break character needs to be found

			// if there is a final break character
			if (word.indexOf(breakChar) != -1) {
				// if the index isn't the last throw an exception
				if (word.indexOf(breakChar) != (word.length() - 1)) {
					throw new XMLSyntaxException("invalid quotting of attribute in xml document!");
				}

				// otherwise set the value to the word minus the
				// first and last characters and continue on to the
				// next attribute
				value += " " + word.substring(0, word.length() - 1);
				attributes.add(new Pair<>(name, value));

				name = "";
				value = "";
				breakChar = 0;
				equalsFound = false;
				continue;
			}

			// otherwise just add the word to value
			value += " " + word;

		}

		// if one of the values isn't empty there was an abrupt end and an
		// exception should be thrown
		if (!name.equals("") || !value.equals("") || breakChar != 0 || equalsFound) {
			throw new XMLSyntaxException("unexpected end of the attribute section");
		}

		// otherwise take the attributes and store them.
		this.attributes = attributes.toArray(this.attributes);

	}

	public int getTagType() {
		return tagType;
	}

	public String getTagText() {
		return currentTag;
	}

	public String getTagName() {
		return tagName;
	}

	public Pair<String, String>[] getTagAttributes() {
		return attributes;
	}

	public String getTextAfterTag() {
		return immediateText;
	}

	public boolean endOfFile() {
		return endOfFile;
	}

	/**
	 * saves the given root tag into an xml virtual file
	 * 
	 * @param root
	 *            root to save
	 * @return virtual xml file with needed contents
	 */
	public static VirtualFile saveTagAsFile(XMLTag root) {

		// grab the bytes from the root
		String rootString = root.toString();
		byte[] bytes = rootString.getBytes();

		// save the bytes to a new virtual file and return it
		return new VirtualFile(bytes);

	}

}
