package com.ulfric.control.commands;

import java.util.Set;

import org.bukkit.OfflinePlayer;

import com.ulfric.control.coll.PunishmentCache;
import com.ulfric.control.entity.Punishment;
import com.ulfric.control.entity.PunishmentHolder;
import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;

class UnpunishmentCommand extends SimpleCommand {

	protected UnpunishmentCommand(ArgStrategy<?> strat, String usage, PunishmentType type)
	{
		this.withArgument("obj", strat, usage);

		this.type = type;
	}

	private final PunishmentType type;

	@Override
	public void run()
	{
		Object object = this.getObject("obj");
		PunishmentHolder holder;
		if (object instanceof String)
		{
			holder = PunishmentHolder.fromIP((String) object);
		}
		else
		{
			holder = PunishmentHolder.fromUUID(((OfflinePlayer) object).getUniqueId());
		}

		Set<Punishment> punishments = PunishmentCache.clearPunishments(holder, this.type);

		if (CollectUtils.isEmpty(punishments))
		{
			Locale.sendError(this.getSender(), "control.punishments_none");

			return;
		}

		StringBuilder builder = new StringBuilder();

		for (Punishment punishment : punishments)
		{
			builder.append('#');
			builder.append(punishment.getId());
			builder.append(", ");
		}

		String results = builder.toString();

		results = results.substring(0, results.length()-2);

		int size = punishments.size();

		Locale.sendSuccess(this.getSender(), "control.punishments_expire", size, size == 1 ? Strings.BLANK : 's', results);
	}

}