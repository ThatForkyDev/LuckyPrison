package com.ulfric.lib.api.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface FunctionalCommand extends CommandExecutor {

	void execute(CommandSender sender);

	@Override
	default boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args)
	{
		this.execute(sender);

		return true;
	}

}