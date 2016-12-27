package com.ulfric.lib.api.task;

import com.google.common.collect.Lists;
import com.ulfric.lib.Lib;
import com.ulfric.lib.api.module.SimpleModule;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.List;

public final class TasksModule extends SimpleModule {

	public TasksModule()
	{
		super("tasks", "BukkitTask utilities module", "Packet", "1.0.0-REL");
	}

	@Override
	public void postEnable()
	{
		Tasks.impl = new Tasks.ITasks() {
			@Override
			public BukkitTask run(Runnable runnable)
			{
				return Bukkit.getScheduler().runTask(Lib.get(), runnable);
			}

			@Override
			public BukkitTask runLater(Runnable runnable, long delayInTicks)
			{
				return Bukkit.getScheduler().runTaskLater(Lib.get(), runnable, delayInTicks);
			}

			@Override
			public BukkitTask runLaterAsync(Runnable runnable, long delayInTicks)
			{
				return Bukkit.getScheduler().runTaskLaterAsynchronously(Lib.get(), runnable, delayInTicks);
			}

			@Override
			public BukkitTask runRepeating(Runnable runnable, long delayInTicks)
			{
				return Tasks.runRepeating(runnable, delayInTicks, delayInTicks);
			}

			@Override
			public BukkitTask runRepeatingAsync(Runnable runnable, long delayInTicks)
			{
				return Tasks.runRepeatingAsync(runnable, delayInTicks, delayInTicks);
			}

			@Override
			public BukkitTask runRepeating(Runnable runnable, long startDelayInTicks, long delayInTicks)
			{
				return Bukkit.getScheduler().runTaskTimer(Lib.get(), runnable, startDelayInTicks, delayInTicks);
			}

			@Override
			public BukkitTask runRepeatingAsync(Runnable runnable, long startDelayInTicks, long delayInTicks)
			{
				return Bukkit.getScheduler().runTaskTimerAsynchronously(Lib.get(), runnable, startDelayInTicks, delayInTicks);
			}

			@Override
			public BukkitTask runAsync(Runnable runnable)
			{
				return Bukkit.getScheduler().runTaskAsynchronously(Lib.get(), runnable);
			}

			@Override
			public List<BukkitTask> run(Collection<Runnable> runnables)
			{
				List<BukkitTask> tasks = Lists.newArrayList();

				for (Runnable runnable : runnables)
				{
					tasks.add(Tasks.run(runnable));
				}

				return tasks;
			}

			@Override
			public List<BukkitTask> runSeparately(Collection<Runnable> runnables, long increment)
			{
				List<BukkitTask> tasks = Lists.newArrayList();

				long base = 0;

				for (Runnable runnable : runnables)
				{
					tasks.add(Tasks.runLater(runnable, base));

					base += increment;
				}

				return tasks;
			}

			@Override
			public void cancel(BukkitTask task)
			{
				Tasks.cancel(task.getTaskId());
			}

			@Override
			public void cancel(int task)
			{
				Bukkit.getScheduler().cancelTask(task);
			}

			@Override
			public BukkitTask cancelLater(int task, long delay)
			{
				return Tasks.runLater(() -> Bukkit.getScheduler().cancelTask(task), delay);
			}

			@Override
			public void cancel(Collection<Integer> tasks)
			{
				for (int task : tasks)
				{
					Tasks.cancel(task);
				}
			}

			@Override
			public List<BukkitTask> cancelSeparately(Collection<Integer> tasks, long increment)
			{
				List<BukkitTask> cancelTasks = Lists.newArrayList();

				long base = 0;

				for (int task : tasks)
				{
					cancelTasks.add(Tasks.cancelLater(task, base));

					base += increment;
				}

				return cancelTasks;
			}

			@Override
			public TimedTask newTimedTask(Runnable runnable, long expiry)
			{
				return new TimedTask(runnable, expiry);
			}
		};
	}

	@Override
	public void postDisable()
	{
		Tasks.impl = Tasks.ITasks.EMPTY;
	}

}
