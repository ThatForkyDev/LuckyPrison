package com.plotsquared.bukkit.object;

import java.util.UUID;

import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.PlotGameMode;
import com.intellectualcrafters.plot.util.PlotWeather;
import com.intellectualcrafters.plot.util.StringMan;
import com.intellectualcrafters.plot.util.UUIDHandler;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.ulfric.lib.api.hook.Hooks;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.RegisteredListener;

public class BukkitPlayer extends PlotPlayer {

	public final Player player;
	public boolean offline;
	private UUID uuid;
	private String name;

	/**
	 * <p>Please do not use this method. Instead use
	 * BukkitUtil.getPlayer(Player), as it caches player objects.</p>
	 *
	 * @param player
	 */
	public BukkitPlayer(Player player)
	{
		this.player = player;
		this.populatePersistentMetaMap();
	}

	public BukkitPlayer(Player player, boolean offline)
	{
		this.player = player;
		this.offline = offline;
		this.populatePersistentMetaMap();
	}

	@Override
	public Location getLocation()
	{
		Location location = super.getLocation();
		return location == null ? BukkitUtil.getLocation(this.player) : location;
	}

	@Override
	public UUID getUUID()
	{
		if (this.uuid == null)
		{
			this.uuid = UUIDHandler.getUUID(this);
		}
		return this.uuid;
	}

	@Override public long getLastPlayed()
	{
		return this.player.getLastPlayed();
	}

	@Override
	public boolean canTeleport(Location loc)
	{
		org.bukkit.Location to = BukkitUtil.getLocation(loc);
		org.bukkit.Location from = this.player.getLocation();
		PlayerTeleportEvent event = new PlayerTeleportEvent(this.player, from, to);
		RegisteredListener[] listeners = event.getHandlers().getRegisteredListeners();
		for (RegisteredListener listener : listeners)
		{
			if ("PlotSquared".equals(listener.getPlugin().getName()))
			{
				continue;
			}
			try
			{
				listener.callEvent(event);
			}
			catch (EventException e)
			{
				e.printStackTrace();
			}
		}
		if (event.isCancelled() || !event.getTo().equals(to))
		{
			return false;
		}
		event = new PlayerTeleportEvent(this.player, to, from);
		for (RegisteredListener listener : listeners)
		{
			if ("PlotSquared".equals(listener.getPlugin().getName()))
			{
				continue;
			}
			try
			{
				listener.callEvent(event);
			}
			catch (EventException e)
			{
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public boolean hasPermission(String permission)
	{
		if (this.offline)
		{
			Hooks.PERMISSIONS.user(this.uuid).hasPermission(permission);
		}
		return this.player.hasPermission(permission);
	}

	@Override
	public void sendMessage(String message)
	{
		if (!StringMan.isEqual(this.getMeta("lastMessage"), message) || (System.currentTimeMillis() - this.<Long>getMeta("lastMessageTime") > 5000))
		{
			this.setMeta("lastMessage", message);
			this.setMeta("lastMessageTime", System.currentTimeMillis());
			this.player.sendMessage(message);
		}
	}

	@Override
	public void teleport(Location location)
	{
		if (Math.abs(location.getX()) >= 30000000 || Math.abs(location.getZ()) >= 30000000)
		{
			return;
		}
		this.player.teleport(
				new org.bukkit.Location(BukkitUtil.getWorld(location.getWorld()), location.getX() + 0.5, location.getY(), location.getZ() + 0.5,
										location.getYaw(), location.getPitch()), PlayerTeleportEvent.TeleportCause.COMMAND);
	}

	@Override
	public String getName()
	{
		if (this.name == null)
		{
			this.name = this.player.getName();
		}
		return this.name;
	}

	@Override
	public boolean isOnline()
	{
		return !this.offline && this.player.isOnline();
	}

	@Override
	public void setCompassTarget(Location location)
	{
		this.player.setCompassTarget(
				new org.bukkit.Location(BukkitUtil.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ()));
	}

	@Override
	public Location getLocationFull()
	{
		return BukkitUtil.getLocationFull(this.player);
	}

	@Override
	public void setWeather(PlotWeather weather)
	{
		switch (weather)
		{
			case CLEAR:
				this.player.setPlayerWeather(WeatherType.CLEAR);
				break;
			case RAIN:
				this.player.setPlayerWeather(WeatherType.DOWNFALL);
				break;
			case RESET:
				this.player.resetPlayerWeather();
				break;
			default:
				this.player.resetPlayerWeather();
				break;
		}
	}

	@Override
	public PlotGameMode getGameMode()
	{
		switch (this.player.getGameMode())
		{
			case ADVENTURE:
				return PlotGameMode.ADVENTURE;
			case CREATIVE:
				return PlotGameMode.CREATIVE;
			case SPECTATOR:
				return PlotGameMode.SPECTATOR;
			case SURVIVAL:
				return PlotGameMode.SURVIVAL;
			default:
				return PlotGameMode.NOT_SET;
		}
	}

	@Override
	public void setGameMode(PlotGameMode gameMode)
	{
		switch (gameMode)
		{
			case ADVENTURE:
				this.player.setGameMode(GameMode.ADVENTURE);
				break;
			case CREATIVE:
				this.player.setGameMode(GameMode.CREATIVE);
				break;
			case SPECTATOR:
				this.player.setGameMode(GameMode.SPECTATOR);
				break;
			case SURVIVAL:
				this.player.setGameMode(GameMode.SURVIVAL);
				break;
			default:
				this.player.setGameMode(GameMode.SURVIVAL);
				break;
		}
	}

	@Override
	public void setTime(long time)
	{
		if (time != Long.MAX_VALUE)
		{
			this.player.setPlayerTime(time, false);
		}
		else
		{
			this.player.resetPlayerTime();
		}
	}

	@Override
	public void setFlight(boolean fly)
	{
		this.player.setAllowFlight(fly);
	}

	@Override
	public boolean getFlight()
	{
		return this.player.getAllowFlight();
	}

	@Override
	public void playMusic(Location location, Object id)
	{
		this.player.playEffect(BukkitUtil.getLocation(location), Effect.RECORD_PLAY, id);
	}

	@Override
	public void kick(String message)
	{
		this.player.kickPlayer(message);
	}

	@Override public void stopSpectating()
	{
		if (this.getGameMode() == PlotGameMode.SPECTATOR)
		{
			this.player.setSpectatorTarget(null);
		}
	}

	@Override
	public boolean isBanned()
	{
		return this.player.isBanned();
	}
}
