package com.ulfric.lib.api.math;

public final class NumberTranslator {

	static INumberTranslator impl = INumberTranslator.EMPTY;

	private NumberTranslator()
	{
	}

	public static RomanNumeral roman(int number)
	{
		return impl.roman(number);
	}

	public static RomanNumeral roman(String value)
	{
		return impl.roman(value);
	}

	protected interface INumberTranslator {
		INumberTranslator EMPTY = null;

		default RomanNumeral roman(int number)
		{
			return null;
		}

		default RomanNumeral roman(String value)
		{
			return null;
		}
	}

}
