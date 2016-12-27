package com.ulfric.control.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.nms.CraftPlayerVI;
import com.ulfric.lib.api.nms.Packets;

public class CommandRespawn extends PunishmentCommand {

	public CommandRespawn()
	{
		super(Argument.REQUIRED_PLAYER);
	}

	@Override
	public void execute()
	{
		CraftPlayerVI.of((Player) this.getObject("player")).sendPacket(Packets.newRespawn());
	}

}