package com.ulfric.prison.modules;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerDamageEvent;

public class ModuleVoid extends SimpleModule {

	public ModuleVoid()
	{
		super("void", "Module for interactions with the void", "Packet", "1.0.0-REL");
	}

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener()
		{
			@EventHandler
			public void onDamage(PlayerDamageEvent event)
			{
				if (!event.getCause().equals(DamageCause.VOID)) return;

				event.setCancelled(true);

				Hooks.ESS.getSpawnpoint().teleport(event.getPlayer());
			}
		});
	}

}