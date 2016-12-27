package com.ulfric.prison.commands;

import java.util.UUID;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Booleans;
import com.ulfric.lib.api.locale.Locale;

public class CommandExplosions extends SimpleCommand {

	public CommandExplosions()
	{
		this.withEnforcePlayer();
	}

	@Override
	public void run()
	{
		UUID uuid = this.getUniqueId();

		boolean current = Hooks.DATA.getPlayerDataAsBoolean(uuid, "prison.sound.explosions");

		Hooks.DATA.setPlayerData(uuid, "prison.sound.explosions", !current);

		Locale.sendSuccess(this.getPlayer(), "prison.explosion_sound", Booleans.fancify(current, "Enabled", "Disabled"));
	}

}