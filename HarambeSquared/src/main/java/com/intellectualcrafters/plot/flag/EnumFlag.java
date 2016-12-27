package com.intellectualcrafters.plot.flag;

import java.util.Arrays;
import java.util.HashSet;

import com.intellectualcrafters.plot.util.StringMan;

public class EnumFlag extends Flag<String> {
	private final HashSet<String> values;

	public EnumFlag(String name, String... values)
	{
		super(name);
		this.values = new HashSet<>(Arrays.asList(values));
	}

	@Override
	public String valueToString(Object value)
	{
		return value.toString();
	}

	@Override public String parseValue(String value)
	{
		value = value.toLowerCase();
		if (this.values.contains(value))
		{
			return value;
		}
		return null;
	}

	@Override public String getValueDescription()
	{
		return "Must be one of: " + StringMan.getString(this.values);
	}
}
