package com.ulfric.ess.commands;

import com.ulfric.ess.lang.Meta;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.locale.Locale;

public class CommandSigncmd extends SimpleCommand {


	public CommandSigncmd()
	{
		this.withEnforcePlayer();

		this.withIndexUnusedArgs();

		this.withArgument(Argument.builder().withPath("start").withStrategy(ArgStrategy.STRING).withUsage("ess.sign_command_needed").withRemovalExclusion());
	}


	@Override
	public void run()
	{
		Metadata.apply(this.getPlayer(), Meta.SIGN_CMD, this.getUnusedArgs());

		Locale.sendSuccess(this.getPlayer(), "ess.sign_meta");
	}


}