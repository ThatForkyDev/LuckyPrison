package com.intellectualcrafters.plot.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.object.RunnableVal2;
import com.intellectualcrafters.plot.object.RunnableVal3;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.MathMan;
import com.intellectualcrafters.plot.util.Permissions;
import com.intellectualcrafters.plot.util.UUIDHandler;
import com.plotsquared.general.commands.Command;
import com.plotsquared.general.commands.CommandDeclaration;

@CommandDeclaration(
		command = "visit",
		permission = "plots.visit",
		description = "Visit someones plot",
		usage = "/plot visit [<player>|<alias>|<world>|<id>] [#]",
		aliases = {"v", "tp", "teleport", "goto", "home", "h"},
		requiredType = RequiredType.NONE,
		category = CommandCategory.TELEPORT)
public class Visit extends Command {

	public Visit()
	{
		super(MainCommand.getInstance(), true);
	}

	@Override
	public Collection<Command> tab(PlotPlayer player, String[] args, boolean space)
	{
		return this.tabOf(player, args, space, this.getUsage());
	}

	@Override
	public void execute(PlotPlayer player, String[] args, RunnableVal3<Command, Runnable, Runnable> confirm, RunnableVal2<Command, Command.CommandResult> whenDone) throws CommandException
	{
		if (args.length == 1 && args[0].contains(":"))
		{
			args = args[0].split(":");
		}
		int page = Integer.MIN_VALUE;
		Collection<Plot> unsorted = null;
		switch (args.length)
		{
			case 2:
				if (!MathMan.isInteger(args[1]))
				{
					C.NOT_VALID_NUMBER.send(player, "(1, ∞)");
					C.COMMAND_SYNTAX.send(player, this.getUsage());
					return;
				}
				page = Integer.parseInt(args[1]);
			case 1:
				UUID user = (args.length == 2 || !MathMan.isInteger(args[0])) ? UUIDHandler.getCachedUUID(args[0], null) : null;
				if (page == Integer.MIN_VALUE && user == null && MathMan.isInteger(args[0]))
				{
					page = Integer.parseInt(args[0]);
					unsorted = PS.get().getBasePlots(player);
					break;
				}
				if (user != null)
				{
					unsorted = PS.get().getBasePlots(user);
				}
				else
				{
					Plot plot = MainUtil.getPlotFromString(player, args[0], true);
					if (plot != null)
					{
						unsorted = Collections.singletonList(plot.getBasePlot(false));
					}
				}
				break;
			case 0:
				page = 1;
				unsorted = PS.get().getPlots(player);
				break;
			default:
		}
		if (page == Integer.MIN_VALUE)
		{
			page = 1;
		}
		if (unsorted == null || unsorted.isEmpty())
		{
			C.FOUND_NO_PLOTS.send(player);
			return;
		}
		Iterator<Plot> iterator = unsorted.iterator();
		while (iterator.hasNext())
		{
			if (!iterator.next().isBasePlot())
			{
				iterator.remove();
			}
		}
		if (page < 1 || page > unsorted.size())
		{
			C.NOT_VALID_NUMBER.send(player, "(1, " + unsorted.size() + ')');
			return;
		}
		List<Plot> plots = PS.get().sortPlotsByTemp(unsorted);
		Plot plot = plots.get(page - 1);
		if (!plot.hasOwner())
		{
			if (!Permissions.hasPermission(player, "plots.visit.unowned"))
			{
				C.NO_PERMISSION.send(player, "plots.visit.unowned");
				return;
			}
		}
		else if (plot.isOwner(player.getUUID()))
		{
			if (!Permissions.hasPermission(player, "plots.visit.owned") && !Permissions.hasPermission(player, "plots.home"))
			{
				C.NO_PERMISSION.send(player, "plots.visit.owned, plots.home");
				return;
			}
		}
		else if (plot.isAdded(player.getUUID()))
		{
			if (!Permissions.hasPermission(player, "plots.visit.shared"))
			{
				C.NO_PERMISSION.send(player, "plots.visit.shared");
				return;
			}
		}
		else
		{
			if (!Permissions.hasPermission(player, "plots.visit.other"))
			{
				C.NO_PERMISSION.send(player, "plots.visit.other");
				return;
			}
		}
		confirm.run(this, () ->
		{
			if (plot.teleportPlayer(player))
			{
				whenDone.run(this, CommandResult.SUCCESS);
			}
			else
			{
				whenDone.run(this, CommandResult.FAILURE);
			}
		}, () -> whenDone.run(this, CommandResult.FAILURE));
	}
}
