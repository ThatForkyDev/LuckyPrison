package com.intellectualcrafters.plot.commands;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.CmdConfirm;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.Permissions;
import com.intellectualcrafters.plot.util.StringMan;
import com.intellectualcrafters.plot.util.TaskManager;
import com.plotsquared.general.commands.CommandDeclaration;

@CommandDeclaration(command = "unlink",
					aliases = {"u", "unmerge"},
					description = "Unlink a mega-plot",
					usage = "/plot unlink",
					requiredType = RequiredType.NONE,
					category = CommandCategory.SETTINGS,
					confirmation = true)
public class Unlink extends SubCommand {

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
		if (!plot.isOwner(player.getUUID()) && !Permissions.hasPermission(player, "plots.admin.command.unlink"))
		{
			return this.sendMessage(player, C.NO_PLOT_PERMS);
		}
		if (!plot.isMerged())
		{
			return this.sendMessage(player, C.UNLINK_IMPOSSIBLE);
		}
		boolean createRoad;
		if (args.length != 0)
		{
			if (args.length != 1 || !StringMan.isEqualIgnoreCaseToAny(args[0], "true", "false"))
			{
				C.COMMAND_SYNTAX.send(player, this.getUsage());
				return false;
			}
			createRoad = Boolean.parseBoolean(args[0]);
		}
		else
		{
			createRoad = true;
		}
		Runnable runnable = () ->
		{
			if (!plot.unlinkPlot(createRoad, createRoad))
			{
				MainUtil.sendMessage(player, "&cUnlink has been cancelled");
				return;
			}
			MainUtil.sendMessage(player, C.UNLINK_SUCCESS);
		};
		if (this.hasConfirmation(player))
		{
			CmdConfirm.addPending(player, "/plot unlink " + plot.getId(), runnable);
		}
		else
		{
			TaskManager.runTask(runnable);
		}
		return true;
	}
}
