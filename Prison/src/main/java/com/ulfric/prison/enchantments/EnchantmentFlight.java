package com.ulfric.prison.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

public final class EnchantmentFlight extends PrisonEnchantment {

	private static final EnchantmentFlight INSTANCE = new EnchantmentFlight();
	public static EnchantmentFlight get() { return EnchantmentFlight.INSTANCE; }

	private EnchantmentFlight()
	{
		super(202, "FLIGHT");
	}

	@Override
	public EnchantmentTarget getItemTarget()
	{
		return EnchantmentTarget.TOOL;
	}

	@Override
	public int getMaxLevel()
	{
		return 1;
	}

}