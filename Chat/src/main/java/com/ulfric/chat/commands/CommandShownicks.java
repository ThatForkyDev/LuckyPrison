package com.ulfric.chat.commands;

import org.bukkit.entity.Player;

import com.ulfric.chat.lang.Meta;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.locale.Locale;

public class CommandShownicks extends SimpleCommand {


	public CommandShownicks()
	{
		this.withEnforcePlayer();
	}


	@Override
	public void run()
	{
		Player player = this.getPlayer();

		boolean value = !Hooks.DATA.getPlayerDataAsBoolean(player.getUniqueId(), Meta.SHOW_NICKNAMES);

		Hooks.DATA.setPlayerData(player.getUniqueId(), Meta.SHOW_NICKNAMES, value);

		if (value)
		{
			Locale.sendSuccess(player, "chat.nickname_show");

			return;
		}

		Locale.sendSuccess(player, "chat.nickname_hide");
	}


}