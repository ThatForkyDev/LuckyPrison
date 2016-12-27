package com.ulfric.lib.api.module;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.locale.Locale;

public final class DisabledCommand extends SimpleCommand {

	@Override
	public void run()
	{
		Locale.sendError(this.getSender(), "system.cmd_module_disabled");
	}

}
