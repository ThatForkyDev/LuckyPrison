package com.ulfric.control.commands;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.locale.Locale;

public class CommandOslay extends PunishmentCommand {

	public CommandOslay()
	{
		super(Argument.REQUIRED_OFFLINE_PLAYER);
	}

	@Override
	public void execute()
	{
		OfflinePlayer target = (OfflinePlayer) this.getObject("player");

		if (target.isOnline())
		{
			Locale.sendError(this.getSender(), "control.oslay_online");

			return;
		}

		UUID uuid = target.getUniqueId();
		boolean value = !Hooks.DATA.getPlayerDataAsBoolean(uuid, "control.slay");
		Hooks.DATA.setPlayerData(uuid, "contron.slay", value);

		Locale.sendSuccess(this.getSender(), "control.oslay", target.getName(), value);
	}

}