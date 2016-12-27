package com.ulfric.lib.api.command.arg;

class StringArg implements ArgStrategy<String> {


	@Override
	public String match(String string)
	{
		return string.toLowerCase();
	}


}