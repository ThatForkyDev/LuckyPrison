package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.math.Numbers;

final class ByteArg extends NumberArg {


	@Override
	public Byte match(String string)
	{
		return Numbers.parseByte(string);
	}


}
