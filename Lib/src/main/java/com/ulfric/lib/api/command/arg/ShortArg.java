package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.math.Numbers;

final class ShortArg extends NumberArg {

	@Override
	public Short match(String string)
	{
		return Numbers.parseShort(string);
	}

}
