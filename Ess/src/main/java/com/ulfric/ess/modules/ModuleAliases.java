package com.ulfric.ess.modules;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.server.Commands;
import com.ulfric.uspigot.event.server.CommandPostprocessEvent;

public class ModuleAliases extends SimpleModule {

	public ModuleAliases()
	{
		super("aliases", "Command aliases module", "Packet", "1.0.0-SNAPSHOT");

		this.withConf();
	}

	private Map<String, String> commands = Maps.newHashMap();

	@Override
	public void postEnable()
	{
		FileConfiguration conf = this.getConf().getConf();

		for (String key : conf.getKeys(false))
		{
			String command = conf.getString(key);

			if (StringUtils.isBlank(command)) continue;

			this.commands.put(key.toLowerCase(), command);
		}
	}

	@Override
	public void postDisable()
	{
		this.commands.clear();
	}

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener()
		{
			@EventHandler
			public void onCommand(CommandPostprocessEvent event)
			{
				if (event.isValid()) return;

				String original = event.getMessage();

				String[] split = original.split("\\s+");

				String command = (split.length == 0 ? original : split[0]);

				if (command.length() <= 1) return;

				command = command.toLowerCase();

				String value = ModuleAliases.this.commands.get(command);

				if (value == null) return;

				event.setValid(true);

				Commands.dispatch(event.getSender(), value.replace("$args", original.substring(command.length()).trim()));
			}
		});
	}

}