package com.ulfric.ess.commands;

import org.bukkit.Location;

import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ExactArg;
import com.ulfric.lib.api.entity.HologramUtils;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.location.LocationUtils;

public class CommandHologram extends SimpleCommand {

	public CommandHologram()
	{
		this.withEnforcePlayer();

		this.withIndexUnusedArgs();

		this.withArgument("rnd", new ExactArg("--round"));
	}

	@Override
	public void run()
	{
		Location location = this.getLocation();

		if (this.hasObject("rnd"))
		{
			location = LocationUtils.round(location.clone());
		}

		String text = Chat.color(this.getUnusedArgs());

		HologramUtils.spawn(location, text);

		Locale.sendSuccess(this.getPlayer(), "ess.hologram", text);
	}

}