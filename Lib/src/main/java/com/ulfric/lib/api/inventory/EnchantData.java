package com.ulfric.lib.api.inventory;

import com.google.common.collect.Lists;
import org.bukkit.enchantments.Enchantment;

import java.util.List;

public final class EnchantData {

	private Enchantment enchant;
	private List<String> aliases;
	private int max = -1;

	EnchantData(Enchantment enchant)
	{
		this.enchant = enchant;
	}

	public Enchantment getEnchant()
	{
		return this.enchant;
	}

	public void setEnchant(Enchantment enchant)
	{
		this.enchant = enchant;
	}

	public List<String> getAliases()
	{
		return this.aliases;
	}

	public void addAlias(String alias)
	{
		alias = alias.toLowerCase();

		if (this.aliases == null)
		{
			this.aliases = Lists.newArrayList(alias);

			return;
		}

		if (this.aliases.contains(alias))
		{
			EnchantUtils.impl.getModule().log("Tried to add duplicate alias '{0}' for enchantment {1}", alias, this.enchant.getName());

			return;
		}

		this.aliases.add(alias);
	}

	public int getMax()
	{
		return this.max;
	}

	public void setMax(int level)
	{
		this.max = level;
	}

}
