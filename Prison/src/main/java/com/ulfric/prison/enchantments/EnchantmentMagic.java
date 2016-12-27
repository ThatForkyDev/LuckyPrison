package com.ulfric.prison.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

public final class EnchantmentMagic extends PrisonEnchantment {

	private static final EnchantmentMagic INSTANCE = new EnchantmentMagic();
	public static EnchantmentMagic get() { return EnchantmentMagic.INSTANCE; }

	private EnchantmentMagic()
	{
		super(203, "MAGIC");
	}

	@Override
	public EnchantmentTarget getItemTarget()
	{
		return EnchantmentTarget.TOOL;
	}

	@Override
	public int getMaxLevel()
	{
		return 4;
	}

}