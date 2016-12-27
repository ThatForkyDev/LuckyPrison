package com.ulfric.prison.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

public final class EnchantmentSpeedyGonzales extends PrisonEnchantment {

	private static final EnchantmentSpeedyGonzales INSTANCE = new EnchantmentSpeedyGonzales();
	public static EnchantmentSpeedyGonzales get() { return EnchantmentSpeedyGonzales.INSTANCE; }

	private EnchantmentSpeedyGonzales()
	{
		super(205, "SPEEDY_GONZALES");
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