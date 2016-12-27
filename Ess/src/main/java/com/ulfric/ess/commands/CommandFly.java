package com.ulfric.ess.commands;

import org.bukkit.entity.Player;

import com.ulfric.ess.lang.Permissions;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.locale.Locale;

public class CommandFly extends SimpleCommand {


	public CommandFly()
	{
		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, Permissions.FLY_OTHERS);
	}


	@Override
	public void run()
	{
		Player player = (Player) this.getObject("player");

		boolean flag = !player.getAllowFlight();
		player.setAllowFlight(flag);

		if (flag)
		{
			Metadata.applyNull(player, "_ulf_allowfly");
		}
		else
		{
			Metadata.remove(player, "_ulf_allowfly");
		}

		Locale.sendSuccess(this.getSender(), "ess.fly", player.getName());
	}


}