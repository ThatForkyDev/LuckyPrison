package com.intellectualcrafters.plot.commands;

import java.util.Set;
import java.util.UUID;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.database.DBFunc;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.EventUtil;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.Permissions;
import com.intellectualcrafters.plot.util.PlotGameMode;
import com.intellectualcrafters.plot.util.UUIDHandler;
import com.intellectualcrafters.plot.util.WorldUtil;
import com.plotsquared.general.commands.Argument;
import com.plotsquared.general.commands.CommandDeclaration;

@CommandDeclaration(command = "deny",
					aliases = {"d", "ban"},
					description = "Deny a user from a plot",
					usage = "/plot deny <player>",
					category = CommandCategory.SETTINGS,
					requiredType = RequiredType.NONE)
public class Deny extends SubCommand {

	public Deny()
	{
		super(Argument.PlayerName);
	}

	@Override
	public boolean onCommand(PlotPlayer player, String... args)
	{

		Location location = player.getLocation();
		Plot plot = location.getPlotAbs();
		if (plot == null)
		{
			return !this.sendMessage(player, C.NOT_IN_PLOT);
		}
		if (!plot.hasOwner())
		{
			MainUtil.sendMessage(player, C.PLOT_UNOWNED);
			return false;
		}
		if (!plot.isOwner(player.getUUID()) && !Permissions.hasPermission(player, "plots.admin.command.deny"))
		{
			MainUtil.sendMessage(player, C.NO_PLOT_PERMS);
			return true;
		}
		Set<UUID> uuids = MainUtil.getUUIDsFromString(args[0]);
		if (uuids.isEmpty())
		{
			MainUtil.sendMessage(player, C.INVALID_PLAYER, args[0]);
			return false;
		}
		for (UUID uuid : uuids)
		{
			if (uuid == DBFunc.everyone && !(Permissions.hasPermission(player, "plots.deny.everyone") || Permissions.hasPermission(player, "plots.admin.command.deny")))
			{
				MainUtil.sendMessage(player, C.INVALID_PLAYER, MainUtil.getName(uuid));
				continue;
			}
			if (plot.isOwner(uuid))
			{
				MainUtil.sendMessage(player, C.ALREADY_OWNER, MainUtil.getName(uuid));
				return false;
			}

			if (plot.getDenied().contains(uuid))
			{
				MainUtil.sendMessage(player, C.ALREADY_ADDED, MainUtil.getName(uuid));
				return false;
			}
			plot.removeMember(uuid);
			plot.removeTrusted(uuid);
			plot.addDenied(uuid);
			EventUtil.manager.callDenied(player, plot, uuid, true);
			if (!uuid.equals(DBFunc.everyone))
			{
				this.handleKick(UUIDHandler.getPlayer(uuid), plot);
			}
			else
			{
				for (PlotPlayer plotPlayer : plot.getPlayersInPlot())
				{
					this.handleKick(plotPlayer, plot);
				}
			}
		}
		if (!uuids.isEmpty())
		{
			MainUtil.sendMessage(player, C.DENIED_ADDED);
		}
		return true;
	}

	private void handleKick(PlotPlayer player, Plot plot)
	{
		if (player == null)
		{
			return;
		}
		if (!plot.equals(player.getCurrentPlot()))
		{
			return;
		}
		if (player.hasPermission("plots.admin.entry.denied"))
		{
			return;
		}
		if (player.getGameMode() == PlotGameMode.SPECTATOR)
		{
			player.stopSpectating();
		}
		player.teleport(WorldUtil.IMP.getSpawn(player.getLocation().getWorld()));
		MainUtil.sendMessage(player, C.YOU_GOT_DENIED);
	}
}
