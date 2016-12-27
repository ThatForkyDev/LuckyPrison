package com.intellectualcrafters.plot.commands;

import java.util.ArrayList;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.database.DBFunc;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.MainUtil;
import com.plotsquared.general.commands.CommandDeclaration;

@CommandDeclaration(
		command = "debugsavetest",
		permission = "plots.debugsavetest",
		category = CommandCategory.DEBUG,
		requiredType = RequiredType.CONSOLE,
		usage = "/plot debugsavetest",
		description = "This command will force the recreation of all plots in the DB")
public class DebugSaveTest extends SubCommand {

	@Override
	public boolean onCommand(PlotPlayer player, String... args)
	{
		ArrayList<Plot> plots = new ArrayList<>(PS.get().getPlots());
		MainUtil.sendMessage(player, "&6Starting `DEBUGSAVETEST`");
		DBFunc.createPlotsAndData(plots, () -> MainUtil.sendMessage(player, "&6Database sync finished!"));
		return true;
	}
}
