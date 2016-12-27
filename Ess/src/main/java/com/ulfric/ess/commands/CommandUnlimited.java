package com.ulfric.ess.commands;

import org.bukkit.entity.Player;

import com.ulfric.ess.lang.Meta;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.java.Booleans;
import com.ulfric.lib.api.locale.Locale;

public class CommandUnlimited extends SimpleCommand {

	public CommandUnlimited()
	{
		this.withEnforcePlayer();

	}
   
	@Override
	public void run()
	{
		Player player = this.getPlayer();

		boolean value = Metadata.removeIfPresent(player, Meta.UNLIMITED);

		if (!value)
		{
			Metadata.applyNull(player, Meta.UNLIMITED);
		}

		Locale.sendSuccess(player, "ess.unlimited_toggle", Booleans.fancify(!value, player));
	}

}