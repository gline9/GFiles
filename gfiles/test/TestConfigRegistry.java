package gfiles.test;

import gfiles.text.ConfigRegistry;

public class TestConfigRegistry extends ConfigRegistry {

	public String x;
	public String y;
	public int z;

	@Override
	protected void initConfigMap() {
		addConfigElement("x", (String value) -> x = value);
		addConfigElement("y", (String value) -> y = value);
		addConfigElement("z", (String value) -> z = Integer.valueOf(value));
	}

}
