package com.ulfric.chat.commands;

import com.ulfric.chat.gui.PanelChatsettings;
import com.ulfric.lib.api.command.SimpleCommand;

public class CommandChat extends SimpleCommand {

	public CommandChat()
	{
		this.withEnforcePlayer();
	}

	@Override
	public void run()
	{
		PanelChatsettings.create(this.getPlayer());
	}

}