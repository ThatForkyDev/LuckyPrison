package com.ulfric.control.commands;

import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.command.arg.ArgStrategy;

public class CommandUncmute extends UnpunishmentCommand {


	public CommandUncmute()
	{
		super(ArgStrategy.OFFLINE_PLAYER, "system.specify_player", PunishmentType.CMUTE);
	}


}