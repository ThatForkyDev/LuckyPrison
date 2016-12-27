package com.ulfric.chat.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.ulfric.chat.lang.Meta;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;

public class CommandNickname extends SimpleCommand {

	public CommandNickname()
	{
		this.withEnforcePlayer();

		this.withArgument("str", ArgStrategy.ENTERED_STRING, "off");

		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, "chat.nickname.other");
	}

	private final Pattern swears = Pattern.compile("(?i)(nigger|fuck|cunt)+");

	@Override
	public void run()
	{
		String nickname = (String) this.getObject("str");

		Player player = (Player) this.getObject("player");

		String lowercase = nickname.toLowerCase();
		if (lowercase.equals("none") || lowercase.equals("off") || lowercase.equals(player.getName().toLowerCase()))
		{
			nickname = player.getName();
		}
		else
		{
			if (!player.hasPermission("chat.nickname.length.bypass"))
			{
				int length = nickname.length();

				if (length < 3 || (nickname = nickname.replaceAll("(?i)[^a-z0-9\\&\\_]", Strings.BLANK)).length() < 3)
				{
					Locale.sendError(player, "chat.nickname_min");

					return;
				}

				else if (length > 16)
				{
					Locale.sendError(player, "chat.nickname_max");

					return;
				}
			}

			if (player.hasPermission("chat.nickname.color"))
			{
				if (!player.hasPermission("chat.nickname.color.bypass")) nickname = Chat.stripBadColor(nickname);
	
				nickname = Chat.color(nickname);
			}

			if (!player.hasPermission("chat.nickname.swear")) swear:
			{
				Matcher matcher = this.swears.matcher(nickname);
				String bad = ChatColor.RESET + nickname;

				if (!matcher.find()) break swear;

				do
				{
					String group = matcher.group();
					String lastColor = ChatColor.getLastColors(bad.split(group)[0]);
					bad = bad.replace(group, ChatColor.DARK_RED + group + lastColor);
				}
				while (matcher.find());

				Locale.sendError(player, "chat.nickname_err", bad);
				return;
			}

			nickname = '*' + nickname;
		}

		Hooks.DATA.setPlayerData(player.getUniqueId(), Meta.NICKNAME_DATA, nickname);
		player.setDisplayName(nickname);

		Locale.sendSuccess(player, "chat.nickname", nickname);
	}

}