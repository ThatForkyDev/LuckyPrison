package com.ulfric.prison.listeners;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.inventory.InventoryUtils;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.inventory.MaterialUtils;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.math.RandomUtils;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.time.Milliseconds;
import com.ulfric.lib.api.tuple.Pair;
import com.ulfric.lib.api.world.Worlds;
import com.ulfric.prison.configuration.ConfigurationStore;
import com.ulfric.prison.entity.Minebuddy;
import com.ulfric.prison.lang.Meta;
import com.ulfric.prison.lang.Permissions;

public class ListenerBlock implements Listener {

	/*@EventHandler(ignoreCancelled = true)
	public void onBlockHit(BlockDamageEvent event)
	{
		Player player = event.getPlayer();

		if (!player.hasMetadata("instabreak")) return;

		Block block = event.getBlock();

		Metadata.applyNull(block, Meta.ARTIFICIAL_BREAK);

		BlockBreakEvent bevent = new BlockBreakEvent(block, player);

		Events.call(bevent);

		Metadata.remove(block, Meta.ARTIFICIAL_BREAK);

		if (bevent.isCancelled()) return;

		block.setType(Material.AIR, false);
	}*/
	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent event)
	{
		final Player constPlayer = event.getPlayer();
		Player player = constPlayer;

		if (!player.getGameMode().equals(GameMode.SURVIVAL)) return;

		Block block = event.getBlock();	

		if (!block.getWorld().equals(Worlds.main())) return;

		Material material = block.getType();

		if (material.equals(Material.LEAVES) || material.equals(Material.LEAVES_2)) return;

		this.cancel(event);

		if (block.getDrops().isEmpty())
		{
			block.setType(Material.AIR, false);

			return;
		}

		Optional<ItemStack> itemo = Optional.ofNullable(player.getItemInHand());
		ItemStack item = itemo.orElseGet(ItemUtils::blank);

		Collection<ItemStack> drops = block.getDrops(item);
		Iterator<ItemStack> dropIter;
		if (!CollectUtils.isEmpty(drops) && (dropIter = drops.iterator()).hasNext()) drop:
		{
			ItemStack drop = dropIter.next();

			if (item.getEnchantmentLevel(Enchantment.SILK_TOUCH) == 0)
			{
				int fortune = item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

				if (fortune > 0)
				{
					drop.setAmount((int) (4 + Math.max(Math.ceil(fortune / RandomUtils.randomRange(4.20, 5)), 1)));
				}
				else
				{
					drop.setAmount(4);
				}

				Material ingot = MaterialUtils.smelt(drop.getType());

				if (ingot != null)
				{
					drop.setType(ingot);
				}
			}
			else
			{
				Material type = block.getType();
				if (type == Material.GLOWING_REDSTONE_ORE)
				{
					type = Material.REDSTONE_ORE;
				}

				drop.setType(type);
				drop.setDurability(block.getData());
				String name = null;

				switch (type)
				{
					case ENDER_STONE:
						name = ChatColor.LIGHT_PURPLE + "Super Lucky Block";
						break;

					case SPONGE:
						name = ChatColor.YELLOW + "Lucky Block";
						break;

					default:
						break;
				}

				if (name != null)
				{
					ItemMeta meta = drop.getItemMeta();

					meta.setDisplayName(name);

					drop.setItemMeta(meta);
				}
			}

			if (player.hasMetadata(Meta.MINEBUDDY)) minebuddy:
			{
				Minebuddy buddy = Metadata.getValue(player, Meta.MINEBUDDY);

				Player partner;

				if (buddy == null || !(partner = buddy.getPartner(player)).isOnline()) break minebuddy;

				player = partner;

				buddy.addItemTotal(drop.getAmount());
			}

			Inventory inventory = player.getInventory();

			if (inventory == null)
			{
				inventory = constPlayer.getInventory();

				if (inventory == null) break drop;
			}

			if (InventoryUtils.hasRoomFor(inventory, drop, true))
			{
				inventory.addItem(drop);

				break drop;
			}

			if (!player.hasPermission(Permissions.SELL_FROM_ECHEST) || !InventoryUtils.hasRoomFor(inventory = player.getEnderChest(), drop, true))
			{
				PlayerUtils.sendTitle(player, Locale.getMessage(player, "prison.inventory_full_alert"));

				break drop;
			}

			Locale.sendTimelock(player, Locale.getMessage(player, "prison.inventory_full_notice"), Milliseconds.fromMinutes(0.75));

			inventory.addItem(drop);
		}

		block.setType(Material.AIR, false);

	}

	public void cancel(BlockBreakEvent event)
	{
		event.setCancelled(true);

		// TODO: Was this useful?
		//event.setNotify(false);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPreClick(PlayerInteractEvent event)
	{
		Action action = event.getAction();

		boolean rcb = action.equals(Action.RIGHT_CLICK_BLOCK);

		Player player = event.getPlayer();

		if (!rcb && !action.equals(Action.LEFT_CLICK_BLOCK)) return;

		Pair<String, String> tuple = ConfigurationStore.INSTANCE.getPermissible(event.getClickedBlock());

		if (tuple == null) return;

		if (player.hasPermission(tuple.getB())) return;

		event.setCancelled(true);

		Locale.sendTimelock(player, Locale.asError(player, tuple.getB()), Milliseconds.SECOND);
	}

}
