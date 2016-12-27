package com.ulfric.luckyscript.lang.enums;

public enum LoadPriority {


	INSTANT(0),
	HIGH(1),
	MEDIUM(2),
	LOW(3),
	RUNNABLE(4);

	private final int value;
	public int getValue() { return this.value; }

	LoadPriority(int next)
	{
		this.value = next;
	}

	public static LoadPriority next(int current)
	{
		current++;

		for (LoadPriority priority : LoadPriority.values())
		{
			if (priority.value != current) continue;

			return priority;
		}

		return null;
	}


}