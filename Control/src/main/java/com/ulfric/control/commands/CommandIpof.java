package com.ulfric.control.commands;

import org.bukkit.entity.Player;

import com.ulfric.control.coll.IPCache;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.locale.Locale;

public class CommandIpof extends SimpleCommand {

	public CommandIpof()
	{
		this.withArgument(Argument.REQUIRED_PLAYER);
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getObject("player");

		Locale.send(this.getSender(), "control.ipof", player.getName(), IPCache.INSTANCE.getData(player).getIp());
	}

}