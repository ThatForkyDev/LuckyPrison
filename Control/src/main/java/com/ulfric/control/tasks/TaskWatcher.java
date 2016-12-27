package com.ulfric.control.tasks;

import java.util.Set;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.collect.Sets;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.math.RandomUtils;
import com.ulfric.lib.api.player.IPlayer;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.task.ATask;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Ticks;

public class TaskWatcher extends ATask implements IPlayer {

	private static Set<ATask> tasks;

	public static void cancelAll()
	{
		if (CollectUtils.isEmpty(TaskWatcher.tasks)) return;

		for (ATask task : TaskWatcher.tasks)
		{
			task.annihilate();
		}
	}

	public TaskWatcher(Player player, int delay)
	{
		this.player = player;

		this.delay = delay;
	}

	private final Player player;
	@Override
	public Player getPlayer() { return this.player; }

	private int delay;
	public int getDelay() { return this.delay; }
	public void setDelay(int delay) { this.delay = delay; }

	@Override
	public void start()
	{
		super.start();

		if (TaskWatcher.tasks == null) TaskWatcher.tasks = Sets.newHashSet();

		TaskWatcher.tasks.add(this);

		this.setTaskId(Tasks.runRepeatingAsync(this, Ticks.fromSeconds(this.getDelay())).getTaskId());
	}

	private int teleports = 0;
	public int getTeleports() { return this.teleports; }

	@Override
	public void run()
	{
		Player target = this.refresh(PlayerUtils.getOnlinePlayersWithPermission("control.bypass.watcher", false));

		if (target == null)
		{
			Locale.sendError(this.getPlayer(), "control.watcher_not_enough");

			return;
		}

		Locale.send(this.getPlayer(), "control.watcher_teleport", target.getName());

		Tasks.run(() -> this.getPlayer().teleport(target));

		this.teleports++;
	}

	private Player last;

	private Player refresh(Set<Player> players)
	{
		if (players.size() < 3) return null;

		Player target = RandomUtils.randomValueFromCollection(players);

		if (this.last != null && target.equals(this.last)) return this.refresh(players);

		this.last = target;

		return target;
	}

	@Override
	public void annihilate()
	{
		super.annihilate();

		TaskWatcher.tasks.remove(this);
	}


}