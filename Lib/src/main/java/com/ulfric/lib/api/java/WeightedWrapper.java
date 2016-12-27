package com.ulfric.lib.api.java;

public final class WeightedWrapper<T> extends Wrapper<T> implements Weighted {

	private final int weight;

	public WeightedWrapper(int weight, T value)
	{
		super(value);

		this.weight = weight;
	}

	@Override
	public int getWeight()
	{
		return this.weight;
	}

}
