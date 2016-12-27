package com.ulfric.lib.api.time;

public final class TimeUtils {

	static ITimeUtils impl = ITimeUtils.EMPTY;

	private TimeUtils()
	{
	}

	public static String formatCurrentDay()
	{
		return impl.formatCurrentDay();
	}

	public static String formatCurrentTime()
	{
		return impl.formatCurrentTime();
	}

	public static String formatCurrentDateFully()
	{
		return impl.formatCurrentDateFully();
	}

	public static String millisecondsToString(long time)
	{
		return impl.millisecondsToString(time);
	}

	public static String millisecondsToString(long time, boolean small)
	{
		return impl.millisecondsToString(time, small);
	}

	public static String millisecondsToString(long time, boolean small, boolean abs)
	{
		return impl.millisecondsToString(time, small, abs);
	}

	public static String secondsToString(long time)
	{
		return impl.secondsToString(time);
	}

	public static String secondsToString(long time, boolean small, boolean abs)
	{
		return impl.secondsToString(time, small, abs);
	}

	public static long millisecondsTill(long time)
	{
		return impl.millisecondsTill(time);
	}

	public static long millisecondsSince(long time)
	{
		return impl.millisecondsSince(time);
	}

	public static long secondsSince(long time)
	{
		return impl.secondsSince(time);
	}

	public static long parseSeconds(String time)
	{
		return impl.parseSeconds(time);
	}

	public static long parseSeconds(String time, long badValue)
	{
		return impl.parseSeconds(time, badValue);
	}

	public static String month()
	{
		return impl.month();
	}

	protected interface ITimeUtils {
		ITimeUtils EMPTY = new ITimeUtils() {
		};

		default String formatCurrentDay()
		{
			return null;
		}

		default String formatCurrentTime()
		{
			return null;
		}

		default String formatCurrentDateFully()
		{
			return null;
		}

		default String millisecondsToString(long time)
		{
			return null;
		}

		default String millisecondsToString(long time, boolean small)
		{
			return null;
		}

		default String millisecondsToString(long time, boolean small, boolean abs)
		{
			return null;
		}

		default String secondsToString(long time)
		{
			return null;
		}

		default String secondsToString(long time, boolean small, boolean abs)
		{
			return null;
		}

		default long millisecondsTill(long time)
		{
			return 0L;
		}

		default long millisecondsSince(long time)
		{
			return 0L;
		}

		default long secondsSince(long time)
		{
			return 0L;
		}

		default long parseSeconds(String time)
		{
			return 0L;
		}

		default long parseSeconds(String time, long badValue)
		{
			return 0L;
		}

		default String month()
		{
			return null;
		}
	}

}
