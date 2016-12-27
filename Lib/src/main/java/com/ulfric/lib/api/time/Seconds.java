package com.ulfric.lib.api.time;

public final class Seconds {

	public static final long YEAR = 31536000L;

	public static final long MONTH = 2592000L;

	public static final long WEEK = 604800L;

	public static final long DAY = 86400L;

	public static final long HOUR = 3600L;

	public static final long MINUTE = 60L;

	public static final long SECOND = 1L;

	static ISeconds impl = ISeconds.EMPTY;

	private Seconds()
	{
	}

	public static long fromMinutes(double minutes)
	{
		return impl.fromMinutes(minutes);
	}

	public static long fromTicks(long ticks)
	{
		return impl.fromTicks(ticks);
	}

	protected interface ISeconds {
		ISeconds EMPTY = new ISeconds() {
		};

		default long fromMinutes(double minutes)
		{
			return 0L;
		}

		default long fromTicks(long ticks)
		{
			return 0L;
		}
	}

}
