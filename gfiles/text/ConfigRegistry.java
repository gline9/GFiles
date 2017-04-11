package gfiles.text;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * class for handling config initialization, see the {@link #initConfigMap()
 * initConfigmap} method for more details on how to implement.
 * 
 * @author Gavin
 *
 */
public abstract class ConfigRegistry {
	private final HashMap<String, Consumer<String>> configMap = new HashMap<>();

	/**
	 * default constructor for the config registry, will initialize the config
	 * map for using in the config.
	 */
	public ConfigRegistry() {
		initConfigMap();
	}

	/**
	 * this method needs to add elements using the
	 * {@link #addConfigElement(String, Consumer) addConfigElement} method. This
	 * method will cause the consumer to be triggered with the config value when
	 * the string is found in the config options. Optional method to overwrite
	 * is the {@link #caseSensitive() caseSensitive} method which defaults to
	 * false but with true you can have different config options with the same
	 * letters but different cases.
	 */
	protected abstract void initConfigMap();

	/**
	 * if true this will take into account the case of the string passed
	 * otherwise it won't
	 * 
	 * @return if the config is case sensitive
	 */
	protected boolean caseSensitive() {
		return false;
	}

	/**
	 * adds the variable and the action to the config map. This will be called
	 * when the config option is found by passing the value to the consumer.
	 * 
	 * @param variable
	 *            variable in the config
	 * @param action
	 *            action to take with the value.
	 */
	protected void addConfigElement(String variable, Consumer<String> action) {
		// if not case sensitive put all the variables to lower case.
		if (!caseSensitive()) {
			variable = variable.toLowerCase();
		}

		// add the given values to the map.
		configMap.put(variable, action);
	}

	/**
	 * called to consume a config line with the variable and value found in the
	 * config.
	 * 
	 * @param variable
	 *            variable to find the config option.
	 * @param value
	 *            value to pass its corresponding consumer.
	 * @return true if there was success, or false if something went wrong.
	 */
	public boolean applyConfigOption(String variable, String value) {
		// if not case sensitive turn the variable to lower case
		if (!caseSensitive()) {
			variable = variable.toLowerCase();
		}
		
		// if the variable is in the config execute the command
		if (configMap.containsKey(variable)) {

			// get the consumer from the map
			Consumer<String> consumer = configMap.get(variable);

			// make sure the consumer isn't null, if it isn't use it and return
			// true otherwise return false.
			if (consumer != null) {
				consumer.accept(value);
				return true;
			}

			return false;

		} else {
			// otherwise return false
			return false;
		}
	}
}
