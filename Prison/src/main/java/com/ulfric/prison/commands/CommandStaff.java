package com.ulfric.prison.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.PermissionsExHook;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.prison.Prison;
import com.ulfric.prison.modules.ModuleStaff;

// TODO Switch to GUI?
public class CommandStaff extends SimpleCommand {

	public CommandStaff()
	{
		this.withArgument("group", ArgStrategy.STRING); // TODO Implement
	}

	@Override
	public void run()
	{
		Locale.send(this.getSender(), "prison.staff_fetch");
		List<PermissionsExHook.Group> groups = ModuleStaff.get().getGroups();
		boolean hadOnlineStaff = false;
		for (PermissionsExHook.Group group : groups)
		{
			boolean hasSentGroupHeader = false;

			String combinedNames = "";
			for (Player player : Prison.get().getServer().getOnlinePlayers())
			{
				PermissionsExHook.User user =
						Hooks.PERMISSIONS.getUserEngine().getUser(player);
				if (Arrays.asList(user.getGroupNames()).contains(group.getName())) continue;
				Player staffMember = Bukkit.getPlayer(user.getUniqueId());
				if (staffMember == null) continue;

				if (combinedNames.length() > 0)
				{
					combinedNames += ChatColor.WHITE+", ";
				}

				if (!hadOnlineStaff)
				{
					Locale.send(getSender(), "prison.stafflist_header");
					hadOnlineStaff = true;
				}

				if (!hasSentGroupHeader)
				{
					Locale.send(getSender(), "prison.stafflist_group", Character.toUpperCase(group.getName().charAt(0))+group.getName().substring(1));
					hasSentGroupHeader = true;
				}

				System.out.println("Online staff: "+staffMember.getName());
				combinedNames += ChatColor.GREEN+user.getName();
			}
			getSender().sendMessage(combinedNames);
		}

		if (!hadOnlineStaff) // Tell the user no staff members are on
		{
			Locale.sendError(getSender(), "prison.stafflist_none");
		}
		else
		{
			Locale.send(getSender(), "prison.stafflist_footer");
		}
	}
}
