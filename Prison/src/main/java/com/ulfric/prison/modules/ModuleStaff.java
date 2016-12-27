package com.ulfric.prison.modules;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.PermissionsExHook;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.persist.ConfigFile;
import com.ulfric.prison.Prison;
import com.ulfric.prison.commands.CommandStaff;

public class ModuleStaff extends SimpleModule {
	private static final ModuleStaff INSTANCE = new ModuleStaff();

	public static ModuleStaff get()
	{
		return ModuleStaff.INSTANCE;
	}

	public ModuleStaff()
	{
		super("stafflist", "List online staff with a command", "StaticShadow", "1.0.0-REL");
		this.withConf();
		this.addCommand("staff", new CommandStaff());
	}

	public List<PermissionsExHook.Group> getGroups()
	{
		ConfigFile configFile = this.getConf();
		FileConfiguration config = configFile.getConf();
		List<String> groups = config.getStringList("groups");
		List<PermissionsExHook.Group> groupList = new ArrayList<>();
		for (String group : groups)
		{
			PermissionsExHook.Group pexGroup = Hooks.PERMISSIONS.getGroupEngine().getGroup(group);
			if (pexGroup != null)
				groupList.add(pexGroup);
			else
				Locale.sendError(Prison.get().getServer().getConsoleSender(),"prison.group_not_recognized",group);
		}
		return groupList;
	}

}
