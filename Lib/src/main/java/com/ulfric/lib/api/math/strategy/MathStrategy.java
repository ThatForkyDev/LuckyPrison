package com.ulfric.lib.api.math.strategy;

public interface MathStrategy {

	MathStrategy EQUAL = EqualToStrategy.INSTANCE;
	MathStrategy LESS_THAN = LessthanStrategy.INSTANCE;
	MathStrategy LESS_THAN_OR_EQUAL = LessthanEqualtoStrategy.INSTANCE;
	MathStrategy GREATER_THAN = GreaterthanStrategy.INSTANCE;
	MathStrategy GREATER_THAN_OR_EQUAL = GreaterthanEqualtoStrategy.INSTANCE;

	static MathStrategy parse(String string)
	{
		return IMath.impl.parse(string);
	}

	boolean matches(short o1, short o2);

	boolean matches(int o1, int o2);

	boolean matches(long o1, long o2);

	boolean matches(double o1, double o2);

	boolean matches(float o1, float o2);

	final class IMath {
		static IMathStrategy impl = IMathStrategy.EMPTY;

		private IMath()
		{
		}

		protected interface IMathStrategy {
			IMathStrategy EMPTY = new IMathStrategy() {
			};

			default MathStrategy parse(String string)
			{
				return null;
			}
		}
	}

}
