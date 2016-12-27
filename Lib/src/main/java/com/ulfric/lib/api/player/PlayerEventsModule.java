package com.ulfric.lib.api.player;

import com.ulfric.lib.api.block.SignUtils;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.server.Events;
import com.ulfric.lib.api.teleport.TeleportTask;
import com.ulfric.uspigot.event.player.PlayerItemStackHeldEvent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class PlayerEventsModule extends SimpleModule {

	public PlayerEventsModule()
	{
		super("playerevents", "Extended player events api", "Packet", "1.0.1-REL");

		this.withSubModule(new FightEventsModule());
		this.withSubModule(new InventoryEventsModule());
		this.withSubModule(new MoveEventsModule());
		this.withSubModule(new InteractEventsModule());
		this.withSubModule(new ConnectEventsModule());
	}

	private static final class ConnectEventsModule extends SimpleModule {
		ConnectEventsModule()
		{
			super("connectevents", "Extended player networking events api", "Packet", "1.0.0-REL");

			this.addListener(new Listener() {
				@EventHandler
				public void onJoin(PlayerJoinEvent event)
				{
					Player player = event.getPlayer();

					if (player.hasPlayedBefore()) return;

					Events.call(new PlayerFirstJoinEvent(player));
				}
			});
		}
	}

	private static final class InteractEventsModule extends SimpleModule {
		InteractEventsModule()
		{
			super("interactevents", "Extended interact events api", "Packet", "1.0.1-REL");

			this.addListener(new Listener() {
				@EventHandler(ignoreCancelled = true)
				public void onSign(PlayerInteractEvent event)
				{
					Action action = event.getAction();
					boolean right = action == Action.RIGHT_CLICK_BLOCK;

					if (!right && action != Action.LEFT_CLICK_BLOCK) return;
					if (!SignUtils.isSign(event.getClickedBlock())) return;

					PlayerUseSignEvent call = new PlayerUseSignEvent(event.getPlayer(),
																	 SignUtils.asSign(event.getClickedBlock()),
																	 right ? PlayerUseSignEvent.ClickType.RIGHT
																		   : PlayerUseSignEvent.ClickType.LEFT);

					if (Events.call(call).isCancelled()) event.setCancelled(true);
				}
			});
		}
	}

	private static final class InventoryEventsModule extends SimpleModule {
		InventoryEventsModule()
		{
			super("inventoryevents", "Extended inventory events api", "Packet", "1.0.0-REL");

			this.addListener(new Listener() {
				@EventHandler
				public void onJoin(PlayerJoinEvent event)
				{
					Events.call(new PlayerItemStackHeldEvent(event.getPlayer(), null, event.getPlayer().getItemInHand()));
				}

				@EventHandler(priority = EventPriority.MONITOR)
				public void onJoin(PlayerDeathEvent event)
				{
					if (event.getKeepInventory()) return;

					Events.call(new PlayerItemStackHeldEvent(event.getEntity(), event.getEntity().getItemInHand(), null));
				}

				@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
				public void onDrop(InventoryClickEvent event)
				{
					Inventory cinventory = event.getClickedInventory();

					if (cinventory == null) return;

					if (event.getView().getTopInventory().getType() != InventoryType.PLAYER && event.getView().getBottomInventory().getType() != InventoryType.PLAYER)
					{ return; }

					PlayerInventory inventory = ((PlayerInventory) (event.getView().getTopInventory() instanceof PlayerInventory ? event.getView().getTopInventory() : event.getView().getBottomInventory()));
					if (event.getClick() == ClickType.NUMBER_KEY)
					{
						int button = event.getHotbarButton();
						if (button != inventory.getHeldItemSlot() && event.getSlot() != inventory.getHeldItemSlot())
						{ return; }

						ItemStack newItem = button == inventory.getHeldItemSlot() ? event.getCurrentItem() : inventory.getItem(button);
						ItemStack hand = inventory.getItemInHand();

						if (newItem != null && hand != null)
						{
							if (newItem.isSimilar(hand)) return;
						}
						else if (newItem == null && hand == null) return;

						Events.call(new PlayerItemStackHeldEvent((Player) inventory.getHolder(), hand, newItem));
						return;
					}
					if (event.getClickedInventory().getType() != InventoryType.PLAYER) return;

					if (event.getSlot() != inventory.getHeldItemSlot()) return;

					ItemStack hand = inventory.getItemInHand();

					ItemStack cursor = event.getCursor();

					if (cursor.isSimilar(hand)) return;

					Events.call(new PlayerItemStackHeldEvent((Player) inventory.getHolder(), hand, cursor));
				}

				@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
				public void onDrop(PlayerDropItemEvent event)
				{
					ItemStack current = event.getPlayer().getItemInHand();

					ItemStack thrown = event.getItemDrop().getItemStack();

					if (current.isSimilar(thrown)) return;

					Events.call(new PlayerItemStackHeldEvent(event.getPlayer(), thrown, current));
				}

				@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
				public void onSwitch(PlayerItemHeldEvent event)
				{
					Inventory inventory = event.getPlayer().getInventory();

					Events.call(new PlayerItemStackHeldEvent(event.getPlayer(), inventory.getItem(event.getPreviousSlot()), inventory.getItem(event.getNewSlot())));
				}
			});
		}
	}

	private static final class FightEventsModule extends SimpleModule {
		FightEventsModule()
		{
			super("fightevents", "Extended fight events api", "Packet", "1.0.0-REL");

			this.addListener(new Listener() {
				@EventHandler(priority = EventPriority.HIGHEST)
				public void onPlayerDamaged(EntityDamageEvent event)
				{
					Entity entity = event.getEntity();

					if (!(entity instanceof Player)) return;

					PlayerDamageEvent call = new PlayerDamageEvent((Player) entity, event.getCause());
					call.setCancelled(event.isCancelled());

					Events.call(call);

					event.setCancelled(call.isCancelled());
				}

				@EventHandler(priority = EventPriority.HIGHEST)
				public void onPlayerDamageEntity(EntityDamageByEntityEvent event)
				{
					Player damager = PlayerUtils.getLinked(event.getDamager());

					if (damager == null) return;

					PlayerDamageEntityEvent call = new PlayerDamageEntityEvent(damager, event.getEntity());
					call.setCancelled(event.isCancelled());

					Events.call(call);

					event.setCancelled(call.isCancelled());
				}

				@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
				public void onPlayerDamagePlayer(PlayerDamageEntityEvent event)
				{
					Entity damaged = event.getDamaged();

					if (!(damaged instanceof Player)) return;

					PlayerDamagePlayerEvent call = new PlayerDamagePlayerEvent(event.getPlayer(), (Player) damaged);
					call.setCancelled(event.isCancelled());

					Events.call(call);

					event.setCancelled(call.isCancelled());
				}
			});
		}
	}

	private static final class MoveEventsModule extends SimpleModule {
		MoveEventsModule()
		{
			super("moveevents", "Extended movement events api", "Packet", "1.0.0-REL");

			this.addListener(new Listener() {
				@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
				public void onMoveBlock(PlayerMoveEvent event)
				{
					Location from = event.getFrom();
					Location to = event.getTo();

					if (from.getBlockX() == to.getBlockX() &&
						from.getBlockY() == to.getBlockY() &&
						from.getBlockZ() == to.getBlockZ()) { return; }

					PlayerMoveBlockEvent pmbevent = new PlayerMoveBlockEvent(event.getPlayer(), from, to);

					if (!Events.call(pmbevent).isCancelled()) return;

					to.setX(from.getBlockX() + 0.5).setY(from.getY()).setZ(from.getBlockZ() + 0.5);
				}

				@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
				public void onMoveChunk(PlayerMoveBlockEvent event)
				{
					Player player = event.getPlayer();

					if (player.hasMetadata("_ulf_teleporting"))
					{
						TeleportTask task = Metadata.getValue(player, "_ulf_teleporting");

						task.kill(TeleportTask.TeleportCancelReason.TOO_FAR);
					}

					Chunk from = event.getFrom().getChunk();
					Chunk to = event.getTo().getChunk();

					if (from.equals(to)) return;

					PlayerMoveChunkEvent pmcevent = new PlayerMoveChunkEvent(event.getPlayer(), from, to);

					if (!Events.call(pmcevent).isCancelled()) return;

					Location locationFrom = event.getFrom();

					event.setTo(event.getTo().setX(locationFrom.getX()).setZ(locationFrom.getZ()));
				}

				@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
				public void onJumpStat(PlayerStatisticIncrementEvent event)
				{
					if (event.getStatistic() != Statistic.JUMP) return;

					Player player = event.getPlayer();

					PlayerJumpEvent jumpevent = new PlayerJumpEvent(player);

					if (!Events.call(jumpevent).isCancelled()) return;

					player.setVelocity(player.getVelocity().setY(0));
				}
			});
		}
	}

}
