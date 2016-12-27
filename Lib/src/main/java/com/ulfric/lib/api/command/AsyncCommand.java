package com.ulfric.lib.api.command;

import com.ulfric.lib.api.task.Tasks;

public abstract class AsyncCommand extends Command implements Runnable, EnforceableCommand {

	@Override
	public boolean dispatch()
	{
		Tasks.runAsync(this);

		return true;
	}

}