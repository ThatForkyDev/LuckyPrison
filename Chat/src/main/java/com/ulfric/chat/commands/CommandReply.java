package com.ulfric.chat.commands;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import com.ulfric.chat.lang.Meta;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.server.Commands;

public class CommandReply extends SimpleCommand {


	public CommandReply()
	{
		this.withEnforcePlayer();

		this.withIndexUnusedArgs();
	}


	@Override
	public void run()
	{
		Player player = this.getPlayer();

		String message = this.getUnusedArgs();

		if (StringUtils.isBlank(message))
		{
			Locale.sendError(player, "chat.message_blank");

			return;
		}

		String last = Metadata.getValueAsString(player, Meta.LAST_PM);

		if (last == null)
		{
			Locale.sendError(player, "chat.message_none");

			return;
		}

		Commands.dispatch(player, Strings.format("msg {0} {1}", last, message));
	}


}