package com.ulfric.lib.api;

import com.ulfric.lib.api.chat.ChatModule;
import com.ulfric.lib.api.entity.EntityModule;
import com.ulfric.lib.api.hook.HookModule;
import com.ulfric.lib.api.inventory.InventoryModule;
import com.ulfric.lib.api.java.JavaModule;
import com.ulfric.lib.api.locale.LocaleModule;
import com.ulfric.lib.api.location.LocationModule;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.nms.NmsModule;
import com.ulfric.lib.api.persist.PersistModule;
import com.ulfric.lib.api.server.PluginsModule;
import com.ulfric.lib.api.task.TasksModule;

/**
 * API module parent for all other Lib API modules.
 * This module contains no features or implementation,
 * and simply acts as a container.
 *
 * @author Adam
 * @see JavaModule
 * @see TasksModule
 * @see PluginsModule
 * @see ChatModule
 * @see PersistModule
 * @see LocationModule
 * @see EntityModule
 * @see LocaleModule
 * @see NmsModule
 * @see InventoryModule
 * @see HookModule
 */
public final class ApiModule extends SimpleModule {

	public ApiModule()
	{
		super("api", "API parent module", "Packet", "1.0.0-REL");

		this.withSubModule(new JavaModule());
		this.withSubModule(new TasksModule());
		this.withSubModule(new PluginsModule());
		this.withSubModule(new ChatModule());
		this.withSubModule(new PersistModule());
		this.withSubModule(new LocationModule());
		this.withSubModule(new EntityModule());
		this.withSubModule(new LocaleModule());
		this.withSubModule(new NmsModule());
		this.withSubModule(new InventoryModule());
		this.withSubModule(new HookModule());
	}

}
