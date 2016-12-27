package com.ulfric.control.commands;

import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.ulfric.control.coll.PunishmentExecutor;
import com.ulfric.control.entity.Punishment;
import com.ulfric.control.entity.PunishmentHolder;
import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerUtils;

public class CommandKickip extends PunishmentCommand {

	public CommandKickip()
	{
		super(Argument.builder().withPath("ip").withStrategy(ArgStrategy.INET).withUsage("control.specify_valid_ip").build());

		this.withIndexUnusedArgs();
	}

	@Override
	public void execute()
	{
		Set<Player> players = PlayerUtils.getOnlinePlayersWithIP((String) this.getObject("ip"));

		players = players.stream().filter(player -> !player.hasPermission("control.bypass.groupkick")).collect(Collectors.toSet());

		if (players.isEmpty())
		{
			Locale.sendError(this.getSender(), "control.ip_not_found");

			return;
		}

		String reason = this.getUnusedArgs();

		for (Player player : players)
		{
			PunishmentExecutor.execute(new Punishment(-1, PunishmentHolder.fromUUID(player.getUniqueId()), this.getPunishmentSender(), PunishmentType.KICK, reason == null ? Locale.getMessage(player, PunishmentType.KICK.getDefaultReason()) : reason, null, null, 0));
		}
	}

}