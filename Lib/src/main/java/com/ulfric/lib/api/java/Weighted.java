package com.ulfric.lib.api.java;

public interface Weighted {

	int getWeight();

	default void setWeight(int weight)
	{
		throw new UnsupportedOperationException();
	}

}