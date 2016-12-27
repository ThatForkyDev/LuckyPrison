package com.ulfric.prison.commands;

import org.bukkit.OfflinePlayer;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.PermissionsExHook.Group;
import com.ulfric.lib.api.hook.PermissionsExHook.User;

public class CommandPexfix extends SimpleCommand {

	public CommandPexfix()
	{
		this.withArgument(Argument.REQUIRED_OFFLINE_PLAYER);
	}

	@Override
	public void run()
	{
		OfflinePlayer player = (OfflinePlayer) this.getObject("player");

		User user = Hooks.PERMISSIONS.user(player.getUniqueId());

		Group current = user.getRankLadderGroup("mines");

		if (current == null) return;

		Group a = Hooks.PERMISSIONS.group("a");

		if (a == null) return;

		if (!current.equals(a)) return;

		user.setGroup(a);
	}

}