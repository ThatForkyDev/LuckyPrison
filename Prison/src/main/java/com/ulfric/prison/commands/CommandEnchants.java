package com.ulfric.prison.commands;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.prison.gui.PanelEnchants;

public class CommandEnchants extends SimpleCommand {

	public CommandEnchants()
	{
		this.withEnforcePlayer();
	}

	@Override
	public void run()
	{
		PanelEnchants.get().open(this.getPlayer());
	}

}