package com.intellectualcrafters.plot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.flag.Flags;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotMessage;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.object.Rating;
import com.intellectualcrafters.plot.object.RunnableVal3;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.MathMan;
import com.intellectualcrafters.plot.util.Permissions;
import com.intellectualcrafters.plot.util.StringComparison;
import com.intellectualcrafters.plot.util.StringMan;
import com.intellectualcrafters.plot.util.UUIDHandler;
import com.intellectualcrafters.plot.util.expiry.ExpireManager;
import com.plotsquared.general.commands.CommandDeclaration;

@CommandDeclaration(
		command = "list",
		aliases = {"l", "find", "search"},
		description = "List plots",
		permission = "plots.list",
		category = CommandCategory.INFO,
		usage = "/plot list <mine|shared|world|top|all|unowned|unknown|player|world|done|fuzzy <search...>> [#]")
public class ListCmd extends SubCommand {

	private String[] getArgumentList(PlotPlayer player)
	{
		List<String> args = new ArrayList<>();
		if (Permissions.hasPermission(player, "plots.list.mine"))
		{
			args.add("mine");
		}
		if (Permissions.hasPermission(player, "plots.list.shared"))
		{
			args.add("shared");
		}
		if (Permissions.hasPermission(player, "plots.list.world"))
		{
			args.add("world");
		}
		if (Permissions.hasPermission(player, "plots.list.top"))
		{
			args.add("top");
		}
		if (Permissions.hasPermission(player, "plots.list.all"))
		{
			args.add("all");
		}
		if (Permissions.hasPermission(player, "plots.list.unowned"))
		{
			args.add("unowned");
		}
		if (Permissions.hasPermission(player, "plots.list.unknown"))
		{
			args.add("unknown");
		}
		if (Permissions.hasPermission(player, "plots.list.player"))
		{
			args.add("<player>");
		}
		if (Permissions.hasPermission(player, "plots.list.world"))
		{
			args.add("<world>");
		}
		if (Permissions.hasPermission(player, "plots.list.done"))
		{
			args.add("done");
		}
		if (Permissions.hasPermission(player, "plots.list.expired"))
		{
			args.add("expired");
		}
		if (Permissions.hasPermission(player, "plots.list.fuzzy"))
		{
			args.add("fuzzy <search...>");
		}
		return args.toArray(new String[args.size()]);
	}

	public void noArgs(PlotPlayer player)
	{
		MainUtil.sendMessage(player, C.SUBCOMMAND_SET_OPTIONS_HEADER.s() + Arrays.toString(this.getArgumentList(player)));
	}

