package com.ulfric.lib.api.module;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.persist.ConfigFile;
import com.ulfric.lib.api.persist.Persist;
import com.ulfric.lib.api.server.Commands;
import com.ulfric.lib.api.server.Listeners;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Module implements IModule {

	private final String description;
	private final String authors;
	private final String version;
	private final String name;
	private final boolean defaultEnabled;
	private boolean hasConfig;
	private ConfigFile config;
	private Plugin owner;
	private Map<ListenerReg, Set<Listener>> listeners;
	private Map<String, CommandExecutor> commands;
	private Map<ModuleTask.StartType, Set<ModuleTask>> tasks;
	private IModule parent;
	private List<IModule> sub;
	private boolean enabled;
	private int enabledCount;

	protected Module(String name, String description, String authors, String version)
	{
		this(name, description, authors, version, true);
	}

	protected Module(String name, String description, String authors, String version, boolean defaultEnabled)
	{
		this.name = name.toLowerCase();

		this.description = description;

		this.authors = authors;

		this.version = version;

		this.defaultEnabled = defaultEnabled;

		Modules.register(this);
	}

	@Override
	public String getModuleDescription()
	{
		return this.description;
	}

	@Override
	public String getModuleAuthors()
	{
		return this.authors;
	}

	@Override
	public String getModuleVersion()
	{
		return this.version;
	}

	@Override
	public Plugin getOwningPlugin()
	{
		for (IModule module : this.getTree())
		{
			if (!(module instanceof Plugin)) continue;

			this.owner = (Plugin) module;

			return this.owner;
		}

		return Assert.notNull(null, "Module owner not found!");
	}

	@Override
	public final void test(Object data)
	{
		this.log("Testing {0}", this.name);

		this.runTest(data);
	}

	@Override
	public final boolean getModuleDefault()
	{
		return this.defaultEnabled;
	}

	@Override
	public boolean isModuleEnabled()
	{
		return this.enabled;
	}

	@Override
	public void setModuleEnabled(boolean enable)
	{
		this.enabled = enable;
	}

	@Override
	public IModule getParentModule()
	{
		return this.parent;
	}

	@Override
	public void setParentModule(IModule module)
	{
		this.parent = module;
	}

	@Override
	public List<IModule> getSubModules()
	{
		return this.sub;
	}

	@Override
	public void withSubModule(IModule module)
	{
		if (this.sub == null)
		{
			this.sub = Lists.newArrayList();
		}

		module.setParentModule(this);

		this.sub.add(module);

		if (!this.enabled) return;

		module.tryEnable();
	}

	@Override
	public void withConf()
	{
		this.hasConfig = true;
	}

	@Override
	public boolean hasConf()
	{
		return this.hasConfig;
	}

	@Override
	public ConfigFile getConf()
	{
		if (!this.hasConfig) throw new NullPointerException("This module does not have a config!");

		return this.config;
	}

	@Override
	public void loadConf()
	{
		this.log("Loading config file for {0}", this.name);

		this.config = Persist.newConfig(new File(this.getOwningPlugin().getDataFolder(), this.name + ".yml"));

		if (!this.config.makeFile()) return;

		this.log("Creating empty config file for {0}", this.name);
	}

	@Override
	public void saveConf()
	{
		this.config.save();
	}

	@Override
	public int enabledCount()
	{
		return this.enabledCount;
	}

	@Override
	public void incrementEnabledCount()
	{
		this.enabledCount++;
	}

	@Override
	public Map<ListenerReg, Set<Listener>> getListeners()
	{
		return this.listeners;
	}

	@Override
	public void addListener(Listener listener)
	{
		if (this.listeners == null) this.listeners = Maps.newEnumMap(ListenerReg.class);

		ListenerReg reg = this.enabled ? ListenerReg.REGISTERED : ListenerReg.PENDING;

		Set<Listener> listeners = this.listeners.get(reg);

		if (listeners == null)
		{
			listeners = new HashSet<>(); // sets module not yet enabled

			this.listeners.put(reg, listeners);
		}

		listeners.add(listener);

		if (!this.enabled) return;

		Listeners.register(this, listener);
	}

	@Override
	public Map<ModuleTask.StartType, Set<ModuleTask>> getTasks()
	{
		return this.tasks;
	}

	@Override
	public void addTask(ModuleTask task)
	{
		if (this.tasks == null) this.tasks = Maps.newEnumMap(ModuleTask.StartType.class);

		Set<ModuleTask> tasks = this.tasks.get(task.getType());

		if (tasks == null)
		{
			tasks = new HashSet<>(); // sets module not enabled

			this.tasks.put(task.getType(), tasks);
		}

		tasks.add(task);

		if (!this.enabled) return;

		if (task.getType() != ModuleTask.StartType.AUTOMATIC) return;

		task.start();
	}

	@Override
	public Map<String, CommandExecutor> getCommands()
	{
		return this.commands;
	}

	@Override
	public void addCommand(String command, CommandExecutor executor)
	{
		if (this.commands == null) this.commands = Maps.newHashMap();

		this.commands.put(command, executor);

		if (!this.enabled) return;

		Commands.register(this, command, executor);
	}

	@Override
	public final String getName()
	{
		return this.name;
	}

}
