package com.ulfric.prison.scoreboard;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

class ScoreboardUpdater extends Thread {

	public static final ScoreboardUpdater INSTANCE = new ScoreboardUpdater();

	private ScoreboardUpdater()
	{
		if (ScoreboardUpdater.INSTANCE == null) return;

		throw new IllegalStateException("Instance already exists!");
	}

	private Map<UUID, Runnable> tasks = Maps.newConcurrentMap();
	private Map<UUID, Runnable> teams = Maps.newConcurrentMap();

	@Override
	public void run()
	{
		while (ModuleScoreboard.INSTANCE.isModuleEnabled())
		{
			for (UUID uuid : this.tasks.keySet())
			{
				this.run(this.tasks, uuid);
			}

			for (UUID uuid : this.teams.keySet())
			{
				this.run(this.teams, uuid);
			}

			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException exception)
			{
				exception.printStackTrace();
			}
		}
	}

	private void run(Map<UUID, Runnable> map, UUID uuid)
	{
		if (uuid == null) return;

		Runnable runnable = map.remove(uuid);

		if (runnable == null) return;

		try
		{
			runnable.run();
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	public static void submitSidebar(UUID uuid, Runnable runnable)
	{
		ScoreboardUpdater.INSTANCE.tasks.put(uuid, runnable);
	}

	public static void submitTeam(UUID uuid, Runnable runnable)
	{
		ScoreboardUpdater.INSTANCE.teams.put(uuid, runnable);
	}

}