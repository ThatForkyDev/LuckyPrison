package com.ulfric.ess.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ulfric.ess.lang.Permissions;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.player.PlayerUtils;

public class CommandShock extends SimpleCommand {


	public CommandShock()
	{
		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, Permissions.SHOCK_OTHERS);
	}

	@Override
	public void run()
	{
		Player target = (Player) this.getObject("player");

		Location location;

		if (this.isPlayer() && !target.equals(this.getPlayer()))
		{
			location = target.getLocation();
		}
		else
		{
			location = PlayerUtils.getTargetBlock(this.getPlayer(), 30).getLocation();
		}

		Metadata.tieToPlayer(location.getWorld().strikeLightning(location), this.getPlayer());
	}


}