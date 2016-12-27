package com.intellectualcrafters.plot.commands;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.config.Settings;
import com.intellectualcrafters.plot.flag.FlagManager;
import com.intellectualcrafters.plot.flag.Flags;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.object.RunnableVal2;
import com.intellectualcrafters.plot.object.RunnableVal3;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.Permissions;
import com.intellectualcrafters.plot.util.block.GlobalBlockQueue;
import com.plotsquared.general.commands.Command;
import com.plotsquared.general.commands.CommandDeclaration;

@CommandDeclaration(command = "clear",
					description = "Clear a plot",
					permission = "plots.clear",
					category = CommandCategory.APPEARANCE,
					usage = "/plot clear",
					aliases = "reset",
					confirmation = true)
public class Clear extends Command {

	// Note: To clear a specific plot use /plot <plot> clear
	// The syntax also works with any command: /plot <plot> <command>

	public Clear()
	{
		super(MainCommand.getInstance(), true);
	}

	@Override
	public void execute(PlotPlayer player, String[] args, RunnableVal3<Command, Runnable, Runnable> confirm, RunnableVal2<Command, CommandResult> whenDone) throws CommandException
	{
		this.checkTrue(args.length == 0, C.COMMAND_SYNTAX, this.getUsage());
		Plot plot = this.check(player.getCurrentPlot(), C.NOT_IN_PLOT);
		this.checkTrue(plot.isOwner(player.getUUID()) || Permissions.hasPermission(player, "plots.admin.command.clear"), C.NO_PLOT_PERMS);
		this.checkTrue(plot.getRunning() == 0, C.WAIT_FOR_TIMER);
		this.checkTrue(!Settings.Done.RESTRICT_BUILDING || !Flags.DONE.isSet(plot) || Permissions.hasPermission(player, "plots.continue"), C.DONE_ALREADY_DONE);
		confirm.run(this, () ->
		{
			long start = System.currentTimeMillis();
			boolean result = plot.clear(true, false, () ->
			{
				plot.unlink();
				GlobalBlockQueue.IMP.addTask(() ->
											 {
												 plot.removeRunning();
												 // If the state changes, then mark it as no longer done
												 if (plot.getFlag(Flags.DONE).isPresent())
												 {
													 FlagManager.removePlotFlag(plot, Flags.DONE);
												 }
												 if (plot.getFlag(Flags.ANALYSIS).isPresent())
												 {
													 FlagManager.removePlotFlag(plot, Flags.ANALYSIS);
												 }
												 MainUtil.sendMessage(player, C.CLEARING_DONE, String.valueOf(System.currentTimeMillis() - start));
											 });
			});
			if (!result)
			{
				MainUtil.sendMessage(player, C.WAIT_FOR_TIMER);
			}
			else
			{
				plot.addRunning();
			}
		}, null);
	}
}
