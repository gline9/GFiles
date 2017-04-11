package gfiles.text.xml;

/**
 * simple class for storing the text info inside an xml document.
 * 
 * @author Gavin
 *
 */
public class XMLInfo extends XMLElement {
	// the string value of the info
	private final String info;

	/**
	 * construct the info class by passing in the info you want it to store
	 * 
	 * @param info
	 */
	public XMLInfo(String info) {
		this.info = info;
	}

	/**
	 * called to get the info contained in the info class
	 * 
	 * @return info in the class
	 */
	public String getInfo() {
		return info;
	}

	public String toString() {
		return info;
	}
}
