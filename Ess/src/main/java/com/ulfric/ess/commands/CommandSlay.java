package com.ulfric.ess.commands;

import org.bukkit.entity.Player;

import com.ulfric.ess.lang.Meta;
import com.ulfric.ess.lang.Permissions;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.entity.EntityUtils;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.locale.Locale;

public class CommandSlay extends SimpleCommand {


	public CommandSlay()
	{
		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, Permissions.SLAY_OTHERS);
	}


	@Override
	public void run()
	{
		Player player = (Player) this.getObject("player");

		if (this.isPlayer() && player.equals(this.getPlayer())) confirm:
		{
			if (Metadata.removeIfPresent(player, Meta.CONFIRM_KILL)) break confirm;

			Metadata.applyNull(player, Meta.CONFIRM_KILL);

			Locale.sendWarning(this.getSender(), "ess.kill_warn");

			return;
		}

		EntityUtils.kill(player);
	}


}