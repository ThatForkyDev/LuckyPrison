package com.ulfric.luckyscript.lang;

import com.ulfric.lib.api.command.arg.ArgStrategy;

public enum ScriptArg implements ArgStrategy<Script> {

	INSTANCE;

	@Override
	public Script match(String string)
	{
		return Scripts.getScript(string);
	}

}