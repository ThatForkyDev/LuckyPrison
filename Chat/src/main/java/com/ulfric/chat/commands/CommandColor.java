package com.ulfric.chat.commands;

import com.ulfric.chat.gui.PanelChatcolor;
import com.ulfric.lib.api.command.SimpleCommand;

public class CommandColor extends SimpleCommand {

	public CommandColor()
	{
		this.withEnforcePlayer();
	}

	@Override
	public void run()
	{
		PanelChatcolor.create(this.getPlayer());
	}

}