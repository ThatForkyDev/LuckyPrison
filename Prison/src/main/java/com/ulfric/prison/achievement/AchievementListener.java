package com.ulfric.prison.achievement;

import java.util.function.Supplier;

import org.bukkit.event.Listener;

public class AchievementListener implements Listener, Supplier<Achievement> {

	private Achievement achievement;
	public void supply(Achievement achievement)
	{
		this.achievement = achievement;
	}
	@Override
	public Achievement get()
	{
		return this.achievement;
	}

}