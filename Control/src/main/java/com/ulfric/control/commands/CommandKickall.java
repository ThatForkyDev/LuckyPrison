package com.ulfric.control.commands;

import org.bukkit.entity.Player;

import com.ulfric.control.coll.PunishmentExecutor;
import com.ulfric.control.entity.Punishment;
import com.ulfric.control.entity.PunishmentHolder;
import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerUtils;

public class CommandKickall extends PunishmentCommand {

	public CommandKickall()
	{
		super(Argument.builder().withPath("start").withStrategy(ArgStrategy.STRING).withRemovalExclusion().build());

		this.withIndexUnusedArgs();
	}

	@Override
	public void execute()
	{
		String reason = this.getUnusedArgs();

		boolean flag = reason == null || (reason = reason.trim()).isEmpty();

		for (Player player : PlayerUtils.getOnlinePlayersWithPermission("control.bypass.groupkick", false))
		{
			PunishmentExecutor.execute(new Punishment(PunishmentHolder.fromUUID(player.getUniqueId()), this.getPunishmentSender(), PunishmentType.KICK, flag ? Locale.getMessage(player, PunishmentType.KICK.getDefaultReason()) : reason, null, null, 0));
		}
	}

}