package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.math.Numbers;

class IntegerArg extends NumberArg {

	@Override
	public Integer match(String string)
	{
		return Numbers.parseInteger(string);
	}

}