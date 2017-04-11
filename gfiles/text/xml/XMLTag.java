package gfiles.text.xml;

import java.util.ArrayList;

import gcore.tuples.Pair;

public class XMLTag extends XMLElement {
	// stores all of the contained features in an array list of elements
	private ArrayList<XMLElement> elements = new ArrayList<>();

	// stores the name of the tag
	private final String tagName;

	// stores the attributes of the tag
	private ArrayList<Pair<String, String>> attributes = new ArrayList<>();

	/**
	 * initialize the element with what the name of the tag is.
	 * 
	 * @param name
	 *            name of the tag
	 */
	public XMLTag(String name) {
		tagName = name;
	}

	/**
	 * used for obtaining the name of the current tag
	 * 
	 * @return name of the tag.
	 */
	public String getName() {
		return tagName;
	}

	/**
	 * adds an attribute to the tag with the given name and value
	 * 
	 * @param name
	 *            name of the attribute
	 * @param value
	 *            value of the attribute
	 */
	public void addAttribute(String name, String value) {
		attributes.add(new Pair<>(name, value));
	}

	/**
	 * method for obtaining the value of a given attribute, will return null if
	 * the attribute isn't present in the tag.
	 * 
	 * @param name
	 *            name of the attribute
	 * @return value of the attribute or null if it doesn't exist.
	 */
	public String getAttributesValue(String name) {
		// loop through the array list looking for the attribute, once found
		// return otherwise return null.
		for (Pair<String, String> attribute : attributes) {
			if (attribute.getFirst().equals(name))
				return attribute.getSecond();
		}

		return null;
	}

	/**
	 * adds an element to the current tag in order from the last add.
	 * 
	 * @param element
	 *            element to add
	 */
	public void addElement(XMLElement element) {
		elements.add(element);
	}

	/**
	 * finds the first tag with the given name and returns it, same as calling
	 * {@link #getNthOccurrence(String, int) getNthOccurrence(name, 0)}.
	 * 
	 * @param name
	 *            name of the tag
	 * @return first tag with that name in the current tag.
	 */
	public XMLTag getFirstTag(String name) {
		return getNthOccurrence(name, 0);
	}

	/**
	 * returns the nth occurrence of the given tags string in the current tag,
	 * if that tag doesn't exist it will return null.
	 * 
	 * @param name
	 *            name of the tag
	 * @param n
	 *            occurrence of the tag in the current tag
	 * @return the found tag
	 */
	public XMLTag getNthOccurrence(String name, int n) {
		// keeps track of how many occurrences have been found
		int count = 0;

		// loop through and increment counter for each one found, when the
		// counter is at the occurrence after the increment return the tag.
		for (XMLElement element : elements) {
			if (element instanceof XMLTag) {
				XMLTag tag = (XMLTag) element;
				if (name.equals(tag.getName())) {

					if (count++ == n)
						return tag;
				
				}
			}
		}
		return null;
	}

	/**
	 * gets the first occurrence of a tag in the current tag that has the given
	 * name and has the attribute with the given value.
	 * 
	 * @param name
	 *            name of the tag
	 * @param attributeName
	 *            name of the attribute
	 * @param attributeValue
	 *            value of the attribute
	 * @return first occurrence of such a tag.
	 */
	public XMLTag getFirstTagWithValue(String name, String attributeName, String attributeValue) {
		return getNthTagWithValue(name, attributeName, attributeValue, 0);
	}

	/**
	 * gets the nth occurrence of a tag in the current tag that has the given
	 * name and has the attribute with the given value.
	 * 
	 * @param name
	 *            name of the tag
	 * @param attributeName
	 *            name of the attribute
	 * @param attributeValue
	 *            value of the attribute
	 * @param n
	 *            number of attribute to look at.
	 * @return nth occurrence of such a tag.
	 */
	public XMLTag getNthTagWithValue(String name, String attributeName, String attributeValue, int n) {
		// check if n is less than 0
		if (n < 0)
			return null;

		// keeps track of how many occurrences have been found
		int count = 0;

		// loop through and increment counter for each one found, when the
		// counter is at the occurrence after the increment return the tag.
		for (XMLElement element : elements) {
			if (element instanceof XMLTag) {
				XMLTag tag = (XMLTag) element;

				// check that the name matches and the attributes value matches.
				if (name.equals(tag.getName())) {

					String value = tag.getAttributesValue(attributeName);
					if (value.equals(attributeValue)) {

						// if we are at the desired value return the tag.
						if (count++ == n)
							return tag;
					}
				}
			}
		}
		return null;
	}

	/**
	 * used for the ability to access all of the xml tags elements without being
	 * able to edit any of them inside the actual tag.
	 * 
	 * @return a copy of the elements list.
	 */
	public ArrayList<XMLElement> getElements() {
		return new ArrayList<>(elements);
	}

	public String toString() {
		String results = "";
		results += "<" + tagName + attributeString() + ">\n";
		for (XMLElement element : elements) {
			results += "\t" + element.toString().replaceAll("\n", "\n\t") + "\n";
		}
		results += "</" + tagName + ">";
		return results;
	}

	private String attributeString() {
		StringBuilder results = new StringBuilder();
		for (Pair<String, String> attribute : attributes) {
			results.append(" " + attribute.getFirst() + " = \"" + attribute.getSecond() + "\"");
		}

		return results.toString();
	}
}
