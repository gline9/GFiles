package gfiles.text;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gfiles.file.VirtualFile;

/**
 * class for loading values from a config.
 * 
 * @author Gavin
 *
 */
public class ConfigFile extends AsciiTextFile {

	// reader to read from the underlying ascii file
	private final AsciiTextFileReader reader = new AsciiTextFileReader(this);

	/**
	 * creates an empty config file.
	 */
	public ConfigFile() {}

	/**
	 * creates a config file from the given input stream, will read until the
	 * input stream terminates.
	 * 
	 * @param in
	 *            input stream to read from
	 * @throws IOException
	 */
	public ConfigFile(InputStream in) throws IOException {
		super(in);
	}
	
	public ConfigFile(byte[] data){
		super(data);
	}

	/**
	 * creates a config from a virtual file object.
	 * 
	 * @param vf
	 *            virtual file to create config from
	 */
	public ConfigFile(VirtualFile vf) {
		super(vf);
	}

	/**
	 * call this method to load the config into the given registry.
	 * 
	 * @param registry
	 *            registry to load config into.
	 */
	public void loadConfig(ConfigRegistry registry) {
		// pattern for checking config entries
		Pattern entryPattern = Pattern.compile("^\\s*(\\S+.*?\\S*)\\s*=\\s*(.+)$");

		// keep looping until there isn't another line to read.
		String nextLine;
		while ((nextLine = reader.readLine()) != null) {

			// if there is a comment on the line trim everything after the pound
			// symbol.
			if (nextLine.indexOf("#") != -1)
				nextLine = nextLine.substring(0, nextLine.indexOf("#")).trim();

			// check if the line is all white-space
			if (nextLine.matches("^\\s*$"))
				continue;


			// get the matcher for the pattern
			Matcher m = entryPattern.matcher(nextLine);
			
			// if the line doesn't match the pattern print an error
			if (!m.matches()) {
				System.err.println(nextLine + ": isn't correct syntax for a config line!");
				System.err.flush();
				continue;
			}

			// set the variable to the part before the equals
			String variable = m.group(1);

			// set the value to everything after the equals except trailing white space
			String value = m.group(2).trim();

			// call the config registry with the given variable and value and
			// store the results into success.
			boolean success = registry.applyConfigOption(variable, value);

			// if success is false it means there wasn't a config option or the
			// consumer wasn't set up properly, just send the failed message to
			// the system.err print writer.
			if (!success) {
				System.err.println("Invalid config option: " + variable);
				System.err.flush();
			}
		}

	}

}
