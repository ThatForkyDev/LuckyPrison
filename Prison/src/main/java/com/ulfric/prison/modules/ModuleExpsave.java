package com.ulfric.prison.modules;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.prison.lang.Permissions;

public class ModuleExpsave extends SimpleModule {

	public ModuleExpsave()
	{
		super("expsave", "Prevent permissible users dropping experience", "StaticShadow", "1.0.0-REL");

		this.addListener(new Listener()
		{
			@EventHandler
			public void onPlayerDeath(PlayerDeathEvent event)
			{
				if (!event.getEntity().hasPermission(Permissions.STOP_EXP_DROP)) return;

				event.setKeepLevel(true);
				event.setDroppedExp(0);
			}
		});
	}

}