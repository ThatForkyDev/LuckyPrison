package com.ulfric.tag.command;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.tag.CombatTag;

public class CommandForcetag extends SimpleCommand {

	public CommandForcetag()
	{
		this.withArgument(Argument.REQUIRED_PLAYER);

		this.withArgument("time", ArgStrategy.INTEGER, 17);
	}

	@Override
	public void run()
	{
		int duration = (int) this.getObject("time");

		Player target = (Player) this.getObject("player");

		CombatTag.tag(target, duration);

		Locale.sendSuccess(this.getSender(), "tag.forcetag", target, duration);
	}

}