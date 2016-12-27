package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.math.Numbers;

final class FloatArg extends NumberArg {


	@Override
	public Float match(String string)
	{
		return Numbers.parseFloat(string);
	}


}
