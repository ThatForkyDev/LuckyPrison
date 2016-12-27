package com.ulfric.ess.modules;

import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.ulfric.lib.api.entity.EntityUtils;
import com.ulfric.lib.api.module.SimpleModule;

public class ModuleMobdisabler extends SimpleModule {

	public ModuleMobdisabler()
	{
		super("mobdisabler", "Disables specific configurable mobs", "Packet", "1.0.0-REL");

		this.withConf();

		this.addListener(new Listener()
		{
			@EventHandler(ignoreCancelled = true)
			public void onSpawn(CreatureSpawnEvent event)
			{
				if (event.getSpawnReason().equals(SpawnReason.CUSTOM)) return;

				if (!ModuleMobdisabler.this.denied.contains(event.getEntityType())) return;

				event.setCancelled(true);
			}
		});
	}

	private Set<EntityType> denied;

	@Override
	public void postEnable()
	{
		this.denied = this.getConf().getValueAsStringList("disable").stream().map(EntityUtils::parse).collect(Collectors.toSet());
	}

}