package com.ulfric.control.commands;

import org.bukkit.entity.Player;

import com.ulfric.control.coll.IPCache;
import com.ulfric.control.entity.IPData;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;

public class CommandViolation extends SimpleCommand {

	public CommandViolation()
	{
		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer);
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getObject("player");

		IPData data = IPCache.INSTANCE.getData(player);

		Locale.send(this.getSender(), "control.violation", player.getName(), data.getViolationTotal());
	}

}