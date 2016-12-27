package com.intellectualcrafters.plot.flag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.intellectualcrafters.plot.util.StringMan;

public class StringListFlag extends ListFlag<List<String>> {

	public StringListFlag(String name)
	{
		super(name);
	}

	@Override public String valueToString(Object value)
	{
		return StringMan.join((List<String>) value, ",");
	}

	@Override public List<String> parseValue(String value)
	{
		return new ArrayList<>(Arrays.asList(value.split(",")));
	}

	@Override public String getValueDescription()
	{
		return "Flag value must be a string list";
	}
}
