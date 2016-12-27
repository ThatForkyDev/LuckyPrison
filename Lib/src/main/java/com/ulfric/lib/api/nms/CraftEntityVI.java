package com.ulfric.lib.api.nms;

import com.ulfric.lib.api.java.Proxy;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

public final class CraftEntityVI implements Proxy<CraftEntity> {

	static ICraftEntityVI impl = ICraftEntityVI.EMPTY;
	private final CraftEntity entity;

	CraftEntityVI(Entity entity)
	{
		this.entity = (CraftEntity) entity;
	}

	public static CraftEntityVI of(Entity entity)
	{
		return impl.of(entity);
	}

	public CraftEntity getEntity()
	{
		return this.entity;
	}

	public net.minecraft.server.v1_8_R3.Entity getHandle()
	{
		return this.entity.getHandle();
	}

	@Override
	@Deprecated
	public CraftEntity get()
	{
		return this.entity;
	}

	protected interface ICraftEntityVI {
		ICraftEntityVI EMPTY = new ICraftEntityVI() {
		};

		default CraftEntityVI of(Entity entity)
		{
			return null;
		}
	}

}
