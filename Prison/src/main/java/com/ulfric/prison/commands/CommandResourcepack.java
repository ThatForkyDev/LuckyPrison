package com.ulfric.prison.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.nms.Mineserver;
import com.ulfric.prison.lang.Permissions;

public class CommandResourcepack extends SimpleCommand {

	public CommandResourcepack()
	{
		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, Permissions.SEND_RESOURCE_PACK);
	}

	@Override
	public void run()
	{
		((Player) this.getObject("player")).setResourcePack(Mineserver.getMinecraft().getResourcePack(), Mineserver.getMinecraft().getResourcePackHash());

		Locale.sendSuccess(this.getSender(), "prison.resourcepack_sent");
	}

}