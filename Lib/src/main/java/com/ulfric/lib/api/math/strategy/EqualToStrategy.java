package com.ulfric.lib.api.math.strategy;

enum EqualToStrategy implements MathStrategy {

	INSTANCE;

	@Override
	public boolean matches(short o1, short o2)
	{
		return o1 == o2;
	}

	@Override
	public boolean matches(int o1, int o2)
	{
		return o1 == o2;
	}

	@Override
	public boolean matches(long o1, long o2)
	{
		return o1 == o2;
	}

	@Override
	public boolean matches(double o1, double o2)
	{
		return Double.compare(o1, o2) == 0;
	}

	@Override
	public boolean matches(float o1, float o2)
	{
		return Float.compare(o1, o2) == 0;
	}
}
