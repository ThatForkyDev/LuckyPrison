package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.math.Numbers;

final class LongArg extends NumberArg {

	@Override
	public Long match(String string)
	{
		return Numbers.parseLong(string);
	}

}
