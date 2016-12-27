package com.ulfric.control.commands;

import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.command.Argument;

public class CommandCmuteip extends TimedPunishmentCommand {

	public CommandCmuteip()
	{
		super(PunishmentType.CMUTE, Argument.REQUIRED_INET);
	}

}