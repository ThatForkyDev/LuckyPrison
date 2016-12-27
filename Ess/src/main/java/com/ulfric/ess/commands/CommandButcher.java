package com.ulfric.ess.commands;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;

public class CommandButcher extends SimpleCommand {

	public CommandButcher()
	{
		this.withEnforcePlayer();

		this.withArgument("range", ArgStrategy.INTEGER, -1);
		this.withArgument("type", ArgStrategy.ENTITY);
	}

	@Override
	public void run()
	{
		Collection<Entity> entities = null;

		int range = (int) this.getObject("range");

		EntityType type = (EntityType) this.getObject("type");

		if (range > 0)
		{
			entities = this.getPlayer().getNearbyEntities(range, range, range);
		}
		else
		{
			entities = this.getPlayer().getWorld().getEntities();
		}

		Iterator<Entity> iterator = entities.iterator();
		while (iterator.hasNext())
		{
			Entity entity = iterator.next();
			EntityType entityt = entity.getType();

			if (!entityt.equals(EntityType.PLAYER) && (type == null || !type.equals(entityt)))
			{
				entity.remove();

				continue;
			}

			iterator.remove();
		}

		if (entities.isEmpty())
		{
			Locale.sendError(this.getPlayer(), "ess.butcher_err");

			return;
		}

		Locale.sendSuccess(this.getPlayer(), "ess.butcher", entities.size());
	}

}