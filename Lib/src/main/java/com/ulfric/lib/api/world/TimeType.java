package com.ulfric.lib.api.world;

public enum TimeType {

	DAY("day", 1000),
	MIDDAY("mid-day", 6000),
	SUNRISE("sunrise", 0),
	SUNSET("sunset", 12000),
	NIGHT("night", 14000),
	MIDNIGHT("midnight", 18000),
	EARLY("early morning", 22000);

	private final String value;
	private final int time;

	TimeType(String value, int time)
	{
		this.value = value;
		this.time = time;
	}

	public String get()
	{
		return this.value;
	}

	public int getTime()
	{
		return this.time;
	}

}
