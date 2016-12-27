package com.ulfric.ess.commands;

import org.bukkit.Bukkit;

import com.ulfric.ess.tasks.TaskReboot;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ExactArg;

public class CommandReboot extends SimpleCommand {

	public CommandReboot()
	{
		this.withArgument("hard", new ExactArg("--hard"), "ess.reboot.hard", "lib.node_err");
	}

	@Override
	public void run()
	{
		if (this.hasObject("hard"))
		{
			Bukkit.shutdown();

			return;
		}

		TaskReboot.get().rebootNow();
	}

}