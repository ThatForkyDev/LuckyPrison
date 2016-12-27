package com.ulfric.lib.api.player;

import com.google.common.collect.ImmutableSet;
import com.ulfric.lib.api.hook.DataHook;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.java.Proxy;
import com.ulfric.lib.api.java.Unique;
import com.ulfric.uspigot.entity.ai.Controller;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import org.github.paperspigot.Title;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings("deprecation")
public final class PlayerProxy implements IPlayer, Named, Unique, Player, Proxy<Player> {

	private static Spigot emptySpigot;
	private final UUID uuid;
	private String name;
	private int hashcode;

	PlayerProxy(UUID uuid)
	{
		this.uuid = uuid;
	}

	PlayerProxy(Player player)
	{
		this.uuid = player.getUniqueId();

		this.name = player.getName();
	}

	public static Spigot emptySpigot()
	{
		Spigot spigot = emptySpigot;

		if (spigot != null) return spigot;

		synchronized (PlayerProxy.class)
		{
			spigot = emptySpigot;

			if (spigot != null) return spigot;

			spigot = new Player.Spigot() {
				@Override
				public InetSocketAddress getRawAddress()
				{
					return null;
				}

				@Override
				public void playEffect(Location location, Effect effect, int id, int data, float offsetX, float offsetY, float offsetZ, float speed, int particleCount, int radius)
				{
				}

				@Override
				public boolean getCollidesWithEntities()
				{
					return false;
				}

				@Override
				public void setCollidesWithEntities(boolean collides)
				{
				}

				@Override
				public void respawn()
				{
				}

				@Override
				public String getLocale()
				{
					return "en_US";
				}

				@Override
				public Set<Player> getHiddenPlayers()
				{
					return ImmutableSet.of();
				}

				@Override
				public void sendMessage(BaseComponent component)
				{
				}

				@Override
				public void sendMessage(BaseComponent... components)
				{
				}

				@Override
				public boolean getAffectsSpawning()
				{
					return true;
				}

				@Override
				public void setAffectsSpawning(boolean affects)
				{
				}

				@Override
				public int getViewDistance()
				{
					return Bukkit.getViewDistance();
				}

				@Override
				public void setViewDistance(int viewDistance)
				{
				}
			};

			emptySpigot = spigot;

			return spigot;
		}
	}

	@Override
	public UUID getUniqueId()
	{
		return this.uuid;
	}

	@Override
	public String getName()
	{
		if (this.name == null)
		{
			Player player = this.getPlayer();
			if (player == null)
			{
				DataHook.IPlayerData data = Hooks.DATA.getPlayerData(this.uuid);
				if (data == null) return null;

				this.name = (String) data.get("data.name");
			}
			else
			{
				this.name = player.getName();
			}

		}

		return this.name;
	}

	@Override
	public Player getPlayer()
	{
		if (this.uuid != null)
		{
			return Bukkit.getPlayer(this.uuid);
		}

		Assert.notNull(this.name);

		return Bukkit.getPlayerExact(this.name);
	}

	private <T> T func(Function<Player, T> function)
	{
		Player player = this.getPlayer();

		if (player == null) return null;

		return function.apply(this.getPlayer());
	}

	@Override
	public PlayerInventory getInventory()
	{
		return this.func(Player::getInventory);
	}

	@Override
	public Inventory getEnderChest()
	{
		return this.func(Player::getEnderChest);
	}

	@Override
	public boolean setWindowProperty(InventoryView.Property prop, int value)
	{
		return this.func(player -> player.setWindowProperty(prop, value));
	}

	@Override
	public InventoryView getOpenInventory()
	{
		return this.func(Player::getOpenInventory);
	}

	@Override
	public InventoryView openInventory(Inventory inventory)
	{
		return this.func(player -> player.openInventory(inventory));
	}

	@Override
	public InventoryView openWorkbench(Location location, boolean force)
	{
		return this.func(player -> player.openWorkbench(location, force));
	}

	@Override
	public InventoryView openEnchanting(Location location, boolean force)
	{
		return this.func(player -> player.openEnchanting(location, force));
	}

	@Override
	public void openInventory(InventoryView inventory)
	{
		this.consume(Player::updateInventory);
	}

	@Override
	public void closeInventory()
	{
		this.consume(Player::closeInventory);
	}

	@Override
	public ItemStack getItemInHand()
	{
		return this.func(Player::getItemInHand);
	}

	@Override
	public void setItemInHand(ItemStack item)
	{
		this.consume(player -> player.setItemInHand(item));
	}

	@Override
	public ItemStack getItemOnCursor()
	{
		return this.func(Player::getItemOnCursor);
	}

