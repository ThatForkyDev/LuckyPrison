package com.ulfric.lib.api.task;

import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.time.Milliseconds;
import com.ulfric.lib.api.time.Ticks;
import com.ulfric.lib.api.time.Timestamp;

public final class TimedTask implements Task {

	private final Runnable runnable;
	private final Timestamp time;
	private int taskId;
	private boolean running;

	TimedTask(Runnable runnable, long expiry)
	{
		this.runnable = runnable;

		this.taskId = Tasks.runLater(this.runnable, expiry).getTaskId();

		this.time = Timestamp.future(Milliseconds.fromTicks(expiry));
	}

	public Runnable getRunnable()
	{
		return this.runnable;
	}

	public int getTask()
	{
		return this.taskId;
	}

	public void addTime(long ticks)
	{
		this.time.incTime(Milliseconds.fromTicks(ticks));

		Tasks.cancel(this.taskId);

		if (this.time.hasPassed())
		{
			Tasks.run(this.runnable);

			return;
		}

		this.taskId = Tasks.runLater(this.runnable, Ticks.fromMilliseconds(this.time.timeTill())).getTaskId();
	}

	@Override
	public boolean isRunning()
	{
		return this.running;
	}

	@Override
	public void setRunning(boolean running)
	{
		this.running = running;
	}

	@Override
	public void run()
	{
		Assert.isFalse(this.running);

		this.running = true;

		this.runnable.run();

		this.running = false;
	}

}
