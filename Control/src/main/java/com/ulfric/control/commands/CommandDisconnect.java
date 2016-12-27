package com.ulfric.control.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.nms.Packets;

public class CommandDisconnect extends PunishmentCommand {

	public CommandDisconnect()
	{
		super(Argument.REQUIRED_PLAYER);
	}

	@Override
	public void execute()
	{
		Player target = (Player) this.getObject("player");
		Packets.newDisconnect().send(target);

		Locale.sendSuccess(this.getSender(), "control.disconnect", target.getName());
	}

}