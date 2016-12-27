package com.ulfric.lib.api.chat;

import org.bukkit.command.CommandSender;

public final class ChatPage {

	private final Message[] messages;
	private int current;

	ChatPage(int lineCount)
	{
		this.messages = new Message[lineCount];
	}

	public boolean isFull()
	{
		return this.current == this.messages.length;
	}

	public void addLine(Message message)
	{
		this.messages[this.current++] = message;
	}

	public void send(CommandSender sender)
	{
		for (Message message : this.messages)
		{
			if (message == null) break;

			message.send(sender);
		}
	}

}
