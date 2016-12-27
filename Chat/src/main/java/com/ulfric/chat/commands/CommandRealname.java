package com.ulfric.chat.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;

public class CommandRealname extends SimpleCommand {

	public CommandRealname()
	{
		this.withArgument("str", ArgStrategy.STRING, "chat.realname_needed");
	}

	@Override
	public void run()
	{
		String str = Chat.color((String) this.getObject("str")).toLowerCase();
		String close = ((String) this.getObject("str")).toLowerCase();

		List<String> exact = Lists.newArrayList();
		List<String> similar = Lists.newArrayList();

		for (Player player : Bukkit.getOnlinePlayers())
		{
			String display = player.getDisplayName();

			String displayL = display.toLowerCase().replace("*", Strings.BLANK);
			String stripped = ChatColor.stripColor(displayL);

			if (str.equals(displayL))
			{
				exact.add(Strings.formatF("&e{0} &f(&7{1}&f), ", player.getName(), display));

				continue;
			}

			if (!StringUtils.isSimilar(stripped, close)) continue;

			similar.add(Strings.formatF("&e{0} &f(&7{1}&f), ", player.getName(), display));
		}

		CommandSender sender = this.getSender();

		if (!similar.isEmpty())
		{
			String nice = StringUtils.mergeNicely(similar);

			sender.sendMessage(Strings.formatF("&5&lSimilar: {0}", nice.substring(0, nice.length()-2)));
		}

		if (exact.isEmpty()) return;

		String nice = StringUtils.mergeNicely(exact);

		sender.sendMessage(Strings.formatF("&5&lExact: {0}", nice.substring(0, nice.length()-2)));
	}

}