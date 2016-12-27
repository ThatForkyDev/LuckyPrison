package com.ulfric.ess.tasks;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.ulfric.lib.api.collect.Sets;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.task.ATask;
import com.ulfric.lib.api.task.Tasks;

public class TaskLagwatch extends ATask {

	private static final TaskLagwatch INSTANCE = new TaskLagwatch();
	public static TaskLagwatch get() { return TaskLagwatch.INSTANCE; }

	private TaskLagwatch() { }

	private Set<Player> set = Sets.newWeakSet();
	public void togglePlayer(Player player)
	{
		if (this.set.remove(player))
		{
			if (!this.set.isEmpty()) return;

			this.annihilate();

			return;
		}

		this.set.add(player);

		if (this.isRunning()) return;

		this.start();
	}

	@Override
	public void start()
	{
		super.start();

		this.setTaskId(Tasks.runRepeatingAsync(this, 1).getTaskId());
	}

	@Override
	public void run()
	{
		if (this.set.isEmpty())
		{
			this.annihilate();

			return;
		}

		String tick = "Tick: " + Bukkit.getTick();

		for (Player player : this.set)
		{
			PlayerUtils.sendActionBar(player, tick);
		}
	}

}