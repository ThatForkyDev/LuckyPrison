package com.ulfric.data.commands;

import org.bukkit.OfflinePlayer;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.hook.Hooks;

public class CommandDelete extends SimpleCommand {

	public CommandDelete()
	{
		this.withArgument(Argument.REQUIRED_OFFLINE_PLAYER);
		this.withArgument("path", ArgStrategy.ENTERED_STRING, "data.delete_path");
	}

	@Override
	public void run()
	{
		OfflinePlayer player = (OfflinePlayer) this.getObject("player");

		Hooks.DATA.getPlayerData(player.getUniqueId()).remove((String) this.getObject("path"));
	}

}