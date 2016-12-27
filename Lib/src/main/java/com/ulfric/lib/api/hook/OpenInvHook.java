package com.ulfric.lib.api.hook;

import com.lishid.openinv.OpenInv;
import com.lishid.openinv.internal.SpecialPlayerInventory;
import com.ulfric.lib.api.hook.OpenInvHook.IOpenInvHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public final class OpenInvHook extends Hook<IOpenInvHook> {

	OpenInvHook()
	{
		super(IOpenInvHook.EMPTY, "OpenInv", "OpenInv hook module", "Packet", "1.0.0-REL");
	}

	@Override
	public void postEnable()
	{
		this.impl(new IOpenInvHook() {
			@Override
			public Player getPlayerOffline(UUID uuid)
			{
				return OpenInv.getPlayerLoader().loadPlayer(uuid);
			}

			@Override
			public Inventory getInventoryOffline(UUID uuid)
			{
				Player player = this.getPlayerOffline(uuid);

				if (player == null) return null;

				SpecialPlayerInventory inv = OpenInv.inventories.get(player.getUniqueId());

				if (inv == null)
				{
					inv = new SpecialPlayerInventory(player, player.isOnline());
				}

				return inv.getBukkitInventory();
			}

			@Override
			public void anyChest(Player player, boolean mode)
			{
				OpenInv.setPlayerAnyChestStatus(player, mode);
			}

			@Override
			public boolean anyChest(Player player)
			{
				return OpenInv.getPlayerAnyChestStatus(player);
			}

			@Override
			public void silentChest(Player player, boolean mode)
			{
				OpenInv.setPlayerSilentChestStatus(player, mode);
			}

			@Override
			public boolean silentChest(Player player)
			{
				return OpenInv.getPlayerSilentChestStatus(player);
			}
		});
	}

	public Player getPlayerOffline(UUID uuid)
	{
		return this.impl.getPlayerOffline(uuid);
	}

	public Inventory getInventoryOffline(UUID uuid)
	{
		return this.impl.getInventoryOffline(uuid);
	}

	public interface IOpenInvHook extends HookImpl {
		IOpenInvHook EMPTY = new IOpenInvHook() {
		};

		default Player getPlayerOffline(UUID uuid)
		{
			return null;
		}

		default Inventory getInventoryOffline(UUID uuid)
		{
			return null;
		}

		default void anyChest(Player player, boolean mode)
		{
		}

		default boolean anyChest(Player player)
		{
			return false;
		}

		default void silentChest(Player player, boolean mode)
		{
		}

		default boolean silentChest(Player player)
		{
			return false;
		}
	}

}
