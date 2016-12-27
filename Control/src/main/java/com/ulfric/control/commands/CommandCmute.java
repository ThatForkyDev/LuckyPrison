package com.ulfric.control.commands;

import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.command.Argument;

public class CommandCmute extends TimedPunishmentCommand {


	public CommandCmute()
	{
		super(PunishmentType.CMUTE, Argument.REQUIRED_OFFLINE_PLAYER);
	}


}