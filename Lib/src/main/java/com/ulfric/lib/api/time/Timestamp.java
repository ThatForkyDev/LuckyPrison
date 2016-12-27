package com.ulfric.lib.api.time;

public interface Timestamp {

	static Timestamp infinite()
	{
		return TimestampAPI.impl.infinite();
	}

	static Timestamp now()
	{
		return TimestampAPI.impl.now();
	}

	static Timestamp of(long time)
	{
		return TimestampAPI.impl.of(time);
	}

	static Timestamp future(long future)
	{
		return TimestampAPI.impl.future(future);
	}

	long getTime();

	void setTime(long time);

	void incTime(long time);

	default void setFuture(long future)
	{
		this.setTime(System.currentTimeMillis() + future);
	}

	default boolean isFinite()
	{
		return this.getTime() != -1;
	}

	default boolean hasPassed()
	{
		return this.isFinite() && this.timeSince() > 0;
	}

	default void setTimeNow()
	{
		this.setTime(System.currentTimeMillis());
	}

	default long timeSince()
	{
		return TimeUtils.millisecondsSince(this.getTime());
	}

	default long timeTill()
	{
		if (!this.isFinite()) return -1;

		return Math.abs(TimeUtils.millisecondsTill(this.getTime()));
	}

	final class TimestampAPI {
		static ITimestamp impl = ITimestamp.EMPTY;

		TimestampAPI()
		{
		}

		protected interface ITimestamp {
			ITimestamp EMPTY = new ITimestamp() {
			};

			default Timestamp now()
			{
				return null;
			}

			default Timestamp of(long time)
			{
				return null;
			}

			default Timestamp future(long future)
			{
				return null;
			}

			default Timestamp infinite()
			{
				return null;
			}
		}
	}

}