	@Override
	public void setItemOnCursor(ItemStack item)
	{
		this.consume(player -> player.setItemOnCursor(item));
	}

	@Override
	public boolean isSleeping()
	{
		return this.func(Player::isSleeping);
	}

	@Override
	public int getSleepTicks()
	{
		return Optional.ofNullable(this.func(Player::getSleepTicks)).orElse(0);
	}

	@Override
	public GameMode getGameMode()
	{
		return Optional.ofNullable(this.func(Player::getGameMode)).orElseGet(Bukkit::getDefaultGameMode);
	}

	@Override
	public void setGameMode(GameMode mode)
	{
		this.consume(player -> player.setGameMode(mode));
	}

	@Override
	public boolean isBlocking()
	{
		return Optional.ofNullable(this.func(Player::isBlocking)).orElse(Boolean.FALSE);
	}

	@Override
	public int getExpToLevel()
	{
		return this.func(Player::getExpToLevel);
	}

	@Override
	public double getEyeHeight()
	{
		return this.func(Player::getEyeHeight);
	}

	@Override
	public double getEyeHeight(boolean ignoreSneaking)
	{
		return this.func(player -> player.getEyeHeight(ignoreSneaking));
	}

	@Override
	public Location getEyeLocation()
	{
		return this.func(Player::getEyeLocation);
	}

