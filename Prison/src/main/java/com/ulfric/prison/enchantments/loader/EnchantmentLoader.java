package com.ulfric.prison.enchantments.loader;

import java.util.Set;

import com.ulfric.prison.enchantments.LoadableEnchantment;

public class EnchantmentLoader {

	protected static IEnchantmentLoader impl = IEnchantmentLoader.EMPTY;

	public static Set<LoadableEnchantment> getEnchants(EnchantmentType type)
	{
		return EnchantmentLoader.impl.getEnchants(type);
	}

	protected interface IEnchantmentLoader
	{
		IEnchantmentLoader EMPTY = new IEnchantmentLoader() { };

		default Set<LoadableEnchantment> getEnchants(EnchantmentType type) { return null; }
	}

}