package com.ulfric.prison.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

public final class EnchantmentNeverbreaking extends PrisonEnchantment {

	private static final EnchantmentNeverbreaking INSTANCE = new EnchantmentNeverbreaking();
	public static EnchantmentNeverbreaking get() { return EnchantmentNeverbreaking.INSTANCE; }

	private EnchantmentNeverbreaking()
	{
		super(204, "NEVERBREAKING");
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