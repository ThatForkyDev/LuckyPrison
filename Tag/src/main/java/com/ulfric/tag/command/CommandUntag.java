package com.ulfric.tag.command;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.locale.Locale;

public class CommandUntag extends SimpleCommand {

	public CommandUntag()
	{
		this.withArgument(Argument.REQUIRED_PLAYER);
	}

	@Override
	public void run()
	{
		Player target = (Player) this.getObject("player");

		if (Metadata.removeIfPresent(target, "_ulf_combattag"))
		{
			Locale.sendSuccess(this.getSender(), "tag.untag", target.getName());

			return;
		}

		Locale.sendError(this.getSender(), "tag.untag_err", target.getName());
	}

}