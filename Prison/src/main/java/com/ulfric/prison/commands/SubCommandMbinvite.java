package com.ulfric.prison.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.Command;
import com.ulfric.lib.api.command.SimpleSubCommand;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.prison.lang.Meta;

class SubCommandMbinvite extends SimpleSubCommand {


	public SubCommandMbinvite(Command command)
	{
		super(command, "invite", "add", "ask");

		this.withEnforcePlayer();

		this.withArgument(Argument.REQUIRED_PLAYER);
	}

	@Override
	public void run()
	{
		Player player = this.getPlayer();
		Player target = (Player) this.getObject("player");

		if (target == null || player.equals(target))
		{
			Locale.sendError(player, "prison.minebuddy_must_specify");

			return;
		}

		if (player.hasMetadata(Meta.MINEBUDDY))
		{
			Locale.send(player, "prison.minebuddy_must_leave");

			return;
		}

		if (target.hasMetadata(Meta.MINEBUDDY))
		{
			Locale.sendError(player, "prison.minebuddy_taken", target.getName());

			return;
		}

		Locale.sendSuccess(player, "prison.minebuddy_invite", target.getName());

		Locale.send(target, "prison.minebuddy_request", player.getName());
		Metadata.apply(target, Meta.MINEBUDDY_REQUEST, player);
	}

}
