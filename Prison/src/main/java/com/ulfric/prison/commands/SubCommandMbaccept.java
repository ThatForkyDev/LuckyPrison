package com.ulfric.prison.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Command;
import com.ulfric.lib.api.command.SimpleSubCommand;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.prison.entity.Minebuddy;
import com.ulfric.prison.lang.Meta;

class SubCommandMbaccept extends SimpleSubCommand {

	public SubCommandMbaccept(Command command)
	{
		super(command, "accept", "yes");

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

		if (player.hasMetadata(Meta.MINEBUDDY))
		{
			Locale.sendError(player, "prison.minebuddy_must_leave");

			return;
		}

		Player buddy = Metadata.getValue(player, Meta.MINEBUDDY_REQUEST);

		Metadata.remove(player, Meta.MINEBUDDY_REQUEST);

		if (buddy.hasMetadata(Meta.MINEBUDDY))
		{
			Locale.sendError(player, "prison.minebuddy_taken", buddy.getName());

			return;
		}

		Minebuddy minebuddy = new Minebuddy(buddy, player);

		Metadata.apply(buddy, Meta.MINEBUDDY, minebuddy);
		Locale.sendSuccess(buddy, "prison.minebuddy_made", player.getName());

		Metadata.apply(player, Meta.MINEBUDDY, minebuddy);
		Locale.sendSuccess(player, "prison.minebuddy_made", buddy.getName());
	}

}