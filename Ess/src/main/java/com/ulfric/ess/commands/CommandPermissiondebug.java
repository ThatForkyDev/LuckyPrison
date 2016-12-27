package com.ulfric.ess.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.locale.Locale;

public class CommandPermissiondebug extends SimpleCommand {

	public CommandPermissiondebug()
	{
		this.withEnforcePlayer();
	}

	@Override
	public void run()
	{
		Player player = this.getPlayer();

		boolean flag = !player.isNotifyNoPerms();

		player.setNotifyNoPerms(flag);

		Locale.sendSuccess(player, "ess.permission_debug", flag);
	}

}