
package com.ulfric.control.commands;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.google.common.collect.Maps;
import com.ulfric.control.coll.PunishmentCache;
import com.ulfric.control.entity.Punishment;
import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.math.PositiveInteger;
import com.ulfric.lib.api.task.Tasks;

public class CommandAudit extends SimpleCommand {

	public CommandAudit()
	{
		this.withArgument(Argument.REQUIRED_OFFLINE_PLAYER);
	}

	@Override
	public void run()
	{
		Locale.send(this.getSender(), "control.punishments_scan", PunishmentCache.getPunishmentTotal());

		OfflinePlayer player = (OfflinePlayer) this.getObject("player");
		String name = player.getName();
		UUID uuid = player.getUniqueId();
		CommandSender sender = this.getSender();

		Tasks.runAsync(() ->
		{
			Map<PunishmentType, Map<Boolean, PositiveInteger>> map = Maps.newEnumMap(PunishmentType.class);

			for (Map<PunishmentType, Map<Integer, Punishment>> entry : PunishmentCache.PUNISHMENTS.values())
			for (Map<Integer, Punishment> punishments : entry.values())
			for (Punishment punishment : punishments.values())
			{
				UUID punishmentUuid = punishment.getSender().getUniqueId();

				if (punishmentUuid == null) continue;

				if (!uuid.equals(punishmentUuid)) continue;

				PunishmentType type = punishment.getType();

				boolean valid = !punishment.isExpired();

				Map<Boolean, PositiveInteger> found = map.get(type);

				if (found == null)
				{
					found = Maps.newHashMap();

					map.put(type, found);
				}

				PositiveInteger number = found.get(valid);

				if (number == null)
				{
					number = new PositiveInteger(0);

					found.put(valid, number);
				}

				number.increment();
			}

			StringBuilder builder = new StringBuilder();
			builder.append(Strings.format(Locale.getMessage(this.getSender(), "control.punishments_probe"), name));

			int total = 0;

			String probeData = Locale.getMessage(this.getSender(), "control.punishments_probe_data");
			for (Entry<PunishmentType, Map<Boolean, PositiveInteger>> entry : map.entrySet())
			{
				PositiveInteger posint = entry.getValue().get(true);
				int valid = posint == null ? 0 : posint.intValue();

				posint = entry.getValue().get(false);
				int invalid = posint == null ? 0 : posint.intValue();

				total += valid;
				total += invalid;

				builder.append(Strings.format(probeData, org.apache.commons.lang3.StringUtils.capitalize(entry.getKey().name().toLowerCase()), valid, invalid));
			}

			builder.append(Strings.format(Locale.getMessage(this.getSender(), "control.punishments_probe_total"), total));

			sender.sendMessage(builder.toString());
		});
	}

}