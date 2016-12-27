package com.ulfric.lib.api.chat;

import org.bukkit.command.CommandSender;

public interface Message {

	Object getMessage();

	void send(CommandSender sender);

}