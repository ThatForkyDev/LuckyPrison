package com.ulfric.prison.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Command;
import com.ulfric.lib.api.command.SimpleSubCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.time.TimeUtils;
import com.ulfric.prison.entity.Minebuddy;
import com.ulfric.prison.lang.Meta;

class SubCommandMbinfo extends SimpleSubCommand {

	public SubCommandMbinfo(Command command)
	{
		super(command, "info", "show", "details");

		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer);
	}

	@Override
	public void run()
	{
		Player target = (Player) this.getObject("player");

		if (target == null) return;

		if (!target.hasMetadata(Meta.MINEBUDDY))
		{
			Locale.send(this.getSender(), "prison.minebuddy_none");

			return;
		}

		Minebuddy buddy = Metadata.getValue(target, Meta.MINEBUDDY);

		this.send("prison.minebuddy_info_header", target.getName());
		this.send("prison.minebuddy_info_buddy", buddy.getPartner(target).getName());
		this.send("prison.minebuddy_info_total_items", StringUtils.formatNumber(buddy.itemTotal()));
		this.send("prison.minebuddy_info_total_cash", StringUtils.formatMoneyFully(buddy.cashTotal()));
		this.send("prison.minebuddy_info_total_token", StringUtils.formatNumber(buddy.tokenTotal()));
		this.send("prison.minebuddy_info_time", TimeUtils.millisecondsToString(buddy.getStarted().timeSince()));
	}

	private void send(String path, Object... objects)
	{
		Locale.sendSpecial(this.getSender(), "prison.minebuddy", path, objects);
	}

}