package com.ulfric.prison.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Command;
import com.ulfric.lib.api.command.SimpleSubCommand;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.prison.entity.Minebuddy;
import com.ulfric.prison.lang.Meta;

class SubCommandMbleave extends SimpleSubCommand {

	public SubCommandMbleave(Command command)
	{
		super(command, "leave", "exit", "stop");

		this.withEnforcePlayer();
	}

	@Override
	public void run()
	{
		Player player = this.getPlayer();

		if (!player.hasMetadata(Meta.MINEBUDDY))
		{
			Locale.sendError(player, "prison.minebuddy_none");

			return;
		}

		Minebuddy partnership = Metadata.getValue(player, Meta.MINEBUDDY);

		partnership.annihilate();
	}

}