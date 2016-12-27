package com.ulfric.lib.api.entity;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Builder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Map;

public final class EntityBuilder implements Builder<Entity> {

	static IEntityBuilder impl = IEntityBuilder.EMPTY;
	private Location location;
	private EntityType type;
	private Map<String, Object> metadata;
	private double health;
	private int fireticks;
	private String name;

	EntityBuilder()
	{
	}

	public static EntityBuilder create()
	{
		return impl.create();
	}

	@Override
	public Entity build()
	{
		Assert.notNull(this.location, "The location must not be null!");

		Assert.notNull(this.type, "The location must not be null!");

		Entity entity = EntityUtils.spawnEntity(this.type, this.location);

		if (this.metadata != null && !this.metadata.isEmpty())
		{
			for (Map.Entry<String, Object> entry : this.metadata.entrySet())
			{
				Metadata.apply(entity, entry.getKey(), entry.getValue());
			}
		}

		if (this.health > 0)
		{
			((Damageable) entity).setHealth(this.health);
		}

		if (!StringUtils.isBlank(this.name))
		{
			entity.setCustomName(this.name);

			entity.setCustomNameVisible(true);
		}

		entity.setFireTicks(this.fireticks);

		return entity;
	}

	public EntityBuilder withLocation(Location location)
	{
		Assert.notNull(location, "The location must not be null!");

		this.location = location;

		return this;
	}

	public EntityBuilder withType(EntityType type)
	{
		Assert.notNull(type, "The entity type must not be null!");

		this.type = type;

		return this;
	}

	public EntityBuilder withMetadata(String path, Object value)
	{
		Assert.isNotEmpty(path, "The path must not be null!", "The path must not be empty!");

		if (this.metadata == null) this.metadata = Maps.newHashMap();

		this.metadata.put(path, value);

		return this;
	}

	public EntityBuilder withFire(int fireticks)
	{
		Assert.isTrue(fireticks > 0, "The fire ticks must be positive!");

		this.fireticks = fireticks;

		return this;
	}

	public EntityBuilder withName(String name)
	{
		Assert.notNull(name, "The name must not be null (it can be empty)!");

		this.name = name;

		return this;
	}

	protected interface IEntityBuilder {
		IEntityBuilder EMPTY = new IEntityBuilder() {
		};

		default EntityBuilder create()
		{
			return null;
		}
	}


}
