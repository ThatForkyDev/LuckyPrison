package com.ulfric.lib.api.task;

import com.ulfric.lib.api.java.Annihilate;

public abstract class ATask implements Task, Annihilate {

	private boolean running;
	private int taskId;

	public void start()
	{
		if (this.running)
		{
			throw new TaskAlreadyRunningException();
		}

		this.running = true;
	}

	@Override
	public void annihilate()
	{
		if (!this.running)
		{
			throw new TaskNotRunningException();
		}

		this.running = false;

		Tasks.cancel(this.taskId);

		this.taskId = 0;
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

	public int getTaskId()
	{
		return this.taskId;
	}

	public void setTaskId(int taskId)
	{
		this.taskId = taskId;
	}

}
