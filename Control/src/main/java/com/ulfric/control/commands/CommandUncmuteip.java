package com.ulfric.control.commands;

import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.command.arg.ArgStrategy;

public class CommandUncmuteip extends UnpunishmentCommand {


	public CommandUncmuteip()
	{
		super(ArgStrategy.INET, "control.specify_valid_ip", PunishmentType.CMUTE);
	}


}