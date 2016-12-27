package com.ulfric.lib.api.java;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class Assertion<T> implements Predicate<T>, Runnable {

	private final boolean nullable;
	private T object;
	private List<Function<T, Boolean>> functions;

	protected Assertion(T object, boolean nullable)
	{
		this.object = object;

		this.nullable = nullable;
	}

	public boolean isNullable()
	{
		return this.nullable;
	}

	public void setObject(T object)
	{
		if (this.nullable)
		{
			this.object = object;

			return;
		}

		this.object = Assert.notNull(object);
	}

	public Assertion<T> addFunction(Function<T, Boolean> function)
	{
		if (this.functions == null) this.functions = Lists.newArrayList();

		this.functions.add(function);

		return this;
	}

	public boolean test()
	{
		for (Function<T, Boolean> function : this.functions)
		{
			if (function.apply(this.object)) continue;

			return false;
		}

		return true;
	}

	@Override
	public boolean test(T object)
	{
		T old = this.object;

		this.setObject(object);

		boolean result = this.test();

		this.object = old;

		return result;
	}

	@Override
	public void run()
	{
		Assert.isTrue(this.test());
	}

	public T orElse(T other)
	{
		return this.test() ? this.object : other;
	}

	public Assertion<T> notNull()
	{
		this.addFunction(Objects::nonNull);

		return this;
	}

	public Assertion<T> isNull()
	{
		this.addFunction(Objects::isNull);

		return this;
	}

	public Assertion<T> isSame(Object object)
	{
		this.addFunction(obj -> obj.equals(object));

		return this;
	}

	public Assertion<T> isNotSame(Object object)
	{
		this.addFunction(obj -> !obj.equals(object));

		return this;
	}

	public Assertion<T> isSameHashcode(Object object)
	{
		this.addFunction(obj -> Objects.hashCode(obj) == Objects.hashCode(object));

		return this;
	}

	public Assertion<T> isTrue()
	{
		this.addFunction(Booleans::isTrue);

		return this;
	}

	public Assertion<T> isFalse()
	{
		this.addFunction(Booleans::isFalse);

		return this;
	}

}
