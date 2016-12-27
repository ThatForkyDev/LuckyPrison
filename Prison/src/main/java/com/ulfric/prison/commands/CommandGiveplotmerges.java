package com.ulfric.prison.commands;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.locale.Locale;

public class CommandGiveplotmerges extends SimpleCommand {

	public CommandGiveplotmerges()
	{
		this.withArgument(Argument.REQUIRED_OFFLINE_PLAYER);

		this.withArgument("int", ArgStrategy.INTEGER, "prison.plot_merge_give_err");
	}

	@Override
	public void run()
	{
		OfflinePlayer target = (OfflinePlayer) this.getObject("player");

		int number = (int) this.getObject("int");

		UUID uuid = target.getUniqueId();

		int current = Hooks.DATA.getPlayerDataAsInt(uuid, "plot.merge");

		Hooks.DATA.setPlayerData(uuid, "plot.merge", number + current);

		Locale.sendSuccess(this.getSender(), "prison.plot_merge_extra", target.getName(), current, number);
	}

}