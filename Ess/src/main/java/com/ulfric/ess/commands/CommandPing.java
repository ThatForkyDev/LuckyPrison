package com.ulfric.ess.commands;

import org.bukkit.entity.Player;

import com.ulfric.ess.lang.Permissions;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerUtils;

public class CommandPing extends SimpleCommand {

	public CommandPing()
	{
		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer);
	}

	@Override
	public void run()
	{
		Player target = (Player) this.getObject("player");

		boolean flag = this.isPlayer() && target.equals(this.getPlayer());

		if (flag && !this.hasPermission(Permissions.PING_OTHERS))
		{
			Locale.sendError(this.getSender(), "ess.ping_other_err");

			return;
		}

		int ping = PlayerUtils.getPing(target);

		String sign = ping > 1000 ? ">" : ping < 0 ? "<" : Strings.BLANK;

		ping = Math.min(1000, Math.max(ping, 0));

		Locale.send(this.getSender(), "ess.ping", flag ? Locale.getMessage(this.getSender(), "system.your") : target.getName() + "'s", Strings.format("{0}{1}ms", sign, ping));
	}

}