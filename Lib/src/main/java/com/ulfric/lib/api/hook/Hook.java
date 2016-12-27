package com.ulfric.lib.api.hook;

import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.module.SimpleModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class Hook<T extends HookImpl> extends SimpleModule {

	private final String[] names;
	protected T impl;
	private String pluginName;
	private Plugin plugin;

	protected Hook(T empty, String name, String description, String authors, String version)
	{
		this(empty, new String[]{name}, description, authors, version);
	}

	protected Hook(T empty, String[] names, String description, String authors, String version)
	{
		super(names[0] + "-hook", description, authors, version);

		this.impl = empty;
		this.names = Arrays.copyOf(names, names.length);
	}

	protected void impl(T impl)
	{
		this.impl = impl;
	}

	public T getImpl()
	{
		return this.impl;
	}

	public final String getPluginName()
	{
		return this.pluginName;
	}

	public final Plugin getPlugin()
	{
		return this.plugin;
	}

	public void implement(Plugin plugin, T impl)
	{
		Assert.isFalse(this.isModuleEnabled());

		this.plugin = plugin;

		this.impl = impl;

		this.enable();
	}

	public void onHook()
	{
	}

	@Override
	public final void enable()
	{
		super.enable();

		if (!this.isModuleEnabled()) return;

		Plugin plug = null;
		for (String name : this.names)
		{
			plug = Bukkit.getPluginManager().getPlugin(name);
			if (plug != null)
			{
				this.pluginName = name;
				break;
			}
		}

		if (plug == null)
		{
			this.log("Unable to find plugin {0}", Arrays.toString(this.names));
			this.disable();

			return;
		}

		this.log("Hooking {0}", this.pluginName);

		this.plugin = plug;

		Hooks.register(this);

		this.onHook();
	}

	@Override
	public final void disable()
	{
		super.disable();

		Hooks.remove(this);
	}

}
