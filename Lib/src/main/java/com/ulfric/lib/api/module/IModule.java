package com.ulfric.lib.api.module;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.persist.ConfigFile;
import com.ulfric.lib.api.server.Commands;
import com.ulfric.lib.api.server.Events;
import com.ulfric.lib.api.server.Listeners;
import org.apache.commons.collections4.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface IModule extends Named {

	String getModuleDescription();

	String getModuleAuthors();

	String getModuleVersion();

	Plugin getOwningPlugin();

	default void test(Object data)
	{
		this.log("Testing {0}", this.getName());

		this.runTest(data);
	}

	default void runTest(Object data)
	{
	}

	default void log(String message, Object... replace)
	{
		Bukkit.getLogger().info(Strings.format("[Module] {0}", Strings.format(message, replace)));
	}

	default boolean getModuleDefault()
	{
		return true;
	}

	boolean isModuleEnabled();

	void setModuleEnabled(boolean enable);

	default IModule getParentModule()
	{
		return null;
	}

	default void setParentModule(IModule module)
	{
		throw new UnsupportedOperationException();
	}

	default boolean hasSubModules()
	{
		return !CollectionUtils.isEmpty(this.getSubModules());
	}

	default List<IModule> getTree()
	{
		IModule parent = this.getParentModule();

		if (parent == null) return null;

		List<IModule> tree = Lists.newArrayList();

		while (parent != null)
		{
			tree.add(parent);

			parent = parent.getParentModule();
		}

		return tree;
	}

	List<IModule> getSubModules();

	default List<IModule> getChildren()
	{
		if (!this.hasSubModules()) return ImmutableList.of();

		List<IModule> modules = Lists.newArrayList();

		for (IModule module : this.getSubModules())
		{
			modules.add(module);

			modules.addAll(module.getChildren());
		}

		return modules;
	}

	default int getTotalSubModules()
	{
		return this.getChildren().size();
	}

	default void enableSubModules()
	{
		if (!this.hasSubModules()) return;

		for (IModule module : this.getSubModules())
		{
			if (module.isModuleEnabled()) continue;

			module.tryEnable();
		}
	}

	default void tryEnable()
	{
		try
		{
			this.enable();
		}
		catch (Throwable t)
		{
			this.log("An error occured while enabling module '{0}'!", Optional.ofNullable(this.getParentModule()).orElse(this).getName());

			t.printStackTrace();
		}
	}

	void withSubModule(IModule module);

	void withConf();

	boolean hasConf();

	ConfigFile getConf();

	void loadConf();

	void saveConf();

	default Object getConfObject(String path)
	{
		return this.getConf().getValue(path);
	}

	default Object getConfObject(String path, Object defaultValue)
	{
		return this.getConf().getValue(path, defaultValue);
	}

	default void setConfObject(String path, Object object)
	{
		this.getConf().setValue(path, object);
	}

	default void enable()
	{
		Assert.isFalse(this.isModuleEnabled(), "This module is already enabled!");

		if (!Modules.canEnable(this)) return;

		this.preEnable();

		if (this.hasConf())
		{
			this.loadConf();
		}

		this.setModuleEnabled(true);

		this.onEnable();

		Events.call(new ModuleEnableEvent(this));

		if (!this.isModuleEnabled()) return;

		if (this.enabledCount() == 0)
		{
			this.onFirstEnable();
		}

		this.incrementEnabledCount();

		this.registerCommands();

		this.registerListeners();

		this.startAutomaticTasks();

		this.enableSubModules();
	}

	int enabledCount();

	void incrementEnabledCount();

	default void onFirstEnable()
	{
	}

	default void preEnable()
	{
	}

	void onEnable();

	default void disable()
	{
		Assert.isTrue(this.isModuleEnabled(), "This module is not enabled!");

		this.preDisable();

		this.setModuleEnabled(false);

		this.onDisable();

		Events.call(new ModuleEnableEvent(this));

		if (this.isModuleEnabled()) return;

		this.unregisterListeners();

		this.unregisterCommands();

		this.cancelTasks();

		/*if (this.hasConf())
		{
			this.saveConf();
		}*/

		if (!this.hasSubModules()) return;

		for (int x = this.getSubModules().size() - 1; x >= 0; x--)
		{
			IModule module = this.getSubModules().get(x);

			if (!module.isModuleEnabled()) continue;

			module.disable();
		}
	}

	default void preDisable()
	{
	}

	void onDisable();

	Map<ListenerReg, Set<Listener>> getListeners();

	void addListener(Listener listener);

	default void registerListeners()
	{
		if (CollectUtils.isEmpty(this.getListeners())) return;

		Set<Listener> listeners = this.getListeners().get(ListenerReg.PENDING);

		if (CollectUtils.isEmpty(listeners)) return;

		for (Listener listener : listeners)
		{
			Listeners.register(this, listener);
		}
	}

	default void unregisterListeners()
	{
		if (CollectUtils.isEmpty(this.getListeners())) return;

		for (Set<Listener> listeners : this.getListeners().values())
		{
			for (Listener listener : listeners)
			{
				HandlerList.unregisterAll(listener);
			}
		}
	}

	Map<ModuleTask.StartType, Set<ModuleTask>> getTasks();

	void addTask(ModuleTask task);

	default void startAutomaticTasks()
	{
		Map<ModuleTask.StartType, Set<ModuleTask>> map = this.getTasks();

		if (CollectUtils.isEmpty(map)) return;

		Set<ModuleTask> tasks = map.get(ModuleTask.StartType.AUTOMATIC);

		if (CollectUtils.isEmpty(tasks)) return;

		tasks.forEach(ModuleTask::start);
	}

	default void cancelTasks()
	{
		if (CollectUtils.isEmpty(this.getTasks())) return;

		this.getTasks().values().forEach(tasks -> tasks.forEach(ModuleTask::annihilate));
	}

	Map<String, CommandExecutor> getCommands();

	void addCommand(String command, CommandExecutor executor);

	default void registerCommands()
	{
		if (CollectUtils.isEmpty(this.getCommands())) return;

		for (Map.Entry<String, CommandExecutor> entry : this.getCommands().entrySet())
		{
			Commands.register(this, entry.getKey(), entry.getValue());
		}
	}

	default void unregisterCommands()
	{
		if (CollectUtils.isEmpty(this.getCommands())) return;

		Plugin plugin = this.getOwningPlugin();

		for (String string : this.getCommands().keySet())
		{
			PluginCommand command = plugin.getCommand(string);

			if (command == null) continue;

			command.setExecutor(new DisabledCommand());
		}
	}

}
