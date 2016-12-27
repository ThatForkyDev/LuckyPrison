package com.ulfric.lib.api.time;

public final class MutableTimestamp implements Timestamp {


	private long time;

	MutableTimestamp()
	{
		this(System.currentTimeMillis());
	}

	MutableTimestamp(long time)
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
		this.time = time;
	}

	@Override
	public void incTime(long time)
	{
		this.time += time;
	}


}