	@Deprecated
	@Override
	public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance)
	{
		return this.func(player -> player.getLineOfSight(transparent, maxDistance));
	}

	@Override
	public List<Block> getLineOfSight(Set<Material> transparent, int maxDistance)
	{
		return this.func(player -> player.getLineOfSight(transparent, maxDistance));
	}

	@Deprecated
	@Override
	public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance)
	{
		return this.func(player -> player.getTargetBlock(transparent, maxDistance));
	}

	@Override
	public Block getTargetBlock(Set<Material> transparent, int maxDistance)
	{
		return this.func(player -> player.getTargetBlock(transparent, maxDistance));
	}

	@Deprecated
	@Override
	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance)
	{
		return this.func(player -> player.getLastTwoTargetBlocks(transparent, maxDistance));
	}

	@Override
	public List<Block> getLastTwoTargetBlocks(Set<Material> transparent, int maxDistance)
	{
		return this.func(player -> player.getLastTwoTargetBlocks(transparent, maxDistance));
	}

	@Deprecated
	@Override
	public Egg throwEgg()
	{
		return this.func(Player::throwEgg);
	}

	@Deprecated
	@Override
	public Snowball throwSnowball()
	{
		return this.func(Player::throwSnowball);
	}

	@Deprecated
	@Override
	public Arrow shootArrow()
	{
		return this.func(Player::shootArrow);
	}

	@Override
	public int getRemainingAir()
	{
		return this.func(Player::getRemainingAir);
	}

	@Override
	public void setRemainingAir(int ticks)
	{
		this.consume(player -> player.setRemainingAir(ticks));
	}

	@Override
	public int getMaximumAir()
	{
		return this.func(Player::getMaximumAir);
	}

	@Override
	public void setMaximumAir(int ticks)
	{
		this.consume(player -> player.setMaximumAir(ticks));
	}

	@Override
	public int getMaximumNoDamageTicks()
	{
		return this.func(Player::getMaximumNoDamageTicks);
	}

	@Override
	public void setMaximumNoDamageTicks(int ticks)
	{
		this.consume(player -> player.setMaximumNoDamageTicks(ticks));
	}

	@Override
	public double getLastDamage()
	{
		return this.func(Player::getLastDamage);
	}

	@Override
	public void setLastDamage(double damage)
	{
		this.consume(player -> player.setLastDamage(damage));
	}

	@Override
	public int getNoDamageTicks()
	{
		return this.func(Player::getNoDamageTicks);
	}

	@Override
	public void setNoDamageTicks(int ticks)
	{
		this.consume(player -> player.setNoDamageTicks(ticks));
	}

	@Override
	public Player getKiller()
	{
		return this.func(Player::getKiller);
	}

	@Override
	public boolean addPotionEffect(PotionEffect effect)
	{
		return this.func(player -> player.addPotionEffect(effect));
	}

	@Override
	public boolean addPotionEffect(PotionEffect effect, boolean force)
	{
		return this.func(player -> player.addPotionEffect(effect, force));
	}

	@Override
	public boolean addPotionEffects(Collection<PotionEffect> effects)
	{
		return this.func(player -> player.addPotionEffects(effects));
	}

	@Override
	public boolean hasPotionEffect(PotionEffectType type)
	{
		return this.func(player -> player.hasPotionEffect(type));
	}

	@Override
	public void removePotionEffect(PotionEffectType type)
	{
		this.consume(player -> player.removePotionEffect(type));
	}

	@Override
	public Collection<PotionEffect> getActivePotionEffects()
	{
		return this.func(Player::getActivePotionEffects);
	}

	@Override
	public boolean hasLineOfSight(Entity other)
	{
		return this.func(player -> player.hasLineOfSight(other));
	}

	@Override
	public boolean getRemoveWhenFarAway()
	{
		return this.func(Player::getRemoveWhenFarAway);
	}

	@Override
	public void setRemoveWhenFarAway(boolean remove)
	{
		this.consume(player -> player.setRemoveWhenFarAway(remove));
	}

	@Override
	public EntityEquipment getEquipment()
	{
		return this.func(Player::getEquipment);
	}

	@Override
	public void setCanPickupItems(boolean pickup)
	{
		this.consume(player -> player.setCanPickupItems(pickup));
	}

	@Override
	public Location getLocation()
	{
		return this.func(Player::getLocation);
	}

	@Override
	public Location getLocation(Location loc)
	{
		return this.func(player -> player.getLocation(loc));
	}

	@Override
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue)
	{
		this.consume(player -> player.setMetadata(metadataKey, newMetadataValue));
	}

	@Override
	public boolean getCanPickupItems()
	{
		return this.func(Player::getCanPickupItems);
	}

	@Override
	public List<MetadataValue> getMetadata(String metadataKey)
	{
		return this.func(player -> player.getMetadata(metadataKey));
	}

	@Override
	public boolean hasMetadata(String metadataKey)
	{
		return Optional.ofNullable(this.func(player -> player.hasMetadata(metadataKey))).orElse(Boolean.FALSE);
	}

	@Override
	public void removeMetadata(String metadataKey, Plugin owningPlugin)
	{
		this.consume(player -> player.removeMetadata(metadataKey, owningPlugin));
	}

	@Override
	public boolean isLeashed()
	{
		return this.func(Player::isLeashed);
	}

	@Override
	public void sendMessage(String message)
	{
		this.consume(player -> player.sendMessage(message));
	}

	@Override
	public void sendMessage(String[] messages)
	{
		this.consume(player -> player.sendMessage(messages));
	}

	@Override
	public boolean isPermissionSet(String name)
	{
		return this.func(player -> player.isPermissionSet(name));
	}

	@Override
	public Entity getLeashHolder() throws IllegalStateException
	{
		return this.func(Player::getLeashHolder);
	}

	@Override
	public boolean isPermissionSet(Permission perm)
	{
		return this.func(player -> player.isPermissionSet(perm));
	}

	@Override
	public boolean hasPermission(String name)
	{
		return Optional.ofNullable(this.func(player -> player.hasPermission(name))).orElse(Boolean.FALSE);
	}

	@Override
	public boolean hasPermission(Permission perm)
	{
		return Optional.ofNullable(this.func(player -> player.hasPermission(perm))).orElse(Boolean.FALSE);
	}

	@Override
	public boolean setLeashHolder(Entity holder)
	{
		return this.func(player -> player.setLeashHolder(holder));
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value)
	{
		return this.func(player -> player.addAttachment(plugin, name, value));
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin)
	{
		return this.func(player -> player.addAttachment(plugin));
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks)
	{
		return this.func(player -> player.addAttachment(plugin, name, value, ticks));
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks)
	{
		return this.func(player -> player.addAttachment(plugin, ticks));
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment)
	{
		this.consume(player -> player.removeAttachment(attachment));
	}

	@Override
	public void setVelocity(Vector velocity)
	{
		this.consume(player -> player.setVelocity(velocity));
	}

	@Override
	public void recalculatePermissions()
	{
		this.consume(Player::recalculatePermissions);
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions()
	{
		return this.func(Player::getEffectivePermissions);
	}

	@Override
	public boolean isOp()
	{
		return this.func(Player::isOp);
	}

	@Override
	public Vector getVelocity()
	{
		return this.func(Player::getVelocity);
	}

	@Override
	public void setOp(boolean value)
	{
		this.consume(player -> player.setOp(value));
	}

	@Override
	public void damage(double amount)
	{
		this.consume(player -> player.damage(amount));
	}

	@Override
	public void damage(double amount, Entity source)
	{
		this.consume(player -> player.damage(amount, source));
	}

	@Override
	public World getWorld()
	{
		return this.func(Player::getWorld);
	}

	@Override
	public double getHealth()
	{
		return this.func(Player::getHealth);
	}

	@Override
	public void setHealth(double health)
	{
		this.consume(player -> player.setHealth(health));
	}

	@Override
	public double getMaxHealth()
	{
		return this.func(Player::getMaxHealth);
	}

	@Override
	public boolean teleport(Location location)
	{
		return this.func(player -> player.teleport(location));
	}

	@Override
	public void setMaxHealth(double health)
	{
		this.consume(player -> player.setMaxHealth(health));
	}

	@Override
	public void resetMaxHealth()
	{
		this.consume(Player::resetMaxHealth);
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> projectile)
	{
		return this.func(player -> player.launchProjectile(projectile));
	}

	@Override
	public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause)
	{
		return this.func(player -> player.teleport(location, cause));
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> projectile, Vector velocity)
	{
		return this.func(player -> player.launchProjectile(projectile, velocity));
	}

	@Override
	public boolean isConversing()
	{
		return this.func(Player::isConversing);
	}

	@Override
	public void acceptConversationInput(String input)
	{
		this.consume(player -> player.acceptConversationInput(input));
	}

	@Override
	public boolean teleport(Entity destination)
	{
		return this.func(player -> player.teleport(destination));
	}

	@Override
	public boolean beginConversation(Conversation conversation)
	{
		return this.func(player -> player.beginConversation(conversation));
	}

	@Override
	public void abandonConversation(Conversation conversation)
	{
		this.consume(player -> player.abandonConversation(conversation));
	}

	@Override
	public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details)
	{
		this.consume(player -> player.abandonConversation(conversation, details));
	}

	@Override
	public boolean teleport(Entity destination, PlayerTeleportEvent.TeleportCause cause)
	{
		return this.func(player -> player.teleport(destination, cause));
	}

	@Override
	public boolean isOnline()
	{
		return Optional.ofNullable(this.func(Player::isOnline)).orElse(Boolean.FALSE);
	}

	@Override
	public boolean isBanned()
	{
		return this.func(Player::isBanned);
	}

	@Deprecated
	@Override
	public void setBanned(boolean banned)
	{
		this.consume(player -> player.setBanned(banned));
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z)
	{
		return this.func(player -> player.getNearbyEntities(x, y, z));
	}

	@Override
	public boolean isWhitelisted()
	{
		return this.func(Player::isWhitelisted);
	}

	@Override
	public void setWhitelisted(boolean value)
	{
		this.consume(player -> player.setWhitelisted(value));
	}

	@Override
	public long getFirstPlayed()
	{
		return this.func(Player::getFirstPlayed);
	}

	@Override
	public int getEntityId()
	{
		return this.func(Player::getEntityId);
	}

	@Override
	public long getLastPlayed()
	{
		return this.func(Player::getLastPlayed);
	}

	@Override
	public boolean hasPlayedBefore()
	{
		return this.func(Player::hasPlayedBefore);
	}

	@Override
	public Map<String, Object> serialize()
	{
		return this.func(Player::serialize);
	}

	@Override
	public int getFireTicks()
	{
		return this.func(Player::getFireTicks);
	}

	@Override
	public void sendPluginMessage(Plugin source, String channel, byte[] message)
	{
		this.consume(player -> player.sendPluginMessage(source, channel, message));
	}

	@Override
	public Set<String> getListeningPluginChannels()
	{
		return this.func(Player::getListeningPluginChannels);
	}

	@Override
	public String getDisplayName()
	{
		return this.func(Player::getDisplayName);
	}

	@Override
	public int getMaxFireTicks()
	{
		return this.func(Player::getMaxFireTicks);
	}

	@Override
	public void setDisplayName(String name)
	{
		this.consume(player -> player.setDisplayName(name));
	}

	@Override
	public String getPlayerListName()
	{
		return this.func(Player::getPlayerListName);
	}

	@Override
	public void setPlayerListName(String name)
	{
		this.consume(player -> player.setPlayerListName(name));
	}

	@Override
	public void setFireTicks(int ticks)
	{
		this.consume(player -> player.setFireTicks(ticks));
	}

	@Override
	@Deprecated
	public Player get()
	{
		return this.getPlayer();
	}

	@Override
	public int hashCode()
	{
		if (this.hashcode == 0)
		{
			this.hashcode = this.uuid.hashCode();
		}

		return this.hashcode;
	}

	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof Player)) return false;

		Player other = (Player) object;

		return this.uuid.equals(other.getUniqueId());
	}

	@Override
	public void remove()
	{
		this.consume(Player::remove);
	}


	@Override
	public boolean isDead()
	{
		return this.func(Player::isDead);
	}


	@Override
	public boolean isValid()
	{
		return Optional.ofNullable(this.func(Player::isValid)).orElse(Boolean.FALSE);
	}


	@Override
	public Server getServer()
	{
		return this.func(Player::getServer);
	}


	@Override
	public Entity getPassenger()
	{
		return this.func(Player::getPassenger);
	}


	@Override
	public boolean setPassenger(Entity passenger)
	{
		return this.func(player -> player.setPassenger(passenger));
	}


	@Override
	public boolean isEmpty()
	{
		return this.func(Player::isEmpty);
	}


	@Override
	public boolean eject()
	{
		return this.func(Player::eject);
	}


	@Override
	public float getFallDistance()
	{
		return this.func(Player::getFallDistance);
	}


	@Override
	public void setFallDistance(float distance)
	{
		this.consume(player -> player.setFallDistance(distance));
	}


	@Override
	public void setLastDamageCause(EntityDamageEvent event)
	{
		this.consume(player -> player.setLastDamageCause(event));
	}


	@Override
	public EntityDamageEvent getLastDamageCause()
	{
		return this.func(Player::getLastDamageCause);
	}


	@Override
	public int getTicksLived()
	{
		return this.func(Player::getTicksLived);
	}


	@Override
	public void setTicksLived(int value)
	{
		this.consume(player -> player.setTicksLived(value));
	}


	@Override
	public void playEffect(EntityEffect type)
	{
		this.consume(player -> player.playEffect(type));
	}


	@Override
	public EntityType getType()
	{
		return this.func(Player::getType);
	}


	@Override
	public boolean isInsideVehicle()
	{
		return this.func(Player::isInsideVehicle);
	}


	@Override
	public boolean leaveVehicle()
	{
		return this.func(Player::leaveVehicle);
	}


	@Override
	public Entity getVehicle()
	{
		return this.func(Player::getVehicle);
	}


	@Override
	public void setCustomName(String name)
	{
		this.consume(player -> player.setCustomName(name));
	}


	@Override
	public String getCustomName()
	{
		return this.func(Player::getCustomName);
	}


	@Override
	public void setCustomNameVisible(boolean flag)
	{
		this.consume(player -> player.setCustomNameVisible(flag));
	}


	@Override
	public boolean isCustomNameVisible()
	{
		return this.func(Player::isCustomNameVisible);
	}


	@Override
	public boolean isNotifyNoPerms()
	{
		return this.func(Player::isNotifyNoPerms);
	}

	@Override
	public void setNotifyNoPerms(boolean notify)
	{
		this.consume(player -> player.setNotifyNoPerms(notify));
	}


	@Override
	public void setCompassTarget(Location loc)
	{
		this.consume(player -> player.setCompassTarget(loc));
	}

	@Override
	public Location getCompassTarget()
	{
		return this.func(Player::getCompassTarget);
	}

	@Override
	public InetSocketAddress getAddress()
	{
		return this.func(Player::getAddress);
	}

	@Override
	public void sendRawMessage(String message)
	{
		this.consume(player -> player.sendRawMessage(message));
	}

	@Override
	public void kickPlayer(String message)
	{
		this.consume(player -> player.kickPlayer(message));
	}

	@Override
	public void chat(String message)
	{
		this.consume(player -> player.chat(message));
	}

	@Override
	public boolean performCommand(String command)
	{
		return this.func(player -> player.performCommand(command));
	}

	@Override
	public boolean isSneaking()
	{
		return Optional.ofNullable(this.func(Player::isSneaking)).orElse(Boolean.FALSE);
	}

	@Override
	public void setSneaking(boolean sneak)
	{
		this.consume(player -> player.setSneaking(sneak));
	}

	@Override
	public boolean isSprinting()
	{
		return Optional.ofNullable(this.func(Player::isSprinting)).orElse(Boolean.FALSE);
	}

	@Override
	public void setSprinting(boolean sprinting)
	{
		this.consume(player -> player.setSprinting(sprinting));
	}

	@Override
	public void saveData()
	{
		this.consume(Player::saveData);
	}

	@Override
	public void loadData()
	{
		this.consume(Player::loadData);
	}

	@Override
	public void setSleepingIgnored(boolean isSleeping)
	{
		this.consume(player -> player.setSleepingIgnored(isSleeping));
	}

	@Override
	public boolean isSleepingIgnored()
	{
		return Optional.ofNullable(this.func(Player::isSleepingIgnored)).orElse(Boolean.FALSE);
	}

	@Deprecated
	@Override
	public void playNote(Location loc, byte instrument, byte note)
	{
		this.consume(player -> player.playNote(loc, instrument, note));
	}

	@Override
	public void playNote(Location loc, Instrument instrument, Note note)
	{
		this.consume(player -> player.playNote(loc, instrument, note));
	}

	@Override
	public void playSound(Location location, Sound sound, float volume, float pitch)
	{
		this.consume(player -> player.playSound(location, sound, volume, pitch));
	}

	@Override
	public void playSound(Location location, String sound, float volume, float pitch)
	{
		this.consume(player -> player.playSound(location, sound, volume, pitch));
	}

	@Deprecated
	@Override
	public void playEffect(Location loc, Effect effect, int data)
	{
		this.consume(player -> player.playEffect(loc, effect, data));
	}

	@Override
	public <T> void playEffect(Location loc, Effect effect, T data)
	{
		this.consume(player -> player.playEffect(loc, effect, data));
	}

	@Deprecated
	@Override
	public void sendBlockChange(Location loc, Material material, byte data)
	{
		this.consume(player -> player.sendBlockChange(loc, material, data));
	}

	@Deprecated
	@Override
	public boolean sendChunkChange(Location loc, int sx, int sy, int sz, byte[] data)
	{
		return this.func(player -> player.sendChunkChange(loc, sx, sy, sz, data));
	}

	@Deprecated
	@Override
	public void sendBlockChange(Location loc, int material, byte data)
	{
		this.consume(player -> player.sendBlockChange(loc, material, data));
	}

	@Override
	public void sendSignChange(Location loc, String[] lines) throws IllegalArgumentException
	{
		this.consume(player -> player.sendSignChange(loc, lines));
	}

	@Override
	public void sendMap(MapView map)
	{
		this.consume(player -> player.sendMap(map));
	}

	@Override
	public void updateInventory()
	{
		this.consume(Player::updateInventory);
	}

	@Override
	public void awardAchievement(Achievement achievement)
	{
		this.consume(player -> player.awardAchievement(achievement));
	}

	@Override
	public void removeAchievement(Achievement achievement)
	{
		this.consume(player -> player.removeAchievement(achievement));
	}

	@Override
	public boolean hasAchievement(Achievement achievement)
	{
		return this.func(player -> player.hasAchievement(achievement));
	}

	@Override
	public void incrementStatistic(Statistic statistic) throws IllegalArgumentException
	{
		this.consume(player -> player.incrementStatistic(statistic));
	}

	@Override
	public void decrementStatistic(Statistic statistic) throws IllegalArgumentException
	{
		this.consume(player -> player.decrementStatistic(statistic));
	}

	@Override
	public void incrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException
	{
		this.consume(player -> player.incrementStatistic(statistic, amount));
	}

	@Override
	public void decrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException
	{
		this.consume(player -> player.decrementStatistic(statistic, amount));
	}

	@Override
	public void setStatistic(Statistic statistic, int newValue) throws IllegalArgumentException
	{
		this.consume(player -> player.setStatistic(statistic, newValue));
	}

	@Override
	public int getStatistic(Statistic statistic) throws IllegalArgumentException
	{
		return this.func(player -> player.getStatistic(statistic));
	}

	@Override
	public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException
	{
		this.consume(player -> player.incrementStatistic(statistic, material));
	}

	@Override
	public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException
	{
		this.consume(player -> player.decrementStatistic(statistic, material));
	}

	@Override
	public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException
	{
		return this.func(player -> player.getStatistic(statistic, material));
	}

	@Override
	public void incrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException
	{
		this.consume(player -> player.incrementStatistic(statistic, material, amount));
	}

	@Override
	public void decrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException
	{
		this.consume(player -> player.decrementStatistic(statistic, material, amount));
	}

	@Override
	public void setStatistic(Statistic statistic, Material material, int newValue) throws IllegalArgumentException
	{
		this.consume(player -> player.setStatistic(statistic, material, newValue));
	}

	@Override
	public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException
	{
		this.consume(player -> player.incrementStatistic(statistic, entityType));
	}

	@Override
	public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException
	{
		this.consume(player -> player.decrementStatistic(statistic, entityType));
	}

	@Override
	public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException
	{
		return this.func(player -> player.getStatistic(statistic, entityType));
	}

	@Override
	public void incrementStatistic(Statistic statistic, EntityType entityType, int amount) throws IllegalArgumentException
	{
		this.consume(player -> player.incrementStatistic(statistic, entityType, amount));
	}

	@Override
	public void decrementStatistic(Statistic statistic, EntityType entityType, int amount)
	{
		this.consume(player -> player.decrementStatistic(statistic, entityType, amount));
	}

	@Override
	public void setStatistic(Statistic statistic, EntityType entityType, int newValue)
	{
		this.consume(player -> player.setStatistic(statistic, entityType, newValue));
	}

	@Override
	public void setPlayerTime(long time, boolean relative)
	{
		this.consume(player -> player.setPlayerTime(time, relative));
	}

	@Override
	public long getPlayerTime()
	{
		return this.func(Player::getPlayerTime);
	}

	@Override
	public long getPlayerTimeOffset()
	{
		return this.func(Player::getPlayerTimeOffset);
	}

	@Override
	public boolean isPlayerTimeRelative()
	{
		return Optional.ofNullable(this.func(Player::isPlayerTimeRelative)).orElse(Boolean.FALSE);
	}

	@Override
	public void resetPlayerTime()
	{
		this.consume(Player::resetPlayerTime);
	}

	@Override
	public void setPlayerWeather(WeatherType type)
	{
		this.consume(player -> player.setPlayerWeather(type));
	}

	@Override
	public WeatherType getPlayerWeather()
	{
		return this.func(Player::getPlayerWeather);
	}

	@Override
	public void resetPlayerWeather()
	{
		this.consume(Player::resetPlayerWeather);
	}

	@Override
	public void giveExp(int amount)
	{
		this.consume(player -> player.giveExp(amount));
	}

	@Override
	public void giveExpLevels(int amount)
	{
		this.consume(player -> player.giveExpLevels(amount));
	}

	@Override
	public float getExp()
	{
		return this.func(Player::getExp);
	}

	@Override
	public void setExp(float exp)
	{
		this.consume(player -> player.setExp(exp));
	}

	@Override
	public int getLevel()
	{
		return this.func(Player::getLevel);
	}

	@Override
	public void setLevel(int level)
	{
		this.consume(player -> player.setLevel(level));
	}

	@Override
	public int getTotalExperience()
	{
		return this.func(Player::getTotalExperience);
	}

	@Override
	public void setTotalExperience(int exp)
	{
		this.consume(player -> player.setTotalExperience(exp));
	}

	@Override
	public float getExhaustion()
	{
		return this.func(Player::getExhaustion);
	}

	@Override
	public void setExhaustion(float value)
	{
		this.consume(player -> player.setExhaustion(value));
	}

	@Override
	public float getSaturation()
	{
		return this.func(Player::getSaturation);
	}

	@Override
	public void setSaturation(float value)
	{
		this.consume(player -> player.setSaturation(value));
	}

	@Override
	public int getFoodLevel()
	{
		return this.func(Player::getFoodLevel);
	}

	@Override
	public void setFoodLevel(int value)
	{
		this.consume(player -> player.setFoodLevel(value));
	}

	@Override
	public Location getBedSpawnLocation()
	{
		return this.func(Player::getBedSpawnLocation);
	}

	@Override
	public void setBedSpawnLocation(Location location)
	{
		this.consume(player -> player.setBedSpawnLocation(location));
	}

	@Override
	public void setBedSpawnLocation(Location location, boolean force)
	{
		this.consume(player -> player.setBedSpawnLocation(location, force));
	}

	@Override
	public boolean getAllowFlight()
	{
		return Optional.ofNullable(this.func(Player::getAllowFlight)).orElse(Boolean.FALSE);
	}

	@Override
	public void setAllowFlight(boolean flight)
	{
		this.consume(player -> player.setAllowFlight(flight));
	}

	@Override
	public void hidePlayer(Player player)
	{
		this.consume(plr -> plr.hidePlayer(player));
	}

	@Override
	public void showPlayer(Player player)
	{
		this.consume(plr -> plr.showPlayer(player));
	}

	@Override
	public boolean canSee(Player player)
	{
		return this.func(plr -> plr.canSee(player));
	}

	@Deprecated
	@Override
	public boolean isOnGround()
	{
		return this.func(Player::isOnGround);
	}

	@Override
	public boolean isFlying()
	{
		return Optional.ofNullable(this.func(Player::isFlying)).orElse(Boolean.FALSE);
	}

	@Override
	public void setFlying(boolean value)
	{
		this.consume(player -> player.setFlying(value));
	}

	@Override
	public void setFlySpeed(float value) throws IllegalArgumentException
	{
		this.consume(player -> player.setFlySpeed(value));
	}

	@Override
	public void setWalkSpeed(float value) throws IllegalArgumentException
	{
		this.consume(player -> player.setWalkSpeed(value));
	}

	@Override
	public float getFlySpeed()
	{
		return this.func(Player::getFlySpeed);
	}

	@Override
	public float getWalkSpeed()
	{
		return this.func(Player::getWalkSpeed);
	}

	@Deprecated
	@Override
	public void setTexturePack(String url)
	{
		this.consume(player -> player.setTexturePack(url));
	}

	@Override
	public void setResourcePack(String url)
	{
		this.consume(player -> player.setResourcePack(url));
	}

	@Override
	public Scoreboard getScoreboard()
	{
		return this.func(Player::getScoreboard);
	}

	@Override
	public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException
	{
		this.consume(player -> player.setScoreboard(scoreboard));
	}

	@Override
	public boolean isHealthScaled()
	{
		return Optional.ofNullable(this.func(Player::isHealthScaled)).orElse(Boolean.FALSE);
	}

	@Override
	public void setHealthScaled(boolean scale)
	{
		this.consume(player -> player.setHealthScaled(scale));
	}

	@Override
	public void setHealthScale(double scale) throws IllegalArgumentException
	{
		this.consume(player -> player.setHealthScale(scale));
	}

	@Override
	public double getHealthScale()
	{
		return this.func(Player::getHealthScale);
	}

	@Override
	public Entity getSpectatorTarget()
	{
		return this.func(Player::getSpectatorTarget);
	}

	@Override
	public void setSpectatorTarget(Entity entity)
	{
		this.consume(player -> player.setSpectatorTarget(entity));
	}

	@Deprecated
	@Override
	public void sendTitle(String title, String subtitle)
	{
		this.consume(player -> player.sendTitle(title, subtitle));
	}

	@Deprecated
	@Override
	public void resetTitle()
	{
		this.consume(Player::resetTitle);
	}

	@Override
	public void sendMessage(BaseComponent baseComponent)
	{
		this.consume(player -> player.sendMessage(baseComponent));
	}

	@Override
	public void sendMessage(BaseComponent... baseComponents)
	{
		this.consume(player -> player.sendMessage(baseComponents));
	}

	@Override
	public void setPlayerListHeaderFooter(BaseComponent[] baseComponents, BaseComponent[] baseComponents1)
	{
		this.consume(player -> player.setPlayerListHeaderFooter(baseComponents, baseComponents1));
	}

	@Override
	public void setPlayerListHeaderFooter(BaseComponent baseComponent, BaseComponent baseComponent1)
	{
		this.consume(player -> player.setPlayerListHeaderFooter(baseComponent, baseComponent1));
	}

	@Override
	public void setTitleTimes(int i, int i1, int i2)
	{
		this.consume(player -> player.setTitleTimes(i, i1, i2));
	}

	@Override
	public void setSubtitle(BaseComponent[] baseComponents)
	{
		this.consume(player -> player.setSubtitle(baseComponents));
	}

	@Override
	public void setSubtitle(BaseComponent baseComponent)
	{
		this.consume(player -> player.setSubtitle(baseComponent));
	}

	@Override
	public void showTitle(BaseComponent[] baseComponents)
	{
		this.consume(player -> player.showTitle(baseComponents));
	}

	@Override
	public void showTitle(BaseComponent baseComponent)
	{
		this.consume(player -> player.showTitle(baseComponent));
	}

	@Override
	public void showTitle(BaseComponent[] baseComponents, BaseComponent[] baseComponents1, int i, int i1, int i2)
	{
		this.consume(player -> player.showTitle(baseComponents, baseComponents1, i, i1, i2));
	}

	@Override
	public void showTitle(BaseComponent baseComponent, BaseComponent baseComponent1, int i, int i1, int i2)
	{
		this.consume(player -> player.showTitle(baseComponent, baseComponent1, i, i1, i2));
	}

	@Override
	public void sendTitle(Title title)
	{
		this.consume(player -> player.sendTitle(title));
	}

	@Override
	public void updateTitle(Title title)
	{
		this.consume(player -> player.sendTitle(title));
	}

	@Override
	public void hideTitle()
	{
		this.consume(Player::hideTitle);
	}

	@Override
	public void setResourcePack(String s, String s1)
	{
		this.consume(player -> player.setResourcePack(s, s1));
	}

	@Override
	public PlayerResourcePackStatusEvent.Status getResourcePackStatus()
	{
		return this.func(Player::getResourcePackStatus);
	}

	@Override
	public String getResourcePackHash()
	{
		return this.func(Player::getResourcePackHash);
	}

	@Override
	public boolean hasResourcePack()
	{
		return this.func(Player::hasResourcePack);
	}

	@Override
	public int getArrowsStuck()
	{
		return this.func(Player::getArrowsStuck);
	}

	@Override
	public void setArrowsStuck(int i)
	{
		this.consume(player -> player.setArrowsStuck(i));
	}

	@Override
	public boolean useCustomAi()
	{
		return this.func(Player::useCustomAi);
	}

	@Override
	public void setUseCustomAi(boolean b)
	{
		this.consume(player -> player.setUseCustomAi(b));
	}

	@Override
	public Controller getCustomAiController()
	{
		return this.func(Player::getCustomAiController);
	}

	@Override
	public Spigot spigot()
	{
		return Optional.ofNullable(this.func(Player::spigot)).orElse(emptySpigot());
	}


}
