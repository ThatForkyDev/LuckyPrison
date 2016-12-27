package com.ulfric.control.commands;

import org.bukkit.entity.Player;

import com.ulfric.control.coll.PunishmentExecutor;
import com.ulfric.control.entity.Punishment;
import com.ulfric.control.entity.PunishmentHolder;
import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.time.Timestamp;

public class CommandKick extends PunishmentCommand {

	public CommandKick()
	{
		super(Argument.REQUIRED_PLAYER);

		this.withIndexUnusedArgs();
	}

	@Override
	public void execute()
	{
		String reason = this.getUnusedArgs();

		Player player = (Player) this.getObject("player");

		if (reason == null || (reason = (reason.trim())).isEmpty()) reason = Locale.getMessage(player, PunishmentType.KICK.getDefaultReason());

		PunishmentExecutor.execute(new Punishment(-1, PunishmentHolder.fromUUID(player.getUniqueId()), this.getPunishmentSender(), PunishmentType.KICK, reason, null, Timestamp.of(0), 0));
	}

}