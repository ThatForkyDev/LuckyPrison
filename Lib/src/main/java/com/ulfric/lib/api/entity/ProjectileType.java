package com.ulfric.lib.api.entity;

import org.bukkit.entity.*;

public enum ProjectileType {


	ARROW(Arrow.class),
	SNOWBALL(Snowball.class),
	EGG(Egg.class),
	FIREBALL(Fireball.class),
	FIREBALL_BIG(LargeFireball.class),
	FIREBALL_SMALL(SmallFireball.class),
	FIREBALL_WITHER(WitherSkull.class),
	ENDERPEARL(EnderPearl.class),
	EXP_BOTTLE(ThrownExpBottle.class);


	private final Class<? extends Projectile> clazz;

	ProjectileType(Class<? extends Projectile> clazz)
	{
		this.clazz = clazz;
	}

	public Class<? extends Projectile> getClazz()
	{
		return this.clazz;
	}


}
