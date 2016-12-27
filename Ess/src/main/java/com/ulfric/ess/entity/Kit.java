package com.ulfric.ess.entity;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Named;

public class Kit implements Named {


	public Kit(String name, ItemStack[] contents, long cooldown)
	{
		this.name = name;
		this.contents = contents;
		this.cooldown = cooldown;
	}

	private String name;
	@Override
	public String getName() { return this.name; }
	public void setName(String name) { this.name = name; }

	private final ItemStack[] contents;
	public ItemStack[] getContents()
	{
		return Arrays.copyOf(this.contents, this.contents.length);
	}

	private final long cooldown;
	public long getCooldown() { return this.cooldown; }
	public boolean hasCooldown() { return this.getCooldown() != 0; }
	public boolean isSingular() { return this.getCooldown() == -1; }

	public KitCooldown canUse(Player player)
	{
		if (!this.hasCooldown() || player == null || player.hasPermission("ess.kit.nocooldown")) return KitCooldown.NO_COOLDOWN;

		UUID uuid = player.getUniqueId();

		String path = "cooldown.kits." + this.getName().toLowerCase();

		long lastUsed = Hooks.DATA.getPlayerDataAsLong(uuid, path);

		long current = System.currentTimeMillis();

		long cooldown = 0;

		if (lastUsed > 0)
		{
			if (this.isSingular()) return KitCooldown.ONE_TIME;

			cooldown = (this.getCooldown() - (current - lastUsed)) / 1000;

			if (cooldown >= 0) return new KitCooldown(KitUsageType.CANNOT_USE_COOLDOWN, cooldown);
		}

		return new KitCooldown(KitUsageType.CAN_USE_COOLDOWN, cooldown);
	}

	public static class KitCooldown
	{
		private static final KitCooldown NO_COOLDOWN = new KitCooldown(KitUsageType.CAN_USE);
		private static final KitCooldown ONE_TIME = new KitCooldown(KitUsageType.CANNOT_USE_SINGULAR, -1);

		private KitCooldown(KitUsageType type)
		{
			this(type, 0);
		}
		private KitCooldown(KitUsageType type, long cooldown)
		{
			this.type = type;
			this.cooldown = cooldown;
		}

		private final KitUsageType type;
		public KitUsageType getType() { return this.type; }

		private final long cooldown;
		public long getCooldown() { return this.cooldown; }
	}

	public enum KitUsageType
	{
		CAN_USE,
		CAN_USE_COOLDOWN,
		CANNOT_USE_COOLDOWN,
		CANNOT_USE_SINGULAR;
	}


}