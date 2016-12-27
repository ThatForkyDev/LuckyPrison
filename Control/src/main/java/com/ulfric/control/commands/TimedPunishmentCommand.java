package com.ulfric.control.commands;

import org.bukkit.OfflinePlayer;

import com.ulfric.control.coll.PunishmentExecutor;
import com.ulfric.control.entity.Punishment;
import com.ulfric.control.entity.PunishmentHolder;
import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.command.arg.InetArg;
import com.ulfric.lib.api.time.TimeUtils;
import com.ulfric.lib.api.time.Timestamp;

class TimedPunishmentCommand extends PunishmentCommand {


	protected TimedPunishmentCommand(PunishmentType type, Argument argument)
	{
		super(argument);

		this.withIndexUnusedArgs();

		this.withArgument("time", ArgStrategy.FUTURE, Timestamp.infinite());

		this.base = argument;

		this.type = type;
	}


	private final Argument base;

	private final PunishmentType type;
	public PunishmentType getType() { return this.type; }


	@Override
	public void execute()
	{
		String reason = null;

		Timestamp time = (Timestamp) this.getObject("time");

		reason = this.getUnusedArgs();

		if (reason == null || reason.isEmpty())
		{
			reason = this.getType().getDefaultReason();
		}

		if (this.base.getStrategy() instanceof InetArg)
		{
			PunishmentExecutor.execute(new Punishment(PunishmentHolder.fromIP((String) this.getObject("inet")), this.getPunishmentSender(), this.getType(), reason, TimeUtils.formatCurrentDay(), time, time.timeTill()));

			return;
		}
		PunishmentHolder.fromUUID(((OfflinePlayer) this.getObject("player")).getUniqueId());
		this.getPunishmentSender();
		this.getType();
		TimeUtils.formatCurrentDay();
		time.timeTill();
		System.out.println(reason);
		System.out.println(time);
		PunishmentExecutor.execute(new Punishment(PunishmentHolder.fromUUID(((OfflinePlayer) this.getObject("player")).getUniqueId()), this.getPunishmentSender(), this.getType(), reason, TimeUtils.formatCurrentDay(), time, time.timeTill()));
	}


}