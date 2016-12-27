package com.ulfric.chat.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.ulfric.chat.lang.Meta;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;

public class CommandMessage extends SimpleCommand {


	public CommandMessage()
	{
		this.withIndexUnusedArgs();

		this.withArgument(Argument.builder().withPath("player").withStrategy(ArgStrategy.PLAYER).withUsage("system.specify_player").withCap(1));

		this.withArgument(Argument.builder().withPath("start").withStrategy(ArgStrategy.STRING).withUsage("chat.message_empty").withRemovalExclusion());
	}


	@Override
	public void run()
	{
		Player player = this.getPlayer();
		Player target = (Player) this.getObject("player");

		UUID tUuid = target.getUniqueId();

		if (this.isPlayer() && target.equals(player))
		{
			Locale.sendError(player, "chat.message_self");

			return;
		}

		String pm = this.getUnusedArgs();
		String name = this.getName();

		if (this.isPlayer())
		{
			if (!this.hasPermission("chat.immutable")) ignore:
			{
				if (!Hooks.DATA.getPlayerDataAsStringList(tUuid, Meta.IGNORE).contains(player.getUniqueId().toString())) break ignore;

				Locale.sendError(player, "chat.message_ignore", target.getName());

				return;
			}

			Metadata.apply(player, Meta.LAST_PM, target.getName());
			Locale.send(target, "chat.message_incoming", player.hasPermission("chat.message.staff") ? Locale.getMessage(target, "prison.sb_staff") : Strings.BLANK, Hooks.DATA.getPlayerDataAsBoolean(tUuid, Meta.SHOW_NICKNAMES) ? name : player.getDisplayName(), pm);
			Locale.send(player, "chat.message_outgoing", Hooks.DATA.getPlayerDataAsBoolean(player.getUniqueId(), Meta.SHOW_NICKNAMES) ? target.getName() : target.getDisplayName(), pm);

			this.sendSpy(player, target, pm);
		}
		else
		{
			Locale.send(target, "chat.message_incoming", name, pm);
			Locale.send(this.getSender(), "chat.message_incoming", target.getName(), pm);
		}

		Metadata.apply(target, Meta.LAST_PM, name);

		if (!Hooks.DATA.getPlayerDataAsBoolean(tUuid, Meta.NOTIFICATIONS)) return;

		target.playSound(target.getEyeLocation(), Sound.NOTE_PLING, 5F, 5.5F);
	}

	protected void sendSpy(Player sender, Player target, String message)
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (player == sender || player == target || !player.hasMetadata(Meta.SCHUTZSTAFFEL)) continue;

			Locale.send(player, "chat.social_spy", sender.getName(), target.getName(), message);
		}
	}

}