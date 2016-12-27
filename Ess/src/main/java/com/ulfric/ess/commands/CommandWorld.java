package com.ulfric.ess.commands;

import com.ulfric.ess.modules.ModuleSpawnpoint;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public class CommandWorld extends SimpleCommand {

	public CommandWorld()
	{
		this.withArgument("world", ArgStrategy.WORLD);
	}

	@Override
	public void run()
	{
		World world = (World) this.getObject("world");
		if (world == null || !this.isPlayer())
		{
			ComponentBuilder message = new ComponentBuilder(Locale.getMessage(this.getSender(), "ess.worlds"));

			String click = Locale.getMessage(this.getSender(), "ess.worlds_click");
			List<World> worlds = Bukkit.getWorlds();
			for (int i = 0; i < worlds.size(); i++)
			{
				World lworld = worlds.get(i);
				String name = lworld.getName();

				message.append(name).color(ChatColor.GRAY);

				message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											 TextComponent.fromLegacyText(Strings.format(click, name))));

				message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/world " + name));

				if (i + 1 != worlds.size()) message.append(", ").color(ChatColor.YELLOW);
			}

			this.getSender().sendMessage(message.create());

			return;
		}

		Location location = null;
		Location spawn = ModuleSpawnpoint.get().getSpawn().getLocation();

		if (world.equals(spawn.getWorld()))
		{
			location = spawn;
		}

		if (location == null)
		{
			location = world.getSpawnLocation();
		}

		this.getPlayer().teleport(location);

		Locale.sendSuccess(this.getPlayer(), "ess.worlds_teleport", world.getName());
	}

}
