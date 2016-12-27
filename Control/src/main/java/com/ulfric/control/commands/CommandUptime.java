package com.ulfric.control.commands;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.nms.Mineserver;
import com.ulfric.lib.api.time.TimeUtils;

public class CommandUptime extends SimpleCommand {

	@Override
	public void run()
	{
		Locale.send(this.getSender(), "control.uptime", TimeUtils.millisecondsToString(Mineserver.uptime().timeSince()));
	}

}