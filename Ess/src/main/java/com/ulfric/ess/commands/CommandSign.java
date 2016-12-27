package com.ulfric.ess.commands;

import com.ulfric.ess.lang.Meta;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.tuple.Tuples;

public class CommandSign extends SimpleCommand {


	public CommandSign()
	{
		this.withEnforcePlayer();

		this.withIndexUnusedArgs();

		this.withArgument("int", ArgStrategy.POSITIVE_INTEGER, "ess.sign_specify_line");
	}


	@Override
	public void run()
	{
		Metadata.apply(this.getPlayer(), Meta.SIGN_CHANGE, Tuples.newPair((int) this.getObject("int"), Chat.color(this.getUnusedArgs())));

		Locale.sendSuccess(this.getPlayer(), "ess.sign_meta");
	}


}