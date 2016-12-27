package com.ulfric.chat.commands;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.entity.Player;

import com.ulfric.chat.channel.ChannelStrategy;
import com.ulfric.chat.channel.enums.ChatChannel;
import com.ulfric.chat.lang.Meta;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.locale.Locale;

public class CommandChannel extends SimpleCommand {


	public CommandChannel()
	{
		this.withEnforcePlayer();

		this.withArgument("channel", ChannelStrategy.INSTANCE, ChatChannel.GLOBAL);
	}


	@Override
	public void run()
	{
		Player player = this.getPlayer();

		ChatChannel channel = (ChatChannel) this.getObject("channel");

		if (channel.equals(ChatChannel.GLOBAL))
		{
			if (!Metadata.removeIfPresent(player, Meta.CHAT_CHANNEL))
			{
				Locale.sendError(this.getPlayer(), "chat.channels");

				return;
			}

			Locale.sendSuccess(this.getPlayer(), "chat.channel_global");

			return;
		}

		if (channel.equals(ChatChannel.OPERATOR) && !this.hasPermission("chat.channels.operator"))
		{
			Locale.sendError(this.getPlayer(), "chat.channel_operator_err");

			return;
		}

		Metadata.apply(player, Meta.CHAT_CHANNEL, channel);
		Locale.sendSuccess(this.getPlayer(), "chat.channel_change", WordUtils.capitalize(channel.name().toLowerCase()));
	}


}