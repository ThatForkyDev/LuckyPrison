package com.ulfric.chat.commands;

import org.bukkit.entity.Player;

import com.ulfric.chat.lang.Meta;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.java.Booleans;
import com.ulfric.lib.api.locale.Locale;

public class CommandSocialSpy extends SimpleCommand {

	public CommandSocialSpy()
	{
		this.withEnforcePlayer();
	}

	@Override
	public void run()
	{
		Player player = this.getPlayer();

		boolean value = Metadata.removeIfPresent(player, Meta.SCHUTZSTAFFEL);

		if (!value)
		{
			Metadata.applyNull(player, Meta.SCHUTZSTAFFEL);
		}

		Locale.sendSuccess(player, "chat.social_spy_toggle", Booleans.fancify(!value, player));
	}

}