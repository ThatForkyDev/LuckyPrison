package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.player.PlayerUtils;
import org.bukkit.OfflinePlayer;

final class NeverPlayedPlayerArg extends OfflinePlayerArg {


	@Override
	public OfflinePlayer match(String string)
	{
		return PlayerUtils.getOffline(string, true);
	}


}
