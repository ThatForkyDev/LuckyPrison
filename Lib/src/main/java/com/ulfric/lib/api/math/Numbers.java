package com.ulfric.lib.api.math;

public final class Numbers {


	static INumbers impl = INumbers.EMPTY;

	private Numbers()
	{
	}

	public static boolean isDouble(Number number)
	{
		return impl.isDouble(number);
	}

	public static long roundUp(long number, long multiple)
	{
		return impl.roundUp(number, multiple);
	}

	public static double percentage(Number percent, Number total)
	{
		return impl.percentage(percent, total);
	}

	public static String firstSection(Number number)
	{
		return impl.firstSection(number);
	}

	public static String sectionAt(Number number, int row)
	{
		return impl.sectionAt(number, row);
	}

	public static Long parseLong(String number)
	{
		return impl.parseLong(number);
	}

	public static Integer parseInteger(String number)
	{
		return impl.parseInteger(number);
	}

	public static Short parseShort(String number)
	{
		return impl.parseShort(number);
	}

	public static Byte parseByte(String number)
	{
		return impl.parseByte(number);
	}

	public static Float parseFloat(String number)
	{
		return impl.parseFloat(number);
	}

	public static Double parseDouble(String number)
	{
		return impl.parseDouble(number);
	}

	public static Number parseNumber(String number)
	{
		return impl.parseNumber(number);
	}

	public static boolean isWithin(int i1, int i2, int max)
	{
		return impl.isWithin(i1, i2, max);
	}

	protected interface INumbers {
		INumbers EMPTY = new INumbers() {
		};

		default boolean isDouble(Number number)
		{
			return false;
		}

		default long roundUp(long number, long multiple)
		{
			return 0L;
		}

		default double percentage(Number percent, Number total)
		{
			return 0.0D;
		}

		default String firstSection(Number number)
		{
			return null;
		}

		default String sectionAt(Number number, int row)
		{
			return null;
		}

		default Long parseLong(String number)
		{
			return null;
		}

		default Integer parseInteger(String number)
		{
			return null;
		}

		default Short parseShort(String number)
		{
			return null;
		}

		default Byte parseByte(String number)
		{
			return null;
		}

		default Float parseFloat(String number)
		{
			return null;
		}

		default Double parseDouble(String number)
		{
			return null;
		}

		default Number parseNumber(String number)
		{
			return null;
		}

		default boolean isWithin(int i1, int i2, int max)
		{
			return false;
		}
	}

}
