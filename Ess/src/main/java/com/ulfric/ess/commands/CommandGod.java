package com.ulfric.ess.commands;

import org.bukkit.entity.Player;

import com.ulfric.ess.lang.Meta;
import com.ulfric.ess.lang.Permissions;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Booleans;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;

public class CommandGod extends SimpleCommand {

	public CommandGod()
	{
		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, Permissions.GOD_OTHERS);
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getObject("player");

		boolean value = Metadata.removeIfPresent(player, Meta.GODMODE);

		if (!value)
		{
			Metadata.applyNull(player, Meta.GODMODE);
		}

		Locale.sendSuccess(this.getSender(), "ess.godmode", Booleans.fancify(!value, Locale.getLanguage(player), this.isPlayer() && player.equals(this.getPlayer()) ? Strings.BLANK : " on " + player.getName()));

		Hooks.PRISON.updateScoreboard(player);
	}

}