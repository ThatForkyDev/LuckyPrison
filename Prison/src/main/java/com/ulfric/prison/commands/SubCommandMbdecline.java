package com.ulfric.prison.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Command;
import com.ulfric.lib.api.command.SimpleSubCommand;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.prison.lang.Meta;

class SubCommandMbdecline extends SimpleSubCommand {


	public SubCommandMbdecline(Command command)
	{
		super(command, "decline", "no");

		this.withEnforcePlayer();
	}

	@Override
	public void run()
	{
		Player player = this.getPlayer();

		if (!player.hasMetadata(Meta.MINEBUDDY_REQUEST))
		{
			Locale.sendError(player, "prison.minebuddy_no_request");

			return;
		}

		Metadata.remove(player, Meta.MINEBUDDY_REQUEST);
	}

}
