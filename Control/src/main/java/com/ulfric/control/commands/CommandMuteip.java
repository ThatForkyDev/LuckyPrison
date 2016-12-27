package com.ulfric.control.commands;

import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.command.Argument;

public class CommandMuteip extends TimedPunishmentCommand {


	public CommandMuteip()
	{
		super(PunishmentType.MUTE, Argument.REQUIRED_INET);
	}


}