package com.ulfric.control.commands;

import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.command.arg.ArgStrategy;

public class CommandUnmuteip extends UnpunishmentCommand {


	public CommandUnmuteip()
	{
		super(ArgStrategy.INET, "control.specify_valid_ip", PunishmentType.MUTE);
	}


}