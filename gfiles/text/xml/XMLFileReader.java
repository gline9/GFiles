package gfiles.text.xml;

import gcore.tuples.Pair;

/**
 * class that parses an xml file.
 * 
 * @author Gavin
 *
 */
public class XMLFileReader {

	// file for the reader to parse through
	private final XMLFile file;

	// destination for the parsing
	private final XMLStructure document = new XMLStructure();

	public XMLFileReader(XMLFile file) {
		this.file = file;

		// parse the file for reading.
		init();
	}

	/**
	 * initializes the xml file reader to have read the entire xml file.
	 */
	private void init() {

		// keep looping until the end of the xmlfile has been reached
		while (!file.endOfFile()) {
			// get the next tag
			file.nextTag();

			// store the tags values into the appropriate variables
			String tagName = file.getTagName();
			int tagType = file.getTagType();
			Pair<String, String>[] attributes = file.getTagAttributes();
			String textAfterTag = file.getTextAfterTag();

			// check for the type of tag
			switch (tagType) {
			case XMLFile.TYPE_OPENING_TAG:
				// add to the document appropriately
				document.addOpeningTag(tagName, attributes);
				break;

			case XMLFile.TYPE_CLOSING_TAG:
				// add to the document appropriately
				document.addClosingTag(tagName);
				break;

			case XMLFile.TYPE_SELF_CLOSING_TAG:
				// create the opening tag and then immediately close it
				document.addOpeningTag(tagName, attributes);
				document.addClosingTag(tagName);
			}

			// add the following info to the appropriate tag
			if (!textAfterTag.trim().equals("")) {
				document.addInfo(textAfterTag.trim());
			}

		}

		// after the entire document has been read, finalize the document
		document.finalize();

	}

	/**
	 * used to get the root tag for the xml document
	 * 
	 * @return root tag of the document.
	 * @since Mar 25, 2016
	 */
	public XMLTag getRoot() {
		return document.getRoot();
	}

}
