package com.ulfric.prison.commands;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.PermissionsExHook.Group;
import com.ulfric.lib.api.server.Commands;

public class CommandMine extends SimpleCommand {


	public CommandMine()
	{
		this.withEnforcePlayer();
	}


	@Override
	public void run()
	{
		Group rank = Hooks.PERMISSIONS.user(this.getPlayer()).getRankLadderGroup("mines");

		String rankName = rank == null ? "a" : rank.getName();

		Commands.dispatch(this.getSender(), "warp " + rankName);
	}


}