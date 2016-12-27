package com.ulfric.lib.api.module;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.hook.Hook;
import com.ulfric.lib.api.hook.HookImpl;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.persist.ConfigFile;
import com.ulfric.lib.api.server.Commands;
import com.ulfric.lib.api.server.Events;
import com.ulfric.lib.api.server.Listeners;
import com.ulfric.lib.api.task.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Plugin extends JavaPlugin implements IModule {

	private Map<ListenerReg, Set<Listener>> listeners;
	private Map<String, CommandExecutor> commands;
	private Map<ModuleTask.StartType, Set<ModuleTask>> tasks;
	private boolean enabled;
	private List<IModule> sub;
	private int enabledCount;

	/**
	 * Returns the module description
	 */
	@Override
	public String getModuleDescription()
	{
		return this.getDescription().getDescription();
	}

	/**
	 * Returns the module authors
	 */
	@Override
	public String getModuleAuthors()
	{
		return StringUtils.mergeNicely(this.getDescription().getAuthors());
	}

	/**
	 * Returns the module version
	 */
	@Override
	public String getModuleVersion()
	{
		return this.getDescription().getVersion();
	}

	/**
	 * Returns this.
	 */
	@Override
	public Plugin getOwningPlugin()
	{
		return this;
	}

	@Override
	public final void test(Object data)
	{
		this.log("Testing {0}", this.getName());

		this.runTest(data);
	}

	@Override
	public void log(String message, Object... objects)
	{
		this.getLogger().info(Strings.format(message, objects));
	}

	/**
	 * Returns true if the module is enabled
	 */
	@Override
	public boolean isModuleEnabled()
	{
		return this.enabled;
	}

	/**
	 * Changes the status of the module.
	 * The enable and disable methods shoud
	 * be used in most cases.
	 */
	@Override
	public void setModuleEnabled(boolean enable)
	{
		this.enabled = enable;
	}

	/**
	 * Gets a list of submodules that DIRECTLY
	 * (child by 1) inherit this module.
	 */
	@Override
	public List<IModule> getSubModules()
	{
		return this.sub;
	}

	/**
	 * Adds a submodule to this module
	 */
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

	@Deprecated
	@Override
	public void withConf()
	{
		throw new UnsupportedOperationException("Plugin modules should use the config.yml");
	}

	@Deprecated
	@Override
	public boolean hasConf()
	{
		return false;
	}

	@Deprecated
	@Override
	public ConfigFile getConf()
	{
		throw new UnsupportedOperationException("Plugin modules should use the config.yml");
	}

	@Deprecated
	@Override
	public void loadConf()
	{
		throw new UnsupportedOperationException("Plugin modules should use the config.yml");
	}

	@Deprecated
	@Override
	public void saveConf()
	{
		throw new UnsupportedOperationException("Plugin modules should use the config.yml");
	}

	/**
	 * Called onEnable()
	 */
	@Override
	public void enable()
	{
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

	/**
	 * Called onDisable()
	 */
	@Override
	public void disable()
	{
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

		task.getTask().start();
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
	public final void onLoad()
	{
		Modules.register(this);

		this.preLoad();

		if (!Modules.canEnable(this)) return;

		this.load();

		this.enabled = true;
	}

	@Override
	public final void onDisable()
	{
		if (!this.enabled) return;

		this.preDisable();

		this.enabled = false;

		this.disable();

		Events.call(new ModuleEnableEvent(this));

		this.unregisterCommands();

		this.unregisterListeners();

		this.cancelTasks();

		if (!this.hasSubModules()) return;

		for (int x = this.sub.size() - 1; x >= 0; x--)
		{
			IModule module = this.sub.get(x);

			if (!module.isModuleEnabled()) continue;

			module.disable();
		}
	}

	@Override
	public final void onEnable()
	{
		if (!this.enabled)
		{
			Tasks.run(() -> this.setEnabled(false));
			return;
		}

		this.preEnable();

		if (this.enabled)
		{
			this.enableSubModules();
		}

		this.tryEnable();

		Events.call(new ModuleEnableEvent(this));

		this.registerCommands();
		this.registerListeners();
		this.startAutomaticTasks();

		Tasks.run(this::postEnable);

		this.incrementEnabledCount();

		Map<String, Map<String, Object>> commands = this.getDescription().getCommands();
		if (CollectUtils.isEmpty(commands)) return;

		for (String command : commands.keySet())
		{
			PluginCommand cmd = this.getCommand(command);
			if (cmd == null || cmd.getExecutor() != this) continue;

			cmd.setExecutor(new DisabledCommand());
		}
	}

	/**
	 * Called onLoad()
	 */
	public void load()
	{
	}

	/**
	 * Called before this module is ensured to be enableable
	 */
	public void preLoad()
	{
	}

	/**
	 * Called last, after the server has loaded all plugins
	 */
	public void postEnable()
	{
	}

	/**
	 * Logs something as a warning message
	 */
	public void warn(String string, Object... objects)
	{
		this.getLogger().warning(Strings.format(string, objects));
	}

	/**
	 * Logs something as a debug message
	 */
	public void debug(String string, Object... objects)
	{
		this.getLogger().info(Strings.format("[Debug] {0}", Strings.format(string, objects)));
	}

	/**
	 * Register a plugin hook
	 *
	 * @param <T>
	 * @param hook The hook to register
	 */
	public <T extends HookImpl> void registerHook(Hook<T> hook, T impl)
	{
		hook.implement(this, impl);

		Hooks.register(hook);
	}

	/**
	 * Get another plugin by name
	 *
	 * @param plugin The plugin's name
	 * @return The plugin object, or null if it doesn't exist
	 */
	public org.bukkit.plugin.Plugin getPlugin(String plugin)
	{
		return Bukkit.getPluginManager().getPlugin(plugin);
	}

}
