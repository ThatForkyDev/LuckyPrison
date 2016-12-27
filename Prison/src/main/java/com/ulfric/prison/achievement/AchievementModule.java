package com.ulfric.prison.achievement;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.server.Listeners;
import com.ulfric.prison.achievements.PrisonAchievementsModule;

public class AchievementModule extends SimpleModule {

	private static final AchievementModule INSTANCE = new AchievementModule();

	public static AchievementModule get()
	{
		return AchievementModule.INSTANCE;
	}

	public static void registerListener(AchievementListener listener)
	{
		Listeners.register(AchievementModule.get(), listener);
	}

	private AchievementModule()
	{
		super("achievement", "Achievements module", "Packet", "1.0.0-REL");

		this.withSubModule(new PrisonAchievementsModule());

		this.addCommand("achievements", new AchievementsCommand());
	}

	private class AchievementsCommand extends SimpleCommand
	{
		private AchievementsCommand()
		{
			this.withEnforcePlayer();
		}

		@Override
		public void run()
		{
			AchievementCategoryPanel.create(this.getPlayer());
		}
	}

}