package com.intellectualcrafters.plot.commands;

import java.util.UUID;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.config.Settings;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.CmdConfirm;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.Permissions;
import com.intellectualcrafters.plot.util.StringMan;
import com.intellectualcrafters.plot.util.UUIDHandler;
import com.plotsquared.general.commands.CommandDeclaration;

@CommandDeclaration(command = "merge",
					aliases = "m",
					description = "Merge the plot you are standing on, with another plot",
					permission = "plots.merge", usage = "/plot merge <all|n|e|s|w> [removeroads]",
					category = CommandCategory.SETTINGS,
					requiredType = RequiredType.NONE,
					confirmation = true)
public class Merge extends SubCommand {

	public static final String[] values = {"north", "east", "south", "west", "auto"};
	public static final String[] aliases = {"n", "e", "s", "w", "all"};

	public static String direction(float yaw)
	{
		yaw /= 90;
		int i = Math.round(yaw);
		switch (i)
		{
			case -4:
			case 0:
			case 4:
				return "SOUTH";
			case -1:
			case 3:
				return "EAST";
			case -2:
			case 2:
				return "NORTH";
			case -3:
			case 1:
				return "WEST";
			default:
				return "";
		}
	}

	@Override
	public boolean onCommand(PlotPlayer player, String... args)
	{
		Location loc = player.getLocationFull();
		Plot plot = loc.getPlotAbs();
		if (plot == null)
		{
			return !this.sendMessage(player, C.NOT_IN_PLOT);
		}
		if (!plot.hasOwner())
		{
			MainUtil.sendMessage(player, C.PLOT_UNOWNED);
			return false;
		}
		UUID uuid = player.getUUID();
		if (!plot.isOwner(uuid))
		{
			if (!Permissions.hasPermission(player, "plots.admin.command.merge"))
			{
				MainUtil.sendMessage(player, C.NO_PLOT_PERMS);
				return false;
			}
			else
			{
				uuid = plot.owner;
			}
		}
		PlotArea plotArea = plot.getArea();
		int size = plot.getConnectedPlots().size();
		int maxSize = Permissions.hasPermissionRange(player, "plots.merge", Settings.Limit.MAX_PLOTS);
		if (size - 1 > maxSize)
		{
			MainUtil.sendMessage(player, C.NO_PERMISSION, "plots.merge." + (size + 1));
			return false;
		}
		int direction = -1;
		if (args.length == 0)
		{
			switch (direction(player.getLocationFull().getYaw()))
			{
				case "NORTH":
					direction = 0;
					break;
				case "EAST":
					direction = 1;
					break;
				case "SOUTH":
					direction = 2;
					break;
				case "WEST":
					direction = 3;
					break;
			}
		}
		else
		{
			if ("all".equalsIgnoreCase(args[0]) || "auto".equalsIgnoreCase(args[0]))
			{
				boolean terrain = true;
				if (args.length == 2)
				{
					terrain = "true".equalsIgnoreCase(args[1]);
				}
				if (plot.autoMerge(-1, maxSize, uuid, terrain))
				{
					MainUtil.sendMessage(player, C.SUCCESS_MERGE);
					return true;
				}
				MainUtil.sendMessage(player, C.NO_AVAILABLE_AUTOMERGE);
				return false;
			}
			for (int i = 0; i < values.length; i++)
			{
				if (args[0].equalsIgnoreCase(values[i]) || args[0].equalsIgnoreCase(aliases[i]))
				{
					direction = i;
					break;
				}
			}
		}
		if (direction == -1)
		{
			MainUtil.sendMessage(player, C.COMMAND_SYNTAX, "/plot merge <" + StringMan.join(values, "|") + "> [removeroads]");
			MainUtil.sendMessage(player, C.DIRECTION.s().replaceAll("%dir%", direction(loc.getYaw())));
			return false;
		}
		boolean terrain = args.length != 2 || "true".equalsIgnoreCase(args[1]);
		if (plot.autoMerge(direction, maxSize - size, uuid, terrain))
		{
			MainUtil.sendMessage(player, C.SUCCESS_MERGE);
			return true;
		}
		Plot adjacent = plot.getRelative(direction);
		if (adjacent == null || !adjacent.hasOwner() || adjacent.getMerged((direction + 2) % 4) || adjacent.isOwner(uuid))
		{
			MainUtil.sendMessage(player, C.NO_AVAILABLE_AUTOMERGE);
			return false;
		}
		if (!Permissions.hasPermission(player, C.PERMISSION_MERGE_OTHER))
		{
			MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_MERGE_OTHER);
			return false;
		}
		java.util.Set<UUID> uuids = adjacent.getOwners();
		boolean isOnline = false;
		for (UUID owner : uuids)
		{
			PlotPlayer accepter = UUIDHandler.getPlayer(owner);
			if (accepter == null)
			{
				continue;
			}
			isOnline = true;
			int dir = direction;
			Runnable run = () ->
			{
				MainUtil.sendMessage(accepter, C.MERGE_ACCEPTED);
				plot.autoMerge(dir, maxSize - size, owner, terrain);
				PlotPlayer plotPlayer = UUIDHandler.getPlayer(player.getUUID());
				if (plotPlayer == null)
				{
					this.sendMessage(accepter, C.MERGE_NOT_VALID);
					return;
				}
				MainUtil.sendMessage(player, C.SUCCESS_MERGE);
			};
			if (this.hasConfirmation(player))
			{
				CmdConfirm.addPending(accepter, C.MERGE_REQUEST_CONFIRM.s().replaceAll("%s", player.getName()), run);
			}
			else
			{
				run.run();
			}
		}
		if (!isOnline)
		{
			MainUtil.sendMessage(player, C.NO_AVAILABLE_AUTOMERGE);
			return false;
		}
		MainUtil.sendMessage(player, C.MERGE_REQUESTED);
		return true;
	}
}
