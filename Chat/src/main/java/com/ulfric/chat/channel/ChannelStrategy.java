package com.ulfric.chat.channel;

import com.ulfric.chat.channel.enums.ChatChannel;
import com.ulfric.lib.api.command.arg.ArgStrategy;

public enum ChannelStrategy implements ArgStrategy<ChatChannel> {

	INSTANCE;

	@Override
	public ChatChannel match(String string)
	{
		string = string.toUpperCase();

		for (ChatChannel channel : ChatChannel.values())
		{
			if (!channel.name().startsWith(string)) continue;

			return channel;
		}

		return null;
	}

}