package com.ulfric.lib.api.time;

public final class Milliseconds {

	public static final long YEAR = 31536000000L;

	public static final long MONTH = 2592000000L;

	public static final long WEEK = 604800000L;

	public static final long DAY = 86400000L;

	public static final long HOUR = 3600000L;

	public static final long MINUTE = 60000L;

	public static final long SECOND = 1000L;

	static IMilliseconds impl = IMilliseconds.EMPTY;

	private Milliseconds()
	{
	}

	public static long fromTicks(long ticks)
	{
		return impl.fromTicks(ticks);
	}

	public static long fromSeconds(double seconds)
	{
		return impl.fromSeconds(seconds);
	}

	public static long fromSeconds(long seconds)
	{
		return impl.fromSeconds(seconds);
	}

	public static long fromMinutes(double minutes)
	{
		return impl.fromMinutes(minutes);
	}

	public static long fromHours(double hours)
	{
		return impl.fromHours(hours);
	}

	public static long toMinutes(long milliseconds)
	{
		return impl.toMinutes(milliseconds);
	}

	protected interface IMilliseconds {
		IMilliseconds EMPTY = new IMilliseconds() {
		};

		default long fromTicks(long ticks)
		{
			return 0L;
		}

		default long fromSeconds(double seconds)
		{
			return 0L;
		}

		default long fromSeconds(long seconds)
		{
			return 0L;
		}

		default long fromMinutes(double minutes)
		{
			return 0L;
		}

		default long fromHours(double hours)
		{
			return 0L;
		}

		default long toMinutes(long milliseconds)
		{
			return 0L;
		}
	}

}
