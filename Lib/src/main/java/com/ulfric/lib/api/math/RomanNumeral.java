package com.ulfric.lib.api.math;

public final class RomanNumeral {

	static IRomanNumeral impl = IRomanNumeral.EMPTY;
	private final int number;
	private final String value;

	RomanNumeral(int number, String value)
	{
		this.number = number;

		this.value = value;
	}

	static RomanNumeral of(int number)
	{
		return impl.of(number);
	}

	static RomanNumeral of(String value)
	{
		return impl.of(value);
	}

	public int intValue()
	{
		return this.number;
	}

	@Override
	public String toString()
	{
		return this.value;
	}

	protected interface IRomanNumeral {
		IRomanNumeral EMPTY = new IRomanNumeral() {
		};

		default RomanNumeral of(int number)
		{
			return null;
		}

		default RomanNumeral of(String value)
		{
			return null;
		}
	}

}
