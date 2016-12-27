package com.ulfric.ess.commands;

import org.bukkit.Bukkit;

import com.ulfric.ess.lang.Permissions;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ExactArg;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerUtils;

public class CommandOnline extends SimpleCommand {

	public CommandOnline()
	{
		this.withArgument(Argument.builder().withPath("unique").withStrategy(new ExactArg("--unique")).withNode("ess.online.unique"));
	}

	@Override
	public void run()
	{
		if (this.hasObject("unique"))
		{
			Locale.send(this.getSender(), "ess.online", Hooks.CONTROL.countUniqueIPs());

			return;
		}

		if (this.hasPermission(Permissions.VANISH_SEE))
		{
			Locale.send(this.getSender(), "ess.online", Bukkit.getOnlinePlayers().size());

			return;
		}

		Locale.send(this.getSender(), "ess.online", Bukkit.getOnlinePlayers().size() - PlayerUtils.countVanished());
	}

}