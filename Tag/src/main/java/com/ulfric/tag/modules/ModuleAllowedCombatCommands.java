package com.ulfric.tag.modules;

import com.ulfric.lib.api.collect.Sets;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Set;

public class ModuleAllowedCombatCommands extends SimpleModule {

	private Set<String> allowedCommands;

	public ModuleAllowedCombatCommands()
	{
		super("allowed-combat-commands", "Allowed commands in combat", "Packet", "1.0.0-REL");

		this.withConf();

		this.addListener(new Listener() {
			@EventHandler(ignoreCancelled = true)
			public void onCommand(PlayerCommandPreprocessEvent event)
			{
				if (!event.getPlayer().hasMetadata("_ulf_combattag")) return;

				String original = event.getMessage().substring(1).split("\\s+")[0].toLowerCase();
				PluginCommand command = Bukkit.getPluginCommand(original);
				if (command == null || !command.isRegistered()) return;

				if (!ModuleAllowedCombatCommands.this.allowed(command))
				{
					Locale.sendError(event.getPlayer(), "tag.not_allowed", original);
					event.setCancelled(true);
				}
			}
		});
	}

	@Override
	public void postEnable()
	{
		this.allowedCommands = Sets.newHashSet(this.getConf().getValueAsStringList("allowed"));
	}

	private boolean allowed(Command command)
	{
		return this.allowedCommands.contains(command.getName().toLowerCase()) ||
			   command.getAliases().stream().anyMatch(alias -> this.allowedCommands.contains(alias.toLowerCase()));
	}
}
