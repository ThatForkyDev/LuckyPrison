package com.intellectualcrafters.plot.commands;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.config.Settings;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.CmdConfirm;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.Permissions;
import com.intellectualcrafters.plot.util.TaskManager;
import com.plotsquared.general.commands.CommandDeclaration;

@CommandDeclaration(
		command = "delete",
		permission = "plots.delete",
		description = "Delete a plot",
		usage = "/plot delete",
		aliases = {"dispose", "del"},
		category = CommandCategory.CLAIMING,
		requiredType = RequiredType.NONE,
		confirmation = true)
public class Delete extends SubCommand {

	@Override
	public boolean onCommand(PlotPlayer player, String... args)
	{

		Location loc = player.getLocation();
		Plot plot = loc.getPlotAbs();
		if (plot == null)
		{
			return !this.sendMessage(player, C.NOT_IN_PLOT);
		}
		if (!plot.hasOwner())
		{
			return !this.sendMessage(player, C.PLOT_UNOWNED);
		}
		if (!plot.isOwner(player.getUUID()) && !Permissions.hasPermission(player, "plots.admin.command.delete"))
		{
			return !this.sendMessage(player, C.NO_PLOT_PERMS);
		}
		PlotArea plotArea = plot.getArea();
		java.util.Set<Plot> plots = plot.getConnectedPlots();
		int currentPlots = Settings.Limit.GLOBAL ? player.getPlotCount() : player.getPlotCount(loc.getWorld());
		Runnable run = () ->
		{
			if (plot.getRunning() > 0)
			{
				MainUtil.sendMessage(player, C.WAIT_FOR_TIMER);
				return;
			}
			long start = System.currentTimeMillis();
			boolean result = plot.deletePlot(() ->
											 {
												 plot.removeRunning();
												 MainUtil.sendMessage(player, C.CLEARING_DONE, System.currentTimeMillis() - start);
											 });
			if (result)
			{
				plot.addRunning();
			}
			else
			{
				MainUtil.sendMessage(player, C.WAIT_FOR_TIMER);
			}
		};
		if (this.hasConfirmation(player))
		{
			CmdConfirm.addPending(player, this.getCommandString() + ' ' + plot.getId(), run);
		}
		else
		{
			TaskManager.runTask(run);
		}
		return true;
	}
}
