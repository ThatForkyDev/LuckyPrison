package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.math.Numbers;

final class DoubleArg extends NumberArg {


	@Override
	public Double match(String string)
	{
		return Numbers.parseDouble(string);
	}


}