	@Override
	public boolean onCommand(PlotPlayer player, String... args)
	{
		if (args.length < 1)
		{
			this.noArgs(player);
			return false;
		}
		int page = 0;
		if (args.length > 1)
		{
			try
			{
				page = Integer.parseInt(args[args.length - 1]);
				--page;
				if (page < 0)
				{
					page = 0;
				}
			}
			catch (NumberFormatException ignored)
			{
				page = -1;
			}
		}

		List<Plot> plots = null;

		String world = player.getLocation().getWorld();
		PlotArea area = player.getApplicablePlotArea();
		String arg = args[0].toLowerCase();
		boolean sort = true;
		switch (arg)
		{
			case "mine":
				if (!Permissions.hasPermission(player, "plots.list.mine"))
				{
					MainUtil.sendMessage(player, C.NO_PERMISSION, "plots.list.mine");
					return false;
				}
				sort = false;
				plots = PS.get().sortPlotsByTemp(PS.get().getBasePlots(player));
				break;
			case "shared":
				if (!Permissions.hasPermission(player, "plots.list.shared"))
				{
					MainUtil.sendMessage(player, C.NO_PERMISSION, "plots.list.shared");
					return false;
				}
				plots = new ArrayList<>();
				for (Plot plot : PS.get().getPlots())
				{
					if (plot.getTrusted().contains(player.getUUID()) || plot.getMembers().contains(player.getUUID()))
					{
						plots.add(plot);
					}
				}
				break;
			case "world":
				if (!Permissions.hasPermission(player, "plots.list.world"))
				{
					MainUtil.sendMessage(player, C.NO_PERMISSION, "plots.list.world");
					return false;
				}
				if (!Permissions.hasPermission(player, "plots.list.world." + world))
				{
					MainUtil.sendMessage(player, C.NO_PERMISSION, "plots.list.world." + world);
					return false;
				}
				plots = new ArrayList<>(PS.get().getPlots(world));
				break;
			case "expired":
				if (!Permissions.hasPermission(player, "plots.list.expired"))
				{
					MainUtil.sendMessage(player, C.NO_PERMISSION, "plots.list.expired");
					return false;
				}
				plots = ExpireManager.IMP == null ? new ArrayList<>() : new ArrayList<>(ExpireManager.IMP.getPendingExpired());
				break;
			case "area":
				if (!Permissions.hasPermission(player, "plots.list.area"))
				{
					MainUtil.sendMessage(player, C.NO_PERMISSION, "plots.list.area");
					return false;
				}
				if (!Permissions.hasPermission(player, "plots.list.world." + world))
				{
					MainUtil.sendMessage(player, C.NO_PERMISSION, "plots.list.world." + world);
					return false;
				}
				plots = area == null ? new ArrayList<>() : new ArrayList<>(area.getPlots());
				break;
			case "all":
				if (!Permissions.hasPermission(player, "plots.list.all"))
				{
					MainUtil.sendMessage(player, C.NO_PERMISSION, "plots.list.all");
					return false;
				}
				plots = new ArrayList<>(PS.get().getPlots());
				break;
			case "done":
				if (!Permissions.hasPermission(player, "plots.list.done"))
				{
					MainUtil.sendMessage(player, C.NO_PERMISSION, "plots.list.done");
					return false;
				}
				plots = new ArrayList<>();
				for (Plot plot : PS.get().getPlots())
				{
					Optional<String> flag = plot.getFlag(Flags.DONE);
					if (!flag.isPresent())
					{
						continue;
					}
					plots.add(plot);
				}
				Collections.sort(plots, (a, b) ->
				{
					String va = (String) a.getFlags().get(Flags.DONE);
					String vb = (String) b.getFlags().get(Flags.DONE);
					if (MathMan.isInteger(va))
					{
						if (MathMan.isInteger(vb))
						{
							return Integer.parseInt(vb) - Integer.parseInt(va);
						}
						return -1;
					}
					return 1;
				});
				sort = false;
				break;
			case "top":
				if (!Permissions.hasPermission(player, "plots.list.top"))
				{
					MainUtil.sendMessage(player, C.NO_PERMISSION, "plots.list.top");
					return false;
				}
				plots = new ArrayList<>(PS.get().getPlots());
				Collections.sort(plots, (p1, p2) ->
				{
					double v1 = 0;
					int p1s = p1.getSettings().getRatings().size();
					int p2s = p2.getRatings().size();
					if (!p1.getSettings().getRatings().isEmpty())
					{
						for (Map.Entry<UUID, Rating> entry : p1.getRatings().entrySet())
						{
							double av = entry.getValue().getAverageRating();
							v1 += av * av;
						}
						v1 /= p1s;
						v1 += p1s;
					}
					double v2 = 0;
					if (!p2.getSettings().getRatings().isEmpty())
					{
						for (Map.Entry<UUID, Rating> entry : p2.getRatings().entrySet())
						{
							double av = entry.getValue().getAverageRating();
							v2 += av * av;
						}
						v2 /= p2s;
						v2 += p2s;
					}
					if (v2 == v1 && v2 != 0)
					{
						return p2s - p1s;
					}
					return (int) Math.signum(v2 - v1);
				});
				sort = false;
				break;
			case "unowned":
				if (!Permissions.hasPermission(player, "plots.list.unowned"))
				{
					MainUtil.sendMessage(player, C.NO_PERMISSION, "plots.list.unowned");
					return false;
				}
				plots = new ArrayList<>();
				for (Plot plot : PS.get().getPlots())
				{
					if (plot.owner == null)
					{
						plots.add(plot);
					}
				}
				break;
			case "unknown":
				if (!Permissions.hasPermission(player, "plots.list.unknown"))
				{
					MainUtil.sendMessage(player, C.NO_PERMISSION, "plots.list.unknown");
					return false;
				}
				plots = new ArrayList<>();
				for (Plot plot : PS.get().getPlots())
				{
					if (plot.owner == null)
					{
						continue;
					}
					if (UUIDHandler.getName(plot.owner) == null)
					{
						plots.add(plot);
					}
				}
				break;
			case "fuzzy":
				if (!Permissions.hasPermission(player, "plots.list.fuzzy"))
				{
					MainUtil.sendMessage(player, C.NO_PERMISSION, "plots.list.fuzzy");
					return false;
				}
				if (args.length < (page == -1 ? 2 : 3))
				{
					C.COMMAND_SYNTAX.send(player, "/plot list fuzzy <search...> [#]");
					return false;
				}
				String term;
				term = MathMan.isInteger(args[args.length - 1]) ? StringMan.join(Arrays.copyOfRange(args, 1, args.length - 1), " ") : StringMan.join(Arrays.copyOfRange(args, 1, args.length), " ");
				plots = MainUtil.getPlotsBySearch(term);
				sort = false;
				break;
			default:
				if (PS.get().hasPlotArea(args[0]))
				{
					if (!Permissions.hasPermission(player, "plots.list.world"))
					{
						MainUtil.sendMessage(player, C.NO_PERMISSION, "plots.list.world");
						return false;
					}
					if (!Permissions.hasPermission(player, "plots.list.world." + args[0]))
					{
						MainUtil.sendMessage(player, C.NO_PERMISSION, "plots.list.world." + args[0]);
						return false;
					}
					plots = new ArrayList<>(PS.get().getPlots(args[0]));
					break;
				}
				UUID uuid = UUIDHandler.getUUID(args[0], null);
				if (uuid == null)
				{
					try
					{
						uuid = UUID.fromString(args[0]);
					}
					catch (Exception ignored) {}
				}
				if (uuid != null)
				{
					if (!Permissions.hasPermission(player, "plots.list.player"))
					{
						MainUtil.sendMessage(player, C.NO_PERMISSION, "plots.list.player");
						return false;
					}
					sort = false;
					plots = PS.get().sortPlotsByTemp(PS.get().getPlots(uuid));
					break;
				}
		}

		if (plots == null)
		{
			this.sendMessage(player, C.DID_YOU_MEAN, new StringComparison<>(args[0], new String[]{"mine", "shared", "world", "all"}).getBestMatch());
			return false;
		}

		if (plots.isEmpty())
		{
			MainUtil.sendMessage(player, C.FOUND_NO_PLOTS);
			return false;
		}
		this.displayPlots(player, plots, 12, page, area, args, sort);
		return true;
	}

