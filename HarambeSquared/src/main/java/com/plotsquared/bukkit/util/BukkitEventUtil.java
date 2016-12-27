package com.plotsquared.bukkit.util;

import java.util.ArrayList;
import java.util.UUID;

import com.intellectualcrafters.plot.flag.Flag;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotCluster;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.object.Rating;
import com.intellectualcrafters.plot.util.EventUtil;
import com.plotsquared.bukkit.events.ClusterFlagRemoveEvent;
import com.plotsquared.bukkit.events.PlayerClaimPlotEvent;
import com.plotsquared.bukkit.events.PlayerEnterPlotEvent;
import com.plotsquared.bukkit.events.PlayerLeavePlotEvent;
import com.plotsquared.bukkit.events.PlayerPlotDeniedEvent;
import com.plotsquared.bukkit.events.PlayerPlotHelperEvent;
import com.plotsquared.bukkit.events.PlayerPlotTrustedEvent;
import com.plotsquared.bukkit.events.PlayerTeleportToPlotEvent;
import com.plotsquared.bukkit.events.PlotClearEvent;
import com.plotsquared.bukkit.events.PlotComponentSetEvent;
import com.plotsquared.bukkit.events.PlotDeleteEvent;
import com.plotsquared.bukkit.events.PlotFlagAddEvent;
import com.plotsquared.bukkit.events.PlotFlagRemoveEvent;
import com.plotsquared.bukkit.events.PlotMergeEvent;
import com.plotsquared.bukkit.events.PlotRateEvent;
import com.plotsquared.bukkit.events.PlotUnlinkEvent;
import com.plotsquared.bukkit.object.BukkitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class BukkitEventUtil extends EventUtil {

	public Player getPlayer(PlotPlayer player)
	{
		if (player instanceof BukkitPlayer)
		{
			return ((BukkitPlayer) player).player;
		}
		return null;
	}

	public boolean callEvent(Event event)
	{
		Bukkit.getServer().getPluginManager().callEvent(event);
		return !(event instanceof Cancellable) || !((Cancellable) event).isCancelled();
	}

	@Override
	public boolean callClaim(PlotPlayer player, Plot plot, boolean auto)
	{
		return this.callEvent(new PlayerClaimPlotEvent(this.getPlayer(player), plot, auto));
	}

	@Override
	public boolean callTeleport(PlotPlayer player, Location from, Plot plot)
	{
		return this.callEvent(new PlayerTeleportToPlotEvent(this.getPlayer(player), from, plot));
	}

	@Override
	public boolean callComponentSet(Plot plot, String component)
	{
		return this.callEvent(new PlotComponentSetEvent(plot, component));
	}

	@Override
	public boolean callClear(Plot plot)
	{
		return this.callEvent(new PlotClearEvent(plot));
	}

	@Override
	public void callDelete(Plot plot)
	{
		this.callEvent(new PlotDeleteEvent(plot));
	}

	@Override
	public boolean callFlagAdd(Flag flag, Plot plot)
	{
		return this.callEvent(new PlotFlagAddEvent(flag, plot));
	}

	@Override
	public boolean callFlagRemove(Flag<?> flag, Plot plot, Object value)
	{
		return this.callEvent(new PlotFlagRemoveEvent(flag, plot));
	}

	@Override
	public boolean callMerge(Plot plot, ArrayList<PlotId> plots)
	{
		return this.callEvent(new PlotMergeEvent(BukkitUtil.getWorld(plot.getArea().worldname), plot, plots));
	}

	@Override
	public boolean callUnlink(PlotArea area, ArrayList<PlotId> plots)
	{
		return this.callEvent(new PlotUnlinkEvent(BukkitUtil.getWorld(area.worldname), area, plots));
	}

	@Override
	public void callEntry(PlotPlayer player, Plot plot)
	{
		this.callEvent(new PlayerEnterPlotEvent(this.getPlayer(player), plot));
	}

	@Override
	public void callLeave(PlotPlayer player, Plot plot)
	{
		this.callEvent(new PlayerLeavePlotEvent(this.getPlayer(player), plot));
	}

	@Override
	public void callDenied(PlotPlayer initiator, Plot plot, UUID player, boolean added)
	{
		this.callEvent(new PlayerPlotDeniedEvent(this.getPlayer(initiator), plot, player, added));
	}

	@Override
	public void callTrusted(PlotPlayer initiator, Plot plot, UUID player, boolean added)
	{
		this.callEvent(new PlayerPlotTrustedEvent(this.getPlayer(initiator), plot, player, added));
	}

	@Override
	public void callMember(PlotPlayer initiator, Plot plot, UUID player, boolean added)
	{
		this.callEvent(new PlayerPlotHelperEvent(this.getPlayer(initiator), plot, player, added));
	}

	@Override
	public boolean callFlagRemove(Flag flag, Object object, PlotCluster cluster)
	{
		return this.callEvent(new ClusterFlagRemoveEvent(flag, cluster));
	}

	@Override
	public Rating callRating(PlotPlayer player, Plot plot, Rating rating)
	{
		PlotRateEvent event = new PlotRateEvent(player, rating, plot);
		Bukkit.getServer().getPluginManager().callEvent(event);
		return event.getRating();
	}
}
