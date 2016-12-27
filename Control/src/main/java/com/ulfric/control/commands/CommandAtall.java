package com.ulfric.control.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ulfric.control.coll.Exemptions;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.EnforcedSimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.server.Commands;

public class CommandAtall extends EnforcedSimpleCommand {

	public CommandAtall()
	{
		this.withIndexUnusedArgs();

		this.withArgument(Argument.builder().withPath("start").withStrategy(ArgStrategy.STRING).withUsage("control.atall_empty_msg").withRemovalExclusion());
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();

		String command = this.getUnusedArgs();

		for (Player player : PlayerUtils.getOnlinePlayersExceptFor(this.getPlayer()))
		{
			Commands.dispatch(sender, command.replace(Strings.PLAYER, player.getName()).replace(Strings.PLAYER.replace('<', '{').replace('>', '}'), player.getName()));
		}

		Locale.sendSuccess(sender, "control.atall");
	}

	@Override
	public boolean enforce()
	{
		if (!this.isPlayer() || Exemptions.isAdam(this.getUniqueId())) return true;

		Locale.sendError(this.getPlayer(), "control.major_restrict");

		return false;
	}

}