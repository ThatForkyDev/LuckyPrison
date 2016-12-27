package com.ulfric.control.entity.enums;

import com.ulfric.lib.api.time.Ticks;

public enum ViolationType {

	NO_MOVE_CHAT(3, 0.54F, 14),
	FAST_CHAT(3, 0.927F, 8),
	SAME_MESSAGE(2, 1.17F, 25),
	SAME_MESSAGE_GLOBAL(2, 0.97F, 20),
	FAST_CHAT_GLOBAL(1, 0.34F, 10),
	FAST_CHAT_LOCAL(1, 1.15F, 20),
	RAPID_LOGIN(1, 2.05F, 34),
	MAX_LOGIN(1, 2.12F, 60),
	LOCKDOWN_CHAT(3, 1.067F, 26),
	MUTED_CHAT(2, 2.04F, 20),
	ADVERTISING(1, 5, 100);

	private final int maxLevel;
	public int getMaxLevel() { return this.maxLevel; }

	private final float totalWorth;
	public float getTotalWorth() { return this.totalWorth; }

	private final long secondsTillReset;
	public long getSecondsTillReset() { return this.secondsTillReset; }

	ViolationType(int maxLevel, float totalWorth, int secondsTillReset)
	{
		this.maxLevel = maxLevel;

		this.totalWorth = totalWorth;

		this.secondsTillReset = Ticks.fromSeconds(secondsTillReset);
	}

}