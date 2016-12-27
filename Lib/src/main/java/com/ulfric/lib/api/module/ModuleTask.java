package com.ulfric.lib.api.module;

import com.ulfric.lib.api.java.Annihilate;
import com.ulfric.lib.api.task.ATask;

public class ModuleTask implements Annihilate {

	private final ATask task;
	private final StartType type;

	public ModuleTask(ATask task, StartType type)
	{
		this.task = task;

		this.type = type;
	}

	public ATask getTask()
	{
		return this.task;
	}

	public void start()
	{
		if (this.task.isRunning()) return;

		this.task.start();
	}

	@Override
	public void annihilate()
	{
		if (!this.task.isRunning()) return;

		this.task.annihilate();
	}

	public StartType getType()
	{
		return this.type;
	}

	public enum StartType {
		MANUAL,
		AUTOMATIC
	}

}
