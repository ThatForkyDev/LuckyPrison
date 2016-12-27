package com.ulfric.control.commands;

import java.util.TimeZone;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.time.TimeUtils;

public class CommandDate extends SimpleCommand {


	@Override
	public void run()
	{
		Locale.send(this.getSender(), "control.date", TimeUtils.formatCurrentTime(), StringUtils.acronym(TimeZone.getDefault().getDisplayName()));
	}


}