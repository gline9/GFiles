package gfiles.text.xml;

import java.util.Stack;

import gcore.tuples.Pair;

/**
 * stores the structure of the xml document that is read, the parser will save
 * its results in this data type for viewing.
 * 
 * @author Gavin
 *
 */
public class XMLStructure {

	// stores the entire structure for the xml document in a single root element
	private XMLTag root;

	// stores if the structure has been finalized or not
	private boolean finalized = false;

	// stack for the tag addition calls, when it is finalized this should be
	// empty as all tags should be accounted for.
	private Stack<XMLTag> tagStack = new Stack<>();

	/**
	 * initialize the xml structure
	 * 
	 * @param rootName
	 *            name of the root tag in the document
	 */
	protected XMLStructure() {}

	/**
	 * adds an opening tag to the structure
	 * 
	 * @param name
	 *            name of the tag
	 * @param attributes
	 *            attributes for the tag
	 */
	public void addOpeningTag(String name, Pair<String, String>[] attributes) {
		// make sure the structure hasn't been finalized
		if (finalized)
			throw new XMLSyntaxException("All xml content must be contained in the root tag!");

		// create a new tag
		XMLTag tag = new XMLTag(name);

		// add all of the attributes to the tag
		if (attributes != null) {
			for (Pair<String, String> attribute : attributes) {
				tag.addAttribute(attribute.getFirst(), attribute.getSecond());
			}
		}

		// add the tag to the top of the stack
		tagStack.push(tag);
	}

	/**
	 * adds a closing tag to the structure
	 * 
	 * @param name
	 *            name of the tag
	 */
	public void addClosingTag(String name) {
		// make sure the structure hasn't been finalized
		if (finalized)
			throw new XMLSyntaxException("All xml content must be contained in the root tag!");

		// take the top tag on the stack and make sure it has the same name
		XMLTag tag = tagStack.pop();
		if (!tag.getName().equals(name)) {
			System.out.println(tag.getName());
			System.out.println(name);
			throw new XMLSyntaxException("Mismatch tags in xml file");
		}

		// if the stack is empty, finalize the structure
		if (tagStack.isEmpty()) {
			finalized = true;
			root = tag;
		} else {
			// if the name matches add the tag to the current top tag of the
			// stack
			tagStack.peek().addElement(tag);
		}
	}

	public void addInfo(String info) {
		// make sure the structure hasn't been finalized
		if (finalized)
			throw new XMLSyntaxException("All xml content must be contained in the root tag!");

		// create a new xml info element
		XMLInfo xmlInfo = new XMLInfo(info);

		// add it to the top tag of the stack
		tagStack.peek().addElement(xmlInfo);
	}

	/**
	 * finalizes the xml structure
	 */
	public void finalize() {
		// if the structure hasn't been finalized yet throw an exception
		if (!finalized)
			throw new XMLSyntaxException("Must close the root tag to finalize the xml document!");
	}

	public XMLTag getRoot() {
		return root;
	}

}
