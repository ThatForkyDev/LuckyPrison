package com.ulfric.control.coll;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.control.Control;
import com.ulfric.control.entity.Punishment;
import com.ulfric.control.entity.PunishmentHolder;
import com.ulfric.control.entity.PunishmentSender;
import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Annihilate;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.persist.Persist;
import com.ulfric.lib.api.time.Timestamp;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public class PunishmentCache implements Annihilate {

	/**
	 * To 'poke' the singleton
	 */
	public static void poke() { }
	public static final Map<PunishmentType, PunishmentCache> CACHES = Maps.newEnumMap(PunishmentType.class);
	public static PunishmentCache getPunishments(PunishmentType type) { return PunishmentCache.CACHES.get(type); }
	public static final Map<PunishmentHolder, Map<PunishmentType, Map<Integer, Punishment>>> PUNISHMENTS = Maps.newHashMap();

	private static int total;
	public static int getPunishmentTotal() { return PunishmentCache.total; }
	public static int getIncrementedPunishmentTotal() { return PunishmentCache.total++; }
	public static void incrementPunishmentTotal() { PunishmentCache.total++; }

	static
	{
		PunishmentCache.CACHES.put(PunishmentType.BAN, new PunishmentCache(PunishmentType.BAN));
		PunishmentCache.CACHES.put(PunishmentType.MUTE, new PunishmentCache(PunishmentType.MUTE));
		PunishmentCache.CACHES.put(PunishmentType.CMUTE, new PunishmentCache(PunishmentType.CMUTE));
	}

	public static boolean isPunished(PunishmentHolder holder) { return PunishmentCache.PUNISHMENTS.containsKey(holder); }
	public static Map<PunishmentType, Map<Integer, Punishment>> getPunishments(PunishmentHolder holder) { return PunishmentCache.PUNISHMENTS.get(holder); }
	public static Collection<Punishment> getPunishments(PunishmentHolder holder, PunishmentType type)
	{
		Map<PunishmentType, Map<Integer, Punishment>> map = PunishmentCache.getPunishments(holder);

		Map<Integer, Punishment> returnValue;

		if (CollectUtils.isEmpty(map) || CollectUtils.isEmpty(returnValue = map.get(type))) return null;

		return returnValue.values();
	}
	public static Set<Punishment> getValidPunishments(OfflinePlayer holder, PunishmentType type)
	{
		return PunishmentCache.weedExpiredOut(PunishmentCache.getPunishments(holder, type));
	}
	public static Set<Punishment> getValidPunishments(PunishmentHolder holder, PunishmentType type)
	{
		return PunishmentCache.weedExpiredOut(PunishmentCache.getPunishments(holder, type));
	}
	private static Set<Punishment> weedExpiredOut(Collection<Punishment> punishments)
	{
		if (CollectUtils.isEmpty(punishments)) return null;

		Set<Punishment> valid = Sets.newHashSet();

		for (Punishment punishment : punishments)
		{
			if (punishment.isExpired()) continue;

			valid.add(punishment);
		}

		return valid;
	}

	public static Punishment getPunishmentById(int id)
	{
		if (id < 0 || id > PunishmentCache.getPunishmentTotal()) return null;

		Integer integerId = id;

		for (Map<PunishmentType, Map<Integer, Punishment>> value : PunishmentCache.PUNISHMENTS.values())
		{
			if (CollectUtils.isEmpty(value)) continue;

			for (Map<Integer, Punishment> entry : value.values())
			{
				if (CollectUtils.isEmpty(entry)) continue;

				Punishment punishment = entry.get(integerId);

				if (punishment == null) continue;

				return punishment;
			}
		}

		return null;
	}
	public static Collection<Punishment> getPunishments(OfflinePlayer holder, PunishmentType type)
	{
		Collection<Punishment> punishments = Sets.newHashSet();

		UUID holderUniqueId = holder.getUniqueId();
		for (PunishmentHolder lholder : PunishmentCache.PUNISHMENTS.keySet())
		{
			UUID uuid = lholder.getUniqueId();

			if (uuid != null) uuid:
			{
				if (!uuid.equals(holderUniqueId)) continue;

				Collection<Punishment> add = PunishmentCache.getPunishments(lholder, type);

				if (CollectUtils.isEmpty(add)) break uuid;

				punishments.addAll(PunishmentCache.getPunishments(lholder, type));
			}

			String ip = lholder.getIp();
			if (ip == null) continue;

			List<String> ips = Hooks.DATA.getPlayerDataAsStringList(holderUniqueId, "control.ips");

			if (CollectUtils.isEmpty(ips)) continue;

			for (String lip : ips)
			{
				if (!lip.equals(ip)) continue;

				Collection<Punishment> add = PunishmentCache.getPunishments(lholder, type);

				if (CollectUtils.isEmpty(add)) continue;

				punishments.addAll(add);
			}
		}

		return punishments.isEmpty() ? null : punishments;
	}

	public static void addPunishment(PunishmentHolder holder, Punishment punishment)
	{
		PunishmentType type = punishment.getType();

		Map<PunishmentType, Map<Integer, Punishment>> map = PunishmentCache.getPunishments(holder);

		if (map == null)
		{
			map = Maps.newEnumMap(PunishmentType.class);

			Map<Integer, Punishment> pMap = Maps.newHashMap();
			pMap.put(punishment.getId(), punishment);
			map.put(type,pMap);

			PunishmentCache.PUNISHMENTS.put(holder, map);

			return;
		}

		Map<Integer, Punishment> punishments = map.get(type);

		if (punishments == null)
		{
			Map<Integer, Punishment> pMap = Maps.newHashMap();
			pMap.put(punishment.getId(), punishment);
			map.put(type, pMap);

			return;
		}

		punishments.put(punishment.getId(), punishment);
	}

	public static Set<Punishment> clearPunishments(PunishmentHolder holder, PunishmentType type)
	{
		Assert.notNull(holder);
		Assert.notNull(type);

		Map<PunishmentType, Map<Integer, Punishment>> map = PunishmentCache.PUNISHMENTS.get(holder);

		Map<Integer, Punishment> values;
		if (CollectUtils.isEmpty(map) || CollectUtils.isEmpty(values = map.get(type))) return null;

		Set<Punishment> punishments = null;

		for (Punishment punishment : values.values())
		{
			if (punishment.isExpired()) continue;

			punishment.expire();

			if (punishments == null)
			{
				punishments = Sets.newHashSet();
			}

			punishments.add(punishment);
		}

		return punishments;
	}

	private PunishmentCache(PunishmentType type)
	{
		this.type = type;
		this.file = new File(new File(Control.get().getDataFolder(), "store"), type.name().toLowerCase() + "s.yml");
		this.config = YamlConfiguration.loadConfiguration(this.file);

		if (!this.file.exists()) return;

		//Control.get().log("Loading punishments: " + type.name().toLowerCase()); // TODO use debug messaging when that's in

		for (String key : this.config.getKeys(false))
		{
			PunishmentHolder holder;
			try
			{
				holder = PunishmentHolder.fromUUID(UUID.fromString(this.config.getString(key + ".holder")));
			}
			catch (IllegalArgumentException exception)
			{
				holder = PunishmentHolder.fromIP(this.config.getString(key + ".holder"));
			}

			if (holder == null) {
				Control.get().log("Failed to parse holder {0} for {1} #{2}", this.config.getString(key + ".holder"),
								  type.name(), key);
				continue;
			}

			PunishmentSender sender = PunishmentSender.fromString(this.config.getString(key + ".sender"));

			String reason = this.config.getString(key + ".reason");

			long expiry = this.config.getLong(key + ".expiry");

			Timestamp time = expiry == -1 ? Timestamp.infinite() : Timestamp.of(expiry);

			long duration = this.config.getLong(key + ".duration");
			duration = duration < 0 ? -1 : duration;

			PunishmentCache.incrementPunishmentTotal();
			PunishmentCache.addPunishment(holder, new Punishment(Integer.parseInt(key), holder, sender, type, reason, this.config.getString(key + ".date"), time, duration));
		}
	}

	private final PunishmentType type;
	private final File file;
	private final FileConfiguration config;

	@Override
	public void annihilate()
	{
		for (Entry<PunishmentHolder, Map<PunishmentType, Map<Integer, Punishment>>> entry : PunishmentCache.PUNISHMENTS.entrySet())
		{
			Map<PunishmentType, Map<Integer, Punishment>> map = entry.getValue();

			if (map == null) continue;

			Map<Integer, Punishment> values = map.get(this.type);

			if (CollectUtils.isEmpty(values)) continue;

			for (Punishment punishment : values.values())
			{
				int id = punishment.getId();
				this.config.set(id + ".holder", punishment.getHolder().toString());
				this.config.set(id + ".sender", punishment.getSender().toString());
				this.config.set(id + ".reason", punishment.getReason());
				this.config.set(id + ".expiry", punishment.longTimestamp());
				this.config.set(id + ".date", punishment.getDate());
				this.config.set(id + ".duration", punishment.getDuration());
			}
		}

		Persist.save(this.config, this.file);
	}

}
