package com.ulfric.lib.api.java;

public final class NamedWrapper<T> extends Wrapper<T> implements Named {

	private final String name;

	public NamedWrapper(String name, T value)
	{
		super(value);

		this.name = name;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

}
