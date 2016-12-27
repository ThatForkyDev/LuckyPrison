package com.ulfric.prison.commands;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.Command;
import com.ulfric.lib.api.command.SimpleSubCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PermissionUtils;

class SubCommandAliasadd extends SimpleSubCommand {


	public SubCommandAliasadd(Command command)
	{
		super(command, "add", "new", "create");

		this.withEnforcePlayer();

		this.withIndexUnusedArgs();

		this.withArgument("alias", ArgStrategy.STRING, "prison.alias_need_cmd");
		this.withArgument(Argument.builder().withPath("cmd").withStrategy(ArgStrategy.STRING).withUsage("prison.alias_need_cmd_valid").withRemovalExclusion());
	}

	@Override
	public void run()
	{
		String alias = (String) this.getObject("alias");

		Player player = this.getPlayer();

		if (Bukkit.getPluginCommand(alias) != null)
		{
			Locale.sendError(player, "prison.alias_taken");

			return;
		}

		List<String> aliases = Hooks.DATA.getPlayerDataAsStringList(this.getUniqueId(), "prison.aliases");

		if (!aliases.isEmpty())
		{
			Iterator<String> iterator = aliases.iterator();

			while (iterator.hasNext())
			{
				String found = StringUtils.findOption(iterator.next(), "cmd");

				if (found == null) continue;

				if (!found.equals(alias)) continue;

				iterator.remove();

				break;
			}

			if (aliases.size() >= PermissionUtils.getMax(this.getSender(), "prison.alias"))
			{
				Locale.sendError(player, "prison.alias_limit");

				return;
			}
		}

		PluginCommand pcommand = Bukkit.getPluginCommand((String) this.getObject("cmd"));

		if (pcommand == null || !pcommand.isRegistered())
		{
			Locale.send(player, "prison.alias_need_cmd_valid");

			return;
		}

		String command = this.getUnusedArgs();

		aliases.add(Strings.format("cmd.{0} exe.{1}", alias, Strings.fakeSpace(command)));

		Hooks.DATA.setPlayerData(this.getUniqueId(), "prison.aliases", aliases);

		Locale.sendSuccess(player, "prison.alias_made", alias, command);
	}

}
