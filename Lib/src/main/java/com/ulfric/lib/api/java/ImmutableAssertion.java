package com.ulfric.lib.api.java;

import java.util.function.Function;

public final class ImmutableAssertion<T> extends Assertion<T> {

	private volatile boolean locked;

	ImmutableAssertion(T object, boolean nullable)
	{
		super(object, nullable);
	}

	@Override
	public Assertion<T> addFunction(Function<T, Boolean> function)
	{
		Assert.isFalse(this.locked);

		return super.addFunction(function);
	}

	@Override
	public boolean test()
	{
		this.locked = true;

		return super.test();
	}

}
