package com.ulfric.prison.enchantments;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.ulfric.lib.api.block.BlockPattern;
import com.ulfric.lib.api.java.Named;

public abstract class LoadableEnchantment extends Enchantment implements Named {

	protected LoadableEnchantment(int id)
	{
		super(id);
	}

	@Override
	public boolean canEnchantItem(ItemStack item)
	{
		return this.getItemTarget().includes(item);
	}

	@Override
	public int getStartLevel()
	{
		return 1;
	}

	public abstract BlockPattern getPattern();

	public abstract String getPatternName();

	public boolean hasPattern()
	{
		return this.getPattern() != null;
	}

	public abstract PotionEffectType getEffect();

	public boolean hasEffect()
	{
		return this.getEffect() != null;
	}

	public abstract PotionEffect getBaseEffect();

	public void apply(LivingEntity entity)
	{
		this.apply(entity, 1);
	}
	public void apply(LivingEntity entity, int level)
	{
		entity.addPotionEffect(level == 0 ? this.getBaseEffect() : new PotionEffect(this.getEffect(), Integer.MAX_VALUE, level, false, false), false);
	}

}