package com.ulfric.ess.commands;

import com.ulfric.ess.tasks.TaskLagwatch;
import com.ulfric.lib.api.command.SimpleCommand;

public class CommandLagwatch extends SimpleCommand {

	public CommandLagwatch()
	{
		this.withEnforcePlayer();
	}

	@Override
	public void run()
	{
		TaskLagwatch.get().togglePlayer(this.getPlayer());
	}

}