package com.ulfric.control.commands.arg;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.bukkit.OfflinePlayer;

import com.ulfric.control.entity.PunishmentHolder;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.player.PlayerUtils;

public enum PunishmentHolderStrategy implements ArgStrategy<PunishmentHolder> {

	INSTANCE;

	@Override
	public PunishmentHolder match(String string)
	{
		OfflinePlayer player = PlayerUtils.getOffline(string);

		if (player != null) return PunishmentHolder.fromUUID(player.getUniqueId());

		if (!InetAddressValidator.getInstance().isValidInet4Address(string = StringUtils.formatIP(string))) return null;

		return PunishmentHolder.fromIP(string);
	}

}