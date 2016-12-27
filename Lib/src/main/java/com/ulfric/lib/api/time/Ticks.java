package com.ulfric.lib.api.time;

public final class Ticks {

	public static final long HOUR = 72000;
	public static final long MINUTE = 1200;
	public static final long SECOND = 20;

	static ITicks impl = ITicks.EMPTY;

	private Ticks()
	{
	}

	public static long fromMilliseconds(long milliseconds)
	{
		return impl.fromMilliseconds(milliseconds);
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

	protected interface ITicks {
		ITicks EMPTY = new ITicks() {
		};

		default long fromMilliseconds(long milliseconds)
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
	}

}
