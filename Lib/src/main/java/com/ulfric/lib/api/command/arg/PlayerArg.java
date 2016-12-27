package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.player.PlayerUtils;
import org.bukkit.entity.Player;

final class PlayerArg extends OfflinePlayerArg {


	@Override
	public Player match(String string)
	{
		return PlayerUtils.getOnline(string);
	}


}
