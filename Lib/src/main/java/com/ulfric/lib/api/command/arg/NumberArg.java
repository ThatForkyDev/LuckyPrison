package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.math.Numbers;

class NumberArg implements ArgStrategy<Number> {

	@Override
	public Number match(String string)
	{
		return Numbers.parseNumber(string);
	}

}