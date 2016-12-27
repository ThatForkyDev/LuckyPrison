package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.math.Numbers;

final class PositiveIntegerArg extends IntegerArg {

	@Override
	public Integer match(String string)
	{
		Integer integer = Numbers.parseInteger(string);

		if (integer == null || integer <= 0) return null;

		return integer;
	}

}
