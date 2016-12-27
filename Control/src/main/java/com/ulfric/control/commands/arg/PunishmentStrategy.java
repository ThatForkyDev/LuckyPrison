package com.ulfric.control.commands.arg;

import com.ulfric.control.coll.PunishmentCache;
import com.ulfric.control.entity.Punishment;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.math.Numbers;

public enum PunishmentStrategy implements ArgStrategy<Punishment> {

	INSTANCE;

	@Override
	public Punishment match(String string)
	{
		Integer id = Numbers.parseInteger(string);

		if (id == null) return null;

		return PunishmentCache.getPunishmentById(id);
	}

}