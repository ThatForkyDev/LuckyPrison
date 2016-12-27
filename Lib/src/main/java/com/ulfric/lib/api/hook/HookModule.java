package com.ulfric.lib.api.hook;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.module.SimpleModule;

import java.util.Map;

public final class HookModule extends SimpleModule {

	public HookModule()
	{
		super("hook", "Plugin hooking module", "Packet", "1.0.0-REL");

		this.withSubModule(Hooks.PERMISSIONS);
		this.withSubModule(Hooks.WORLDEDIT);
		this.withSubModule(Hooks.OPENINV);
		this.withSubModule(Hooks.PETS);
	}

	@Override
	public void postEnable()
	{
		Hooks.impl = new Hooks.IHooks() {
			private final Map<String, Hook<?>> hooks = Maps.newConcurrentMap();

			@Override
			public void register(Hook<?> hook)
			{
				this.hooks.put(hook.getName(), hook);
			}

			@Override
			public void remove(Hook<?> hook)
			{
				this.hooks.remove(hook.getName());
			}

			@Override
			public Hook<?> forName(String name)
			{
				return this.hooks.get(name);
			}
		};
	}

	@Override
	public void postDisable()
	{
		Hooks.impl = Hooks.IHooks.EMPTY;
	}

}
