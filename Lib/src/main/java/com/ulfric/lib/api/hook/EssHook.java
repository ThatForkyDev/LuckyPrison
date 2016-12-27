package com.ulfric.lib.api.hook;

import com.ulfric.lib.api.hook.EssHook.IEss;
import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.location.LocationProxy;
import com.ulfric.lib.api.location.LocationUtils;
import com.ulfric.uspigot.Locatable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class EssHook extends Hook<IEss> {

	EssHook()
	{
		super(IEss.EMPTY, "Ess", "Ess hook module", "Packet", "1.0.0-REL");
	}

	public LocationProxy getSpawnpoint()
	{
		return this.impl.getSpawnpoint();
	}

	public IWarp warp(String name)
	{
		return this.impl.warp(name);
	}

	public void clearMotd()
	{
		this.impl.clearMotd();
	}

	public enum IKitUsageType {
		CAN_USE,
		CAN_USE_COOLDOWN,
		CANNOT_USE_COOLDOWN,
		CANNOT_USE_SINGULAR
	}

	public interface IEss extends HookImpl {
		IEss EMPTY = new IEss() {
		};

		default LocationProxy getSpawnpoint()
		{
			return LocationUtils.proxy(Bukkit.getWorlds().get(0).getSpawnLocation());
		}

		default IWarp warp(String name)
		{
			return null;
		}

		default void clearMotd()
		{
		}

		default IKit kit(String name)
		{
			return null;
		}
	}

	public interface IWarp extends Named, Locatable {
		int getWarmup();
	}

	public interface IKit extends Named {
		ItemStack[] getContents();

		boolean hasCooldown();

		long getCooldown();

		boolean isSingular();

		IKitCooldown canUse(Player player);
	}

	public interface IKitCooldown {
		IKitUsageType getType();

		long getCooldown();
	}

}
