package com.ulfric.ess.hook;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ulfric.ess.entity.Kit;
import com.ulfric.lib.api.hook.EssHook.IKit;
import com.ulfric.lib.api.hook.EssHook.IKitCooldown;

public class KitImpl implements IKit {

	protected KitImpl(Kit kit)
	{
		this.kit = kit;
	}

	private final Kit kit;

	@Override
	public String getName()
	{
		return this.kit.getName();
	}

	@Override
	public ItemStack[] getContents()
	{
		return this.kit.getContents();
	}

	@Override
	public boolean hasCooldown()
	{
		return this.kit.hasCooldown();
	}

	@Override
	public long getCooldown()
	{
		return this.kit.getCooldown();
	}

	@Override
	public boolean isSingular()
	{
		return this.kit.isSingular();
	}

	@Override
	public IKitCooldown canUse(Player player)
	{
		return new KitCooldownImpl(this.kit.canUse(player));
	}

}