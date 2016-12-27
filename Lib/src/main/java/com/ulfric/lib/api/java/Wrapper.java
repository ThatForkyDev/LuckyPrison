package com.ulfric.lib.api.java;

import java.util.Objects;

public class Wrapper<T> {

	private T value;

	public Wrapper()
	{
	}

	public Wrapper(T value)
	{
		this.value = value;
	}

	public T getValue()
	{
		return this.value;
	}

	public void setValue(T value)
	{
		this.value = value;
	}

	public boolean isEmpty()
	{
		return this.value == null;
	}

	@Override
	public boolean equals(Object object)
	{
		if (this == object) return true;

		if (object instanceof Wrapper)
		{
			Wrapper<?> wrapper = (Wrapper<?>) object;
			return Objects.equals(this.value, wrapper.value);
		}

		return Objects.equals(this.value, object);
	}

	@Override
	public int hashCode()
	{
		return this.value != null ? this.value.hashCode() : 0;
	}
}