	public void displayPlots(PlotPlayer player, List<Plot> plots, int pageSize, int page, PlotArea area,
							 String[] args, boolean sort)
	{
		// Header
		Iterator<Plot> iterator = plots.iterator();
		while (iterator.hasNext())
		{
			if (!iterator.next().isBasePlot())
			{
				iterator.remove();
			}
		}
		if (sort)
		{
			plots = PS.get().sortPlots(plots, PS.SortType.CREATION_DATE, area);
		}
		this.paginate(player, plots, pageSize, page, new RunnableVal3<Integer, Plot, PlotMessage>() {
			@Override
			public void run(Integer i, Plot plot, PlotMessage message)
			{
				String color;
				if (plot.owner == null)
				{
					color = "$3";
				}
				else if (plot.isOwner(player.getUUID()))
				{
					color = "$1";
				}
				else if (plot.isAdded(player.getUUID()))
				{
					color = "$4";
				}
				else if (plot.isDenied(player.getUUID()))
				{
					color = "$2";
				}
				else
				{
					color = "$1";
				}
				PlotMessage trusted =
						new PlotMessage().text(C.color(C.PLOT_INFO_TRUSTED.s().replaceAll("%trusted%", MainUtil.getPlayerList(plot.getTrusted()))))
										 .color("$1");
				PlotMessage members =
						new PlotMessage().text(C.color(C.PLOT_INFO_MEMBERS.s().replaceAll("%members%", MainUtil.getPlayerList(plot.getMembers()))))
										 .color("$1");
				String strFlags = StringMan.join(plot.getFlags().values(), ",");
				if (strFlags.isEmpty())
				{
					strFlags = C.NONE.s();
				}
				PlotMessage flags = new PlotMessage().text(C.color(C.PLOT_INFO_FLAGS.s().replaceAll("%flags%", strFlags))).color("$1");
				message.text("[").color("$3").text(String.valueOf(i)).command("/plot visit " + plot.getArea() + ';' + plot.getId())
					   .tooltip("/plot visit " + plot.getArea() + ';' + plot.getId()).color("$1")
					   .text("]")
					   .color("$3").text(" " + plot).tooltip(trusted, members, flags)
					   .command("/plot info " + plot.getArea() + ';' + plot.getId()).color(color).text(" - ").color("$2");
				String prefix = "";
				for (UUID uuid : plot.getOwners())
				{
					String name = UUIDHandler.getName(uuid);
					if (name == null)
					{
						message = message.text(prefix).color("$4").text("unknown").color("$2").tooltip(uuid.toString()).suggest(uuid.toString());
					}
					else
					{
						PlotPlayer pp = UUIDHandler.getPlayer(uuid);
						message = pp != null ? message.text(prefix).color("$4").text(name).color("$1").tooltip(new PlotMessage("Online").color("$4")) : message.text(prefix).color("$4").text(name).color("$1").tooltip(new PlotMessage("Offline").color("$3"));
					}
					prefix = ", ";
				}
			}
		}, "/plot list " + args[0], C.PLOT_LIST_HEADER_PAGED.s());
	}
}
