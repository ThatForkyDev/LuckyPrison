package com.ulfric.lib.api.inventory;

import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.math.NumberTranslator;
import com.ulfric.lib.api.tuple.Pair;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class Enchant extends Pair<Enchantment, Integer> {

	private Enchant(Enchantment enchant, Integer level)
	{
		super(enchant, level);
	}

	public static Enchant of(Enchantment enchant, Integer level)
	{
		Assert.notNull(enchant);

		return new Enchant(enchant, level == null ? 0 : level);
	}

	public Enchantment getEnchant()
	{
		return this.getA();
	}

	public Integer getLevel()
	{
		return this.getB();
	}

	public String toLore(boolean roman)
	{
		int level = this.getLevel();

		return Strings.formatF("&7{0} {1}", EnchantUtils.getHiddenName(this.getEnchant()), roman || level <= 10 ? NumberTranslator.roman(level) : level);
	}

	public void apply(ItemStack item, boolean roman)
	{
		item.setItemMeta(EnchantUtils.addFakeEnchant(item.getItemMeta(), this, roman));
	}

	public void apply(ItemMeta meta, boolean roman)
	{
		EnchantUtils.addFakeEnchant(meta, this, roman);
	}

}
