package com.ulfric.prison.achievements;

import com.ulfric.lib.api.module.SimpleModule;

public class PrisonAchievementsModule extends SimpleModule {

	public PrisonAchievementsModule()
	{
		super("prison-achievements", "Achievements for Prison", "Packet", "1.0.0-REL");

		this.withSubModule(new PrisonAchievementsMoveModule());
		this.withSubModule(new PrisonAchievementsPhysicalModule());
		this.withSubModule(new PrisonAchievementsMoneyModule());
		this.withSubModule(new PrisonAchievementsFightModule());
		this.withSubModule(new PrisonAchievementsMiningModule());
		this.withSubModule(new PrisonAchievementsMiscModule());
	}

}