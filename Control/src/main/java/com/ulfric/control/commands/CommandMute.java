package com.ulfric.control.commands;

import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.command.Argument;

public class CommandMute extends TimedPunishmentCommand {


	public CommandMute()
	{
		super(PunishmentType.MUTE, Argument.REQUIRED_OFFLINE_PLAYER);
	}


}