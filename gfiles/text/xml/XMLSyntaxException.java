package gfiles.text.xml;

/**
 * main exception thrown by the xml parser and the xml file reader
 * 
 * @author Gavin
 *
 */
public class XMLSyntaxException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public XMLSyntaxException() {
		super("Invalid syntax in xml document");
	}

	public XMLSyntaxException(String message){
		super(message);
	}

}
