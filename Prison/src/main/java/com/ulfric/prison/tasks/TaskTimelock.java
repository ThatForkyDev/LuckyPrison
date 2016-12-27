package com.ulfric.prison.tasks;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.ulfric.lib.api.task.ATask;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Ticks;

public class TaskTimelock extends ATask {

	private static final TaskTimelock INSTANCE = new TaskTimelock();
	public static TaskTimelock get() { return TaskTimelock.INSTANCE; }

	private TaskTimelock() { }

	@Override
	public void start()
	{
		super.start();

		this.setTaskId(Tasks.runRepeating(this, Ticks.fromSeconds(30)).getTaskId());
	}

	@Override
	public void run()
	{
		for (World world : Bukkit.getWorlds())
		{
			world.setTime(5500L);
		}
	}

}