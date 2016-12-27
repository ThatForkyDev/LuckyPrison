package com.intellectualcrafters.plot.commands;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.config.Settings;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.object.RunnableVal;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.Permissions;
import com.intellectualcrafters.plot.util.SchematicHandler;
import com.intellectualcrafters.plot.util.TaskManager;
import com.plotsquared.general.commands.CommandDeclaration;

@CommandDeclaration(command = "load", aliases = "restore", category = CommandCategory.SCHEMATIC, requiredType = RequiredType.NONE, description = "Load your plot", permission = "plots.load", usage = "/plot restore")
public class Load extends SubCommand {

	@Override
	public boolean onCommand(PlotPlayer player, String... args)
	{
		String world = player.getLocation().getWorld();
		if (!PS.get().hasPlotArea(world))
		{
			return !this.sendMessage(player, C.NOT_IN_PLOT_WORLD);
		}
		Plot plot = player.getCurrentPlot();
		if (plot == null)
		{
			return !this.sendMessage(player, C.NOT_IN_PLOT);
		}
		if (!plot.hasOwner())
		{
			MainUtil.sendMessage(player, C.PLOT_UNOWNED);
			return false;
		}
		if (!plot.isOwner(player.getUUID()) && !Permissions.hasPermission(player, "plots.admin.command.load"))
		{
			MainUtil.sendMessage(player, C.NO_PLOT_PERMS);
			return false;
		}
		if (plot.getRunning() > 0)
		{
			MainUtil.sendMessage(player, C.WAIT_FOR_TIMER);
			return false;
		}

		if (args.length != 0)
		{
			if (args.length == 1)
			{
				List<String> schematics = player.getMeta("plot_schematics");
				if (schematics == null)
				{
					// No schematics found:
					MainUtil.sendMessage(player, C.LOAD_NULL);
					return false;
				}
				String schematic;
				try
				{
					schematic = schematics.get(Integer.parseInt(args[0]) - 1);
				}
				catch (Exception ignored)
				{
					// use /plot load <index>
					MainUtil.sendMessage(player, C.NOT_VALID_NUMBER, "(1, " + schematics.size() + ')');
					return false;
				}
				URL url;
				try
				{
					url = new URL(Settings.Web.URL + "saves/" + player.getUUID() + '/' + schematic);
				}
				catch (MalformedURLException e)
				{
					e.printStackTrace();
					MainUtil.sendMessage(player, C.LOAD_FAILED);
					return false;
				}
				plot.addRunning();
				MainUtil.sendMessage(player, C.GENERATING_COMPONENT);
				TaskManager.runTaskAsync(() ->
										 {
											 SchematicHandler.Schematic schematic1 = SchematicHandler.manager.getSchematic(url);
											 if (schematic1 == null)
											 {
												 plot.removeRunning();
												 this.sendMessage(player, C.SCHEMATIC_INVALID, "non-existent or not in gzip format");
												 return;
											 }
											 SchematicHandler.manager.paste(schematic1, plot, 0, 0, 0, true, new RunnableVal<Boolean>() {
												 @Override
												 public void run(Boolean value)
												 {
													 plot.removeRunning();
													 if (value)
													 {
														 Load.this.sendMessage(player, C.SCHEMATIC_PASTE_SUCCESS);
													 }
													 else
													 {
														 Load.this.sendMessage(player, C.SCHEMATIC_PASTE_FAILED);
													 }
												 }
											 });
										 });
				return true;
			}
			plot.removeRunning();
			MainUtil.sendMessage(player, C.COMMAND_SYNTAX, "/plot load <index>");
			return false;
		}

		// list schematics

		List<String> schematics = player.getMeta("plot_schematics");
		if (schematics == null)
		{
			plot.addRunning();
			TaskManager.runTaskAsync(() ->
									 {
										 List<String> schematics1 = SchematicHandler.manager.getSaves(player.getUUID());
										 plot.removeRunning();
										 if ((schematics1 == null) || schematics1.isEmpty())
										 {
											 MainUtil.sendMessage(player, C.LOAD_FAILED);
											 return;
										 }
										 player.setMeta("plot_schematics", schematics1);
										 this.displaySaves(player);
									 });
		}
		else
		{
			this.displaySaves(player);
		}
		return true;
	}

	public void displaySaves(PlotPlayer player)
	{
		List<String> schematics = player.getMeta("plot_schematics");
		for (int i = 0; i < Math.min(schematics.size(), 32); i++)
		{
			try
			{
				String schematic = schematics.get(i);
				String[] split = schematic.split("_");
				if (split.length != 6)
				{
					continue;
				}
				String time = this.secToTime((System.currentTimeMillis() / 1000) - Long.parseLong(split[0]));
				String world = split[1];
				PlotId id = PlotId.fromString(split[2] + ';' + split[3]);
				String size = split[4];
				String color = "$4";
				MainUtil.sendMessage(player,
									 "$3[$2" + (i + 1) + "$3] " + color + time + "$3 | " + color + world + ';' + id + "$3 | " + color + size + 'x' + size);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		MainUtil.sendMessage(player, C.LOAD_LIST);
	}

	public String secToTime(long time)
	{
		StringBuilder toreturn = new StringBuilder();
		if (time >= 33868800)
		{
			int years = (int) (time / 33868800);
			time -= years * 33868800;
			toreturn.append(years).append("y ");
		}
		if (time >= 604800)
		{
			int weeks = (int) (time / 604800);
			time -= weeks * 604800;
			toreturn.append(weeks).append("w ");
		}
		if (time >= 86400)
		{
			int days = (int) (time / 86400);
			time -= days * 86400;
			toreturn.append(days).append("d ");
		}
		if (time >= 3600)
		{
			int hours = (int) (time / 3600);
			time -= hours * 3600;
			toreturn.append(hours).append("h ");
		}
		if (time >= 60)
		{
			int minutes = (int) (time / 60);
			time -= minutes * 60;
			toreturn.append(minutes).append("m ");
		}
		if (toreturn.length() == 0 || (time > 0))
		{
			toreturn.append(time).append("s ");
		}
		return toreturn.toString().trim();
	}
}
