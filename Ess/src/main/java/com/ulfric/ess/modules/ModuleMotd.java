package com.ulfric.ess.modules;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import com.google.common.collect.Lists;
import com.ulfric.ess.Ess;
import com.ulfric.ess.commands.CommandSetmotd;
import com.ulfric.ess.configuration.ConfigurationStore;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.math.RandomUtils;
import com.ulfric.lib.api.module.SimpleModule;

public class ModuleMotd extends SimpleModule implements Listener {

	private static final ModuleMotd INSTANCE = new ModuleMotd();
	public static final ModuleMotd get() { return ModuleMotd.INSTANCE; }

	private ModuleMotd()
	{
		super("motd", "Motd management module", "Packet", "1.0.0-REL");

		this.addListener(this);

		this.addCommand("setmotd", new CommandSetmotd());
	}

	@Override
	public void postEnable()
	{
		this.motds = Lists.newArrayList();

		for (File file : ConfigurationStore.get().getFilesFromFolder("motd"))
		{
			FileConfiguration conf = YamlConfiguration.loadConfiguration(file);

			this.addMotd(conf.getString("top"), conf.getString("bottom"));
		}
	}

	private List<String> motds;

	public static final String FORMAT = "&6&lLucky Prison &f{0} {1}<n>&eluckyprison.com &f{0} {2}";

	public void clear()
	{
		if (!ModuleMotd.get().isModuleEnabled()) return;

		this.motds.clear();
	}

	public void addMotd(String l1, String l2)
	{
		if (!ModuleMotd.get().isModuleEnabled()) return;

		this.motds.add(Strings.formatF(ModuleMotd.FORMAT, Chat.seperator(), l1, l2).replace("<version>", Ess.get().getDescription().getVersion()));
	}

	@EventHandler
	public void onPing(ServerListPingEvent event)
	{
		if (!CollectUtils.isEmpty(this.motds))
		{
			event.setMotd(RandomUtils.randomValueFromList(this.motds));

			return;
		}

		event.setMotd(Strings.formatF(ModuleMotd.FORMAT, Chat.seperator(), Strings.BLANK, Strings.BLANK));
	}

}