package com.ulfric.prison.commands;

import org.bukkit.OfflinePlayer;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.PermissionsExHook.Group;
import com.ulfric.lib.api.hook.PermissionsExHook.User;
import com.ulfric.lib.api.locale.Locale;

public class CommandPexr extends SimpleCommand {

	public CommandPexr()
	{
		this.withArgument(Argument.REQUIRED_OFFLINE_PLAYER);

		this.withArgument("cur", ArgStrategy.STRING, "prison.pexr_group_cur");

		this.withArgument("new", ArgStrategy.STRING, "prison.pexr_group_new");
	}

	@Override
	public void run()
	{
		OfflinePlayer player = (OfflinePlayer) this.getObject("player");

		Group current = Hooks.PERMISSIONS.group((String) this.getObject("cur"));

		User user = Hooks.PERMISSIONS.user(player.getUniqueId());

		if (!user.hasGroup(current)) return;

		Group newGroup = Hooks.PERMISSIONS.group((String) this.getObject("new"));

		user.replace(current, newGroup);

		Locale.sendSuccess(this.getSender(), "prison.pexr", user.getName(), current.getName(), newGroup.getName());
	}

}