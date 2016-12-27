package com.ulfric.control.commands;

import org.bukkit.OfflinePlayer;

import com.ulfric.control.coll.Exemptions;
import com.ulfric.control.entity.PunishmentSender;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.locale.Locale;

abstract class PunishmentCommand extends SimpleCommand {

	protected PunishmentCommand(Argument... args)
	{
		for (Argument argument : args)
		{
			this.withArgument(argument);
		}
	}

	private PunishmentSender punishmentSender;
	public PunishmentSender getPunishmentSender() { return this.punishmentSender; }

	@Override
	public void run()
	{
		OfflinePlayer target = (OfflinePlayer) this.getObject("player");

		if (this.isPlayer() && target != null && (Exemptions.isAdam(target.getUniqueId()) && !Exemptions.isAdam(this.getUniqueId())))
		{
			Locale.sendError(this.getPlayer(), "control.exempt", target.getName());

			return;
		}

		if (this.isPlayer())
		{
			this.punishmentSender = PunishmentSender.fromPlayer(this.getPlayer());
		}
		else
		{
			this.punishmentSender = PunishmentSender.CONSOLE;
		}

		this.execute();
	}

	public abstract void execute();

}