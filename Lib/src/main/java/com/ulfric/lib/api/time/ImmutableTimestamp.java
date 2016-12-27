package com.ulfric.lib.api.time;

public final class ImmutableTimestamp implements Timestamp {


	private final long time;

	ImmutableTimestamp()
	{
		this(System.currentTimeMillis());
	}

	ImmutableTimestamp(long time)
	{
		this.time = time;
	}

	@Override
	public long getTime()
	{
		return this.time;
	}

	@Override
	public void setTime(long time)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void incTime(long time)
	{
		throw new UnsupportedOperationException();
	}


}
