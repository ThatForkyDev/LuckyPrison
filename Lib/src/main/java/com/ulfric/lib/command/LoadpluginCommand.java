package com.ulfric.lib.command;

import com.ulfric.lib.Lib;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.persist.JarFileFilter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.util.Arrays;

public final class LoadpluginCommand extends SimpleCommand {

	public LoadpluginCommand()
	{
		this.withIndexUnusedArgs();
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();

		String unused = this.getUnusedArgs();

		if (Strings.isNullOrBlank(unused))
		{
			Locale.sendError(sender, "lib.loadplugin_specify");
			return;
		}

		PluginManager manager = Bukkit.getPluginManager();

		if (Arrays.stream(manager.getPlugins()).anyMatch(p -> p.getName().equalsIgnoreCase(unused)))
		{
			Locale.sendError(sender, "lib.loadplugin_loaded");
			return;
		}

		File folder = Lib.get().getDataFolder().getParentFile();
		File[] pluginFiles = folder.listFiles(new JarFileFilter(unused));

		if (pluginFiles == null || pluginFiles.length == 0)
		{
			Locale.sendError(sender, "lib.loadplugin_no_jars");
			return;
		}

		for (File pluginFile : pluginFiles)
		{
			try
			{
				Plugin plugin = manager.loadPlugin(pluginFile);
				manager.enablePlugin(plugin);
				Locale.sendSuccess(sender, "lib.loadplugin_success", plugin.getName());
			}
			catch (Throwable t)
			{
				Locale.sendError(sender, "lib.loadplugin_err", t.toString());
			}
		}
	}
}
