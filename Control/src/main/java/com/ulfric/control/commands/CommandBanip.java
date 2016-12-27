package com.ulfric.control.commands;

import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.command.Argument;

public class CommandBanip extends TimedPunishmentCommand {

	public CommandBanip()
	{
		super(PunishmentType.BAN, Argument.REQUIRED_INET);
	}

}