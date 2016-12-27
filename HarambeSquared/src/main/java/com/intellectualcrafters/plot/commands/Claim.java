package com.intellectualcrafters.plot.commands;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.config.Settings;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.ByteArrayUtilities;
import com.intellectualcrafters.plot.util.Permissions;
import com.plotsquared.general.commands.CommandDeclaration;

@CommandDeclaration(command = "claim",
					aliases = "c",
					description = "Claim the current plot you're standing on",
					category = CommandCategory.CLAIMING,
					requiredType = RequiredType.NONE,
					permission = "plots.claim", usage = "/plot claim")
public class Claim extends SubCommand {

	@Override
	public boolean onCommand(PlotPlayer player, String... args)
	{
		String schematic = "";
		if (args.length >= 1)
		{
			schematic = args[0];
		}
		Location loc = player.getLocation();
		Plot plot = loc.getPlotAbs();
		if (plot == null)
		{
			return this.sendMessage(player, C.NOT_IN_PLOT);
		}
		int currentPlots = Settings.Limit.GLOBAL ? player.getPlotCount() : player.getPlotCount(loc.getWorld());
		int grants = 0;
		if (currentPlots >= player.getAllowedPlots())
		{
			if (player.hasPersistentMeta("grantedPlots"))
			{
				grants = ByteArrayUtilities.bytesToInteger(player.getPersistentMeta("grantedPlots"));
				if (grants <= 0)
				{
					player.removePersistentMeta("grantedPlots");
					return this.sendMessage(player, C.CANT_CLAIM_MORE_PLOTS);
				}
			}
			else
			{
				return this.sendMessage(player, C.CANT_CLAIM_MORE_PLOTS);
			}
		}
		if (!plot.canClaim(player))
		{
			return this.sendMessage(player, C.PLOT_IS_CLAIMED);
		}
		PlotArea world = plot.getArea();
		if (grants > 0)
		{
			if (grants == 1)
			{
				player.removePersistentMeta("grantedPlots");
			}
			else
			{
				player.setPersistentMeta("grantedPlots", ByteArrayUtilities.integerToBytes(grants - 1));
			}
			this.sendMessage(player, C.REMOVED_GRANTED_PLOT, "1", String.valueOf(grants - 1));
		}
		if (!schematic.isEmpty())
		{
			if (world.SCHEMATIC_CLAIM_SPECIFY)
			{
				if (!world.SCHEMATICS.contains(schematic.toLowerCase()))
				{
					return this.sendMessage(player, C.SCHEMATIC_INVALID, "non-existent: " + schematic);
				}
				if (!Permissions.hasPermission(player, "plots.claim." + schematic) && !Permissions.hasPermission(player, "plots.admin.command.schematic"))
				{
					return this.sendMessage(player, C.NO_SCHEMATIC_PERMISSION, schematic);
				}
			}
		}
		return plot.claim(player, false, schematic) || this.sendMessage(player, C.PLOT_NOT_CLAIMED);
	}
}
