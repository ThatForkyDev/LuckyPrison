package com.ulfric.control.commands;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;

public class CommandLockdown extends SimpleCommand {

	public CommandLockdown()
	{
		this.withArgument("lock", ArgStrategy.BOOLEAN, () -> !CommandLockdown.lockdown());
	}

	public static volatile boolean lockdown;
	public static boolean lockdown() { return CommandLockdown.lockdown; }

	@Override
	public void run()
	{
		CommandLockdown.lockdown = (boolean) this.getObject("lock");

		CommandLockdown.send(this.getName());
	}

	public static void send(String name)
	{
		if (CommandLockdown.lockdown)
		{
			Locale.sendMass("control.lockdown_start", name);

			return;
		}

		Locale.sendMass("control.lockdown_end", name);
	}

}