package com.ulfric.control.commands;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;

public class CommandSpamfilter extends SimpleCommand {

	public CommandSpamfilter()
	{
		this.withArgument("bool", ArgStrategy.BOOLEAN, () -> !CommandSpamfilter.isEnabled());
	}

	private static volatile boolean enabled = true;
	public static boolean isEnabled() { return CommandSpamfilter.enabled; }

	@Override
	public void run()
	{
		CommandSpamfilter.enabled = (boolean) this.getObject("bool");

		CommandSpamfilter.send(this.getName());
	}

	public static void send(String name)
	{
		if (CommandSpamfilter.enabled)
		{
			Locale.sendMass("control.spamfilter_on", name);

			return;
		}

		Locale.sendMass("control.spamfilter_off", name);
	}

}