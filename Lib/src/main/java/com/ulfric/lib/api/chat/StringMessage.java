package com.ulfric.lib.api.chat;

import org.bukkit.command.CommandSender;

public final class StringMessage implements Message {

	private final String message;

	StringMessage(String message)
	{
		this.message = message;
	}

	@Override
	public String getMessage()
	{
		return this.message;
	}

	@Override
	public void send(CommandSender sender)
	{
		sender.sendMessage(this.message);
	}

}
