package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.math.Numbers;

final class IntegerRangeArg extends NumberArg {

	private final int max;
	private final int min;

	private IntegerRangeArg(int max, int min)
	{
		this.max = max;
		this.min = min;
	}

	public static IntegerRangeArg of(int max, int min)
	{
		return new IntegerRangeArg(max, min);
	}

	@Override
	public Integer match(String string)
	{
		Integer number = Numbers.parseInteger(string);
		if (number == null) return null;

		int val = number;
		if (val > this.max || val < this.min) return null;

		return number;
	}

}
