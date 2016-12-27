package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.player.PlayerUtils;
import org.bukkit.OfflinePlayer;

class OfflinePlayerArg implements ArgStrategy<OfflinePlayer> {

	@Override
	public OfflinePlayer match(String string)
	{
		return PlayerUtils.getOffline(string);
	}

}
