package com.ulfric.ess.commands;

import java.util.regex.Pattern;

import com.ulfric.ess.modules.ModuleMotd;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;

public class CommandSetmotd extends SimpleCommand {

	public CommandSetmotd()
	{
		this.withIndexUnusedArgs();

		this.withArgument(Argument.builder().withPath("start").withStrategy(ArgStrategy.STRING).withRemovalExclusion());
	}

	@Override
	public void run()
	{
		if (!this.hasObjects())
		{
			ModuleMotd.get().disable();
			ModuleMotd.get().enable();

			Locale.sendSuccess(this.getSender(), "ess.motd_reset");

			return;
		}

		String[] parts = this.getUnusedArgs().split(Pattern.quote("-b"));

		ModuleMotd.get().clear();
		ModuleMotd.get().addMotd(parts[0].trim(), parts.length == 1 ? Strings.BLANK : parts[1].trim());

		Locale.send(this.getSender(), "ess.motd_set");
	}

}