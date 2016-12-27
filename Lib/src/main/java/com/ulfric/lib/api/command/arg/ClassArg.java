package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.reflect.Reflect;

final class ClassArg implements ArgStrategy<Class<?>> {


	@Override
	public Class<?> match(String string)
	{
		return Reflect.getClass(string);
	}


}
