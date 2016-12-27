package com.ulfric.lib.api.task;

import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.List;

public final class Tasks {

	static ITasks impl = ITasks.EMPTY;

	private Tasks()
	{
	}

	public static BukkitTask run(Runnable runnable)
	{
		return impl.run(runnable);
	}

	public static BukkitTask runLater(Runnable runnable, long delayInTicks)
	{
		return impl.runLater(runnable, delayInTicks);
	}

	public static BukkitTask runLaterAsync(Runnable runnable, long delayInTicks)
	{
		return impl.runLaterAsync(runnable, delayInTicks);
	}

	public static BukkitTask runRepeating(Runnable runnable, long delayInTicks)
	{
		return impl.runRepeating(runnable, delayInTicks);
	}

	public static BukkitTask runRepeatingAsync(Runnable runnable, long delayInTicks)
	{
		return impl.runRepeating(runnable, delayInTicks);
	}

	public static BukkitTask runRepeating(Runnable runnable, long startDelayInTicks, long delayInTicks)
	{
		return impl.runRepeating(runnable, startDelayInTicks, delayInTicks);
	}

	public static BukkitTask runRepeatingAsync(Runnable runnable, long startDelayInTicks, long delayInTicks)
	{
		return impl.runRepeatingAsync(runnable, startDelayInTicks, delayInTicks);
	}

	public static BukkitTask runAsync(Runnable runnable)
	{
		return impl.runAsync(runnable);
	}

	public static List<BukkitTask> run(Collection<Runnable> runnables)
	{
		return impl.run(runnables);
	}

	public static List<BukkitTask> runSeparately(Collection<Runnable> runnables, long increment)
	{
		return impl.runSeparately(runnables, increment);
	}

	public static void cancel(BukkitTask task)
	{
		impl.cancel(task);
	}

	public static void cancel(int task)
	{
		impl.cancel(task);
	}

	public static BukkitTask cancelLater(int task, long delay)
	{
		return impl.cancelLater(task, delay);
	}

	public static void cancel(Collection<Integer> tasks)
	{
		impl.cancel(tasks);
	}

	public static List<BukkitTask> cancelSeparately(Collection<Integer> tasks, long increment)
	{
		return impl.cancelSeparately(tasks, increment);
	}

	public static TimedTask newTimedTask(Runnable runnable, long expiry)
	{
		return impl.newTimedTask(runnable, expiry);
	}

	protected interface ITasks {
		ITasks EMPTY = new ITasks() {
		};

		default BukkitTask run(Runnable runnable)
		{
			return null;
		}

		default BukkitTask runLater(Runnable runnable, long delayInTicks)
		{
			return null;
		}

		default BukkitTask runLaterAsync(Runnable runnable, long delayInTicks)
		{
			return null;
		}

		default BukkitTask runRepeating(Runnable runnable, long delayInTicks)
		{
			return null;
		}

		default BukkitTask runRepeatingAsync(Runnable runnable, long delayInTicks)
		{
			return null;
		}

		default BukkitTask runRepeating(Runnable runnable, long startDelayInTicks, long delayInTicks)
		{
			return null;
		}

		default BukkitTask runRepeatingAsync(Runnable runnable, long startDelayInTicks, long delayInTicks)
		{
			return null;
		}

		default BukkitTask runAsync(Runnable runnable)
		{
			return null;
		}

		default List<BukkitTask> run(Collection<Runnable> runnables)
		{
			return null;
		}

		default List<BukkitTask> runSeparately(Collection<Runnable> runnables, long increment)
		{
			return null;
		}

		default void cancel(BukkitTask task)
		{
		}

		default void cancel(int task)
		{
		}

		default BukkitTask cancelLater(int task, long delay)
		{
			return null;
		}

		default void cancel(Collection<Integer> tasks)
		{
		}

		default List<BukkitTask> cancelSeparately(Collection<Integer> tasks, long increment)
		{
			return null;
		}

		default TimedTask newTimedTask(Runnable runnable, long expiry)
		{
			return null;
		}
	}

}
