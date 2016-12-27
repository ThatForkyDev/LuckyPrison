package com.intellectualcrafters.plot.commands;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.StringMan;
import com.plotsquared.general.commands.CommandDeclaration;

@CommandDeclaration(command = "debug",
					category = CommandCategory.DEBUG,
					description = "Show debug information",
					usage = "/plot debug [msg]",
					permission = "plots.admin")
public class Debug extends SubCommand {

	@Override
	public boolean onCommand(PlotPlayer player, String... args)
	{
		if ((args.length > 0) && "msg".equalsIgnoreCase(args[0]))
		{
			StringBuilder msg = new StringBuilder();
			for (C caption : C.values())
			{
				msg.append(caption.s()).append('\n');
			}
			MainUtil.sendMessage(player, msg.toString());
			return true;
		}
		StringBuilder information = new StringBuilder();
		String header = C.DEBUG_HEADER.s();
		String line = C.DEBUG_LINE.s();
		String section = C.DEBUG_SECTION.s();
		information.append(header);
		information.append(this.getSection(section, "PlotArea"));
		information.append(this.getLine(line, "Plot Worlds", StringMan.join(PS.get().getPlotAreas(), ", ")));
		information.append(this.getLine(line, "Owned Plots", PS.get().getPlots().size()));
		information.append(this.getSection(section, "Messages"));
		information.append(this.getLine(line, "Total Messages", C.values().length));
		information.append(this.getLine(line, "View all captions", "/plot debug msg"));
		MainUtil.sendMessage(player, information.toString());
		return true;
	}

	private String getSection(String line, String val)
	{
		return line.replaceAll("%val%", val) + '\n';
	}

	private String getLine(String line, String var, Object val)
	{
		return line.replaceAll("%var%", var).replaceAll("%val%", String.valueOf(val)) + '\n';
	}
}
