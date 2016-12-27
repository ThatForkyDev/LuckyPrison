package com.ulfric.control.entity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ulfric.control.Control;
import org.apache.commons.collections.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.java.Unique;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.time.Milliseconds;
import com.ulfric.lib.api.time.Timestamp;

public class PunishmentHolder implements Named, Unique {

	private static final Map<Object, PunishmentHolder> CACHE = Maps.newHashMap();

	public static PunishmentHolder fromIP(Player player) { return PunishmentHolder.fromIP(PlayerUtils.getIP(player)); }
	public static PunishmentHolder fromIP(String ip)
	{
		PunishmentHolder holder = PunishmentHolder.CACHE.get(ip);

		if (holder != null) return holder;

		holder = new PunishmentHolder(ip);

		PunishmentHolder.CACHE.put(ip, holder);

		return holder;
	}
	public static PunishmentHolder fromUUID(UUID uuid)
	{
		PunishmentHolder holder = PunishmentHolder.CACHE.get(uuid);

		if (holder != null) return holder;

		holder = new PunishmentHolder(uuid);

		PunishmentHolder.CACHE.put(uuid, holder);

		return new PunishmentHolder(uuid);
	}

	private PunishmentHolder(String ip)
	{
		Assert.notNull(ip);

		this.ip = ip;
	}
	private PunishmentHolder(UUID uuid)
	{
		Assert.notNull(uuid);

		this.uuid = uuid;
	}

	private Timestamp lastWarning;

	public boolean isWarned()
	{
		return !CollectionUtils.isEmpty(this.warnings);
	}

	public boolean canWarn()
	{
		return this.lastWarning == null || this.lastWarning.timeSince() > Milliseconds.fromSeconds(15);
	}

	private List<Warning> warnings;

	public void warn(Warning warning)
	{
		String sender = warning.getSender().getName();
		String reason = warning.getReason();
		String name = this.getName();

		if (this.isIp())
		{
			Locale.sendMassPerm("control.seeip", false, "control.warncast", "<IP HIDDEN>", sender, reason);
			Locale.sendMassPerm("control.seeip", true, "control.warncast", name, sender, reason);
		}
		else
		{
			Locale.sendMass("control.warncast", name, sender, reason);
		}

		if (this.warnings == null) this.warnings = Lists.newArrayList();

		this.warnings.add(warning);

		if (this.lastWarning == null)
		{
			this.lastWarning = Timestamp.now();

			return;
		}

		this.lastWarning.setTimeNow();
	}

	public List<Warning> getWarnings()
	{
		return this.warnings;
	}

	public void clearWarnings()
	{
		if (this.lastWarning == null) return;

		this.lastWarning = null;

		this.warnings.clear();
	}

	private String ip;
	public String getIp() { return this.ip; }
	public boolean isIp() { return this.ip != null; }

	private UUID uuid;
	@Override
	public UUID getUniqueId() { return this.uuid; }

	@Override
	public String getName()
	{
		if (this.uuid == null) return this.ip;

		return Bukkit.getOfflinePlayer(this.uuid).getName();
	}

	public boolean kick(String reason, Object... objects)
	{
		if (this.getUniqueId() != null) uuid:
		{
			Player player = Bukkit.getPlayer(this.getUniqueId());

			if (player == null) break uuid;

			Control.get().getServer().getScheduler().runTask(Control.get(), () ->
					player.kickPlayer(Strings.format(Locale.getMessage(player, reason), objects).replace(Strings.PLAYER, player.getName())));

			return true;
		}

		String ip = this.getIp();
		if (ip == null) return false;

		boolean flag = false;
		for (Player player : Bukkit.getOnlinePlayers())
		{
			String lip = PlayerUtils.getIP(player);

			if (!lip.equals(ip)) continue;
			Control.get().getServer().getScheduler().runTask(Control.get(), () ->
					player.kickPlayer(Strings.format(Locale.getMessage(player, reason), objects).replace(Strings.PLAYER, player.getName())));

			flag = true;
		}

		return flag;
	}

	@Override
	public String toString()
	{
		if (this.uuid == null)
		{
			if (this.ip == null)
			{
				return super.toString();
			}

			return this.ip;
		}

		return this.uuid.toString();
	}

	@Override
	public int hashCode()
	{
		if (this.uuid == null)
		{
			if (this.ip == null)
			{
				return super.hashCode();
			}

			return this.ip.hashCode();
		}

		return this.uuid.hashCode();
	}

	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof PunishmentHolder)) return false;

		PunishmentHolder other = (PunishmentHolder) object;

		if (this.uuid != null && other.uuid != null)
		{
			return this.uuid.equals(other.uuid);
		}

		if (this.ip != null && other.ip != null)
		{
			return this.ip.equals(other.ip);
		}

		return false;
	}

}