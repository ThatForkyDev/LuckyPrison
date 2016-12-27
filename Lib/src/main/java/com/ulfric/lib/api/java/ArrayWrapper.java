package com.ulfric.lib.api.java;

@SuppressWarnings("unchecked")
public final class ArrayWrapper<T> {

	private final T[] array;

	public ArrayWrapper(int size)
	{
		this.array = (T[]) new Object[size];
	}

	public ArrayWrapper(T[] array)
	{
		this.array = array;
	}

	public void set(int index, T value)
	{
		this.array[index] = value;
	}

	public void clear(int index)
	{
		this.set(index, null);
	}

	public void clear()
	{
		for (int x = 0; x < this.array.length; x++)
		{
			this.clear(x);
		}
	}

	public T get(int index)
	{
		return this.array[index];
	}

	public T[] getArray()
	{
		return this.array;
	}

}
