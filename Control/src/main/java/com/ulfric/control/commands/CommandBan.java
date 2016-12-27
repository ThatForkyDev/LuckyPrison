package com.ulfric.control.commands;

import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.command.Argument;

public class CommandBan extends TimedPunishmentCommand {

	public CommandBan()
	{
		super(PunishmentType.BAN, Argument.REQUIRED_OFFLINE_PLAYER);
	}

}