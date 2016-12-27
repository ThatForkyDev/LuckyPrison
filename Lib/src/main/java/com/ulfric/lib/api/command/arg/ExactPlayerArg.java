package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.player.PlayerUtils;
import org.bukkit.entity.Player;

final class ExactPlayerArg extends OfflinePlayerArg {

	@Override
	public Player match(String string)
	{
		return PlayerUtils.getOnlineExact(string);
	}

}
