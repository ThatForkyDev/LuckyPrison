package com.ulfric.lib.api.chat;

import com.ulfric.lib.api.locale.Locale;
import org.bukkit.command.CommandSender;

public final class LocaleMessage implements Message {

	private final String message;

	LocaleMessage(String message)
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
		Locale.send(sender, this.message);
	}

}
