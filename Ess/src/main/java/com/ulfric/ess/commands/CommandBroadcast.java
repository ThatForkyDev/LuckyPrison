package com.ulfric.ess.commands;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;

public class CommandBroadcast extends SimpleCommand {

	public CommandBroadcast()
	{
		this.withIndexUnusedArgs();

		this.withArgument(Argument.builder().withPath("start").withStrategy(ArgStrategy.STRING).withUsage("ess.say_blank").withRemovalExclusion());
	}

	@Override
	public void run()
	{
		Locale.sendMass("ess.broadcast", Strings.formatF(this.getUnusedArgs()));
	}

}