package com.ulfric.ess.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerUtils;

public class CommandSpawnmob extends SimpleCommand {


	public CommandSpawnmob()
	{
		this.withEnforcePlayer();

		this.withArgument("ent", ArgStrategy.ENTITY, "ess.spawnmob_entity_missing");

		this.withArgument("int", ArgStrategy.POSITIVE_INTEGER, 1);

		this.withArgument("range", ArgStrategy.POSITIVE_INTEGER, 35);
	}


	@Override
	public void run()
	{
		EntityType type = (EntityType) this.getObject("ent");

		if (!type.isAlive())
		{
			Locale.sendError(this.getSender(), "ess.entity_not_living");

			return;
		}

		int times = (int) this.getObject("int");

		Location location = PlayerUtils.getTargetBlock(this.getPlayer(), Math.min(100, (int) this.getObject("range"))).getLocation().add(0, 1, 0);

		World world = location.getWorld();

		for (int x = 0; x < times; x++)
		{
			world.spawnEntity(location, type);
		}
	}


}