package com.ulfric.lib.api.world;

public final class Clock {


	static IClock impl = IClock.EMPTY;

	private Clock()
	{
	}

	public static TimeType parse(long time)
	{
		return impl.parse(time);
	}

	public static long parse(int hour)
	{
		return impl.parse(hour);
	}

	public static TimeType parse(String value)
	{
		return impl.parse(value);
	}

	protected interface IClock {
		IClock EMPTY = new IClock() {
		};

		default TimeType parse(long time)
		{
			return null;
		}

		default long parse(int hour)
		{
			return 0;
		}

		default TimeType parse(String value)
		{
			return null;
		}
	}


}
