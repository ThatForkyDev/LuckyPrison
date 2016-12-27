package com.ulfric.control.commands;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Cooldown;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.time.Milliseconds;

public class CommandHelpop extends SimpleCommand {

	public CommandHelpop()
	{
		this.withIndexUnusedArgs();

		this.withCooldown(Cooldown.builder().withName("helpop").withDefaultDelay(Milliseconds.fromSeconds(30)));
	}

	@Override
	public void run()
	{
		String message = this.getUnusedArgs();

		if (StringUtils.isEmpty(message))
		{
			Locale.sendError(this.getSender(), "control.helpop_empty");

			return;
		}

		Locale.sendSuccess(this.getSender(),"control.helpop_success");

		String name = this.getName();

		Locale.send(this.getSender(), "control.helpop", "YOU", message);

		Set<Player> players = PlayerUtils.getOnlinePlayersWithPermission("control.seehelpop", true);

		players.remove(this.getPlayer());

		players.forEach(player -> Locale.send(player, "control.helpop", name, message));
	}

}