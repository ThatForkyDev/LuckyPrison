package com.ulfric.prison.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.prison.gui.PanelBackpack;

public class CommandBackpack extends SimpleCommand {

	public CommandBackpack()
	{
		this.withEnforcePlayer();

		this.withArgument("page", ArgStrategy.INTEGER, 0);
		this.withArgument("player", ArgStrategy.OFFLINE_PLAYER, this::getPlayer, "prison.backpack.others");
	}

	@Override
	public void run()
	{
		int page = (int) this.getObject("page");

		page = Math.max(page-1, 0);

		Player player = this.getPlayer();
		OfflinePlayer target = (OfflinePlayer) this.getObject("player");

		PanelBackpack.create(player, target, page);
	}

}