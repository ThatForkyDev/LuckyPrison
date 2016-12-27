package com.intellectualcrafters.plot.flag;

import java.util.ArrayList;
import java.util.List;

import com.intellectualcrafters.plot.util.StringMan;

public class IntegerListFlag extends ListFlag<List<Integer>> {

	public IntegerListFlag(String name)
	{
		super(name);
	}

	@Override public String valueToString(Object value)
	{
		return StringMan.join((List<Integer>) value, ",");
	}

	@Override public List<Integer> parseValue(String value)
	{
		String[] split = value.split(",");
		ArrayList<Integer> numbers = new ArrayList<>();
		for (String element : split)
		{
			numbers.add(Integer.parseInt(element));
		}
		return numbers;
	}

	@Override public String getValueDescription()
	{
		return "Flag value must be a integer list";
	}
}
