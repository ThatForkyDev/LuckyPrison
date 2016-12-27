package com.ulfric.control.commands;

import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Strings;

public class CommandAlert extends SimpleCommand {

	public CommandAlert()
	{
		this.withArgument(Argument.builder().withPath("player").withStrategy(ArgStrategy.PLAYER).withCap(1));

		this.withArgument(Argument.builder().withPath("start").withStrategy(ArgStrategy.STRING).withUsage("control.alert_msg_empty").withRemovalExclusion());
	}

	private String pattern;

	@SuppressWarnings("deprecation")
	@Override
	public void run()
	{
		Player target = (Player) this.getObject("player");

		String message = Chat.color(this.getUnusedArgs());

		if (this.pattern == null)
		{
			this.pattern = Pattern.quote("-sub");
		}

		String[] parts = message.split(this.pattern);

		message = parts[0];

		String sub = parts.length > 1 ? parts[1] : Strings.BLANK;

		if (target != null)
		{
			target.sendTitle(message.replace(Strings.PLAYER, target.getName()), sub.replace(Strings.PLAYER, target.getName()));

			return;
		}

		for (Player player : Bukkit.getOnlinePlayers())
		{
			player.sendTitle(message.replace(Strings.PLAYER, player.getName()), sub.replace(Strings.PLAYER, player.getName()));
		}
	}

}