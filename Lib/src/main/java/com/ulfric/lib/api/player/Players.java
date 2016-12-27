package com.ulfric.lib.api.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.java.StringUtils;
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
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Players implements Player, Named {

	public static final Players AGENT = new Players("AGENT", ImmutableList.of()) {
		@Override
		public boolean hasPermission(String node)
		{
			return true;
		}

		@Override
		public boolean isOp()
		{
			return true;
		}

		@Override
		public boolean canSee(Player player)
		{
			return true;
		}
	};
	private final String name;
	private final Collection<Player> players;

	protected Players(Collection<Player> players)
	{
		this(StringUtils.mergeNicely(players.stream().map(Player::getName).collect(Collectors.toList())), players);
	}

	protected Players(String name, Collection<Player> players)
	{
		this.name = name;
		this.players = new ArrayList<>(players);
	}

	public static Player of(CommandSender sender)
	{
		Assert.notNull(sender);

		if (sender instanceof Player)
		{
			return (Player) sender;
		}

		else if (sender instanceof ConsoleCommandSender)
		{
			return AGENT;
		}

		return new NonPlayer(sender);
	}

	private void consume(Consumer<Player> consumer)
	{
		this.players.forEach(consumer);
	}

	private boolean consumeMultiResult(Function<Player, Boolean> function)
	{
		boolean flag = true;

		for (Player player : this.players)
		{
			if (function.apply(player)) continue;

			flag = false;
		}

		return flag;
	}

	public boolean getAnyResult(Function<Player, Boolean> function)
	{
		for (Player player : this.players)
		{
			if (!function.apply(player)) continue;

			return true;
		}

		return false;
	}

	private boolean getMultiResult(Function<Player, Boolean> function)
	{
		for (Player player : this.players)
		{
			if (function.apply(player)) continue;

			return false;
		}

		return true;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public PlayerInventory getInventory()
	{
		return null; // TODO empty inventory
	}

	@Override
	public Inventory getEnderChest()
	{
		return null; // TODO empty inventory
	}

	@Override
	public boolean setWindowProperty(InventoryView.Property property, int value)
	{
		return this.consumeMultiResult(player -> player.setWindowProperty(property, value));
	}

	@Override
	public InventoryView getOpenInventory()
	{
		return null; // TODO empty inventory
	}

	@Override
	public InventoryView openInventory(Inventory inventory)
	{
		this.consume(player -> player.openInventory(inventory));

		return null; // TODO empty inventory
	}

	@Override
	public InventoryView openWorkbench(Location location, boolean flag)
	{
		this.consume(player -> player.openWorkbench(location, flag));

		return null;
	}

	@Override
	public InventoryView openEnchanting(Location location, boolean flag)
	{
		this.consume(player -> player.openEnchanting(location, flag));

		return null; // TODO empty inventory
	}

	@Override
	public void openInventory(InventoryView inventory)
	{
		this.consume(player -> player.openInventory(inventory));
	}

	@Override
	public void closeInventory()
	{
		this.consume(Player::closeInventory);
	}

	@Override
	public ItemStack getItemInHand()
	{
		return ItemUtils.blank();
	}

	@Override
	public void setItemInHand(ItemStack item)
	{
		this.consume(player -> player.setItemInHand(item));
	}

	@Override
	public ItemStack getItemOnCursor()
	{
		return ItemUtils.blank();
	}

	@Override
	public void setItemOnCursor(ItemStack item)
	{
		this.consume(player -> player.setItemOnCursor(item));
	}

	@Override
	public boolean isSleeping()
	{
		return this.getMultiResult(Player::isSleeping);
	}

	@Override
	public int getSleepTicks()
	{
		return (int) this.players.stream().mapToInt(Player::getSleepTicks).average().orElse(0);
	}

	@Override
	public GameMode getGameMode()
	{
		return Bukkit.getDefaultGameMode();
	}

	@Override
	public void setGameMode(GameMode gamemode)
	{
		this.consume(player -> player.setGameMode(gamemode));
	}

	@Override
	public boolean isBlocking()
	{
		return this.getMultiResult(Player::isBlocking);
	}

	@Override
	public int getExpToLevel()
	{
		return (int) this.players.stream().mapToInt(Player::getExpToLevel).average().orElse(0);
	}

	@Override
	public double getEyeHeight()
	{
		return this.getEyeHeight(false);
	}

	@Override
	public double getEyeHeight(boolean flag)
	{
		return !flag || this.isSneaking() ? 1.54 : 1.62;
	}

	@Override
	public Location getEyeLocation()
	{
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Block> getLineOfSight(HashSet<Byte> set, int distance)
	{
		ImmutableList.Builder<Block> builder = ImmutableList.builder();

		this.consume(player -> builder.addAll(player.getLineOfSight(set, distance)));

		return builder.build();
	}

	@Override
	public List<Block> getLineOfSight(Set<Material> set, int distance)
	{
		ImmutableList.Builder<Block> builder = ImmutableList.builder();

		this.consume(player -> builder.addAll(player.getLineOfSight(set, distance)));

		return builder.build();
	}

	@Override
	public Block getTargetBlock(HashSet<Byte> set, int distance)
	{
		return null;
	}

	@Override
	public Block getTargetBlock(Set<Material> set, int distance)
	{
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> set, int distance)
	{
		ImmutableList.Builder<Block> builder = ImmutableList.builder();

		this.consume(player -> builder.addAll(player.getLastTwoTargetBlocks(set, distance)));

		return builder.build();
	}

	@Override
	public boolean getCanPickupItems()
	{
		return this.getMultiResult(Player::getCanPickupItems);
	}

	@Override
	public List<Block> getLastTwoTargetBlocks(Set<Material> set, int distance)
	{
		ImmutableList.Builder<Block> builder = ImmutableList.builder();

		this.consume(player -> builder.addAll(player.getLastTwoTargetBlocks(set, distance)));

		return builder.build();
	}

	@SuppressWarnings("deprecation")
	@Override
	public Egg throwEgg()
	{
		this.consume(Player::throwEgg);

		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Snowball throwSnowball()
	{
		this.consume(Player::throwSnowball);

		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Arrow shootArrow()
	{
		this.consume(Player::shootArrow);

		return null;
	}

	@Override
	public int getRemainingAir()
	{
		return (int) this.players.stream().mapToInt(Player::getRemainingAir).average().orElse(0);
	}

	@Override
	public void setRemainingAir(int air)
	{
		this.consume(player -> player.setRemainingAir(air));
	}

	@Override
	public int getMaximumAir()
	{
		return (int) this.players.stream().mapToInt(Player::getMaximumAir).average().orElse(0);
	}

	@Override
	public void setMaximumAir(int maxAir)
	{
		this.consume(player -> player.setMaxHealth(maxAir));
	}

	@Override
	public int getMaximumNoDamageTicks()
	{
		return (int) this.players.stream().mapToInt(Player::getMaximumNoDamageTicks).average().orElse(0);
	}

	@Override
	public void setMaximumNoDamageTicks(int ticks)
	{
		this.consume(player -> player.setMaximumNoDamageTicks(ticks));
	}

	@Override
	public Entity getLeashHolder() throws IllegalStateException
	{
		return null;
	}

	@Override
	public double getLastDamage()
	{
		return this.players.stream().mapToDouble(Player::getLastDamage).average().orElse(0);
	}

	@Override
	public void setLastDamage(double damage)
	{
		this.consume(player -> player.setLastDamage(damage));
	}

	@Override
	public int getNoDamageTicks()
	{
		return (int) this.players.stream().mapToInt(Player::getNoDamageTicks).average().orElse(0);
	}

	@Override
	public void setNoDamageTicks(int ticks)
	{
		this.consume(player -> player.setNoDamageTicks(ticks));
	}

	@Override
	public Player getKiller()
	{
		return null;
	}

	@Override
	public boolean addPotionEffect(PotionEffect effect)
	{
		return this.consumeMultiResult(player -> player.addPotionEffect(effect));
	}

	@Override
	public boolean addPotionEffect(PotionEffect effect, boolean flag)
	{
		return this.consumeMultiResult(player -> player.addPotionEffect(effect, flag));
	}

	@Override
	public boolean addPotionEffects(Collection<PotionEffect> effects)
	{
		return this.consumeMultiResult(player -> player.addPotionEffects(effects));
	}

	@Override
	public boolean hasPotionEffect(PotionEffectType effectType)
	{
		return this.getAnyResult(player -> player.hasPotionEffect(effectType));
	}

	@Override
	public void removePotionEffect(PotionEffectType effectType)
	{
		this.consume(player -> player.removePotionEffect(effectType));
	}

	@Override
	public Collection<PotionEffect> getActivePotionEffects()
	{
		ImmutableList.Builder<PotionEffect> builder = ImmutableList.builder();
		for (Player player : this.players)
		{
			builder.addAll(player.getActivePotionEffects());
		}

		return builder.build();
	}

	@Override
	public boolean hasLineOfSight(Entity entity)
	{
		return this.getMultiResult(player -> player.hasLineOfSight(entity));
	}

	@Override
	public boolean getRemoveWhenFarAway()
	{
		return this.getMultiResult(Player::getRemoveWhenFarAway);
	}

	@Override
	public void setRemoveWhenFarAway(boolean remove)
	{
		this.consume(player -> player.setRemoveWhenFarAway(remove));
	}

	@Override
	public boolean isLeashed()
	{
		return this.getAnyResult(Player::isLeashed);
	}

	@Override
	public EntityEquipment getEquipment()
	{
		return null; // TODO blank entity equipment
	}

	@Override
	public Location getLocation()
	{
		return Hooks.ESS.getSpawnpoint().getLocation();
	}

	@Override
	public Location getLocation(Location location)
	{
		return Hooks.ESS.getSpawnpoint().getLocation();
	}

	@Override
	public void setMetadata(String path, MetadataValue value)
	{
		this.consume(player -> player.setMetadata(path, value));
	}

	@Override
	public void setCanPickupItems(boolean pickup)
	{
		this.consume(player -> player.setCanPickupItems(pickup));
	}

	@Override
	public List<MetadataValue> getMetadata(String path)
	{
		ImmutableList.Builder<MetadataValue> values = ImmutableList.builder();

		this.consume(player -> values.addAll(player.getMetadata(path)));

		return values.build();
	}

	@Override
	public boolean hasMetadata(String path)
	{
		return this.getAnyResult(player -> player.hasMetadata(path));
	}

	@Override
	public void removeMetadata(String path, Plugin plugin)
	{
		this.consume(player -> player.removeMetadata(path, plugin));
	}

	@Override
	public void sendMessage(String message)
	{
		this.consume(player -> player.sendMessage(message));
	}

	@Override
	public boolean setLeashHolder(Entity entity)
	{
		return this.getMultiResult(player -> player.setLeashHolder(entity));
	}

	@Override
	public void sendMessage(String[] messages)
	{
		this.consume(player -> player.sendMessage(messages));
	}

	@Override
	public boolean isPermissionSet(String permission)
	{
		return this.getAnyResult(player -> player.isPermissionSet(permission));
	}

	@Override
	public boolean isPermissionSet(Permission permission)
	{
		return this.getAnyResult(player -> player.isPermissionSet(permission));
	}

	@Override
	public boolean hasPermission(String permission)
	{
		return this.getAnyResult(player -> player.hasPermission(permission));
	}

	@Override
	public boolean hasPermission(Permission permission)
	{
		return this.getAnyResult(player -> player.hasPermission(permission));
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String string, boolean flag)
	{
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin)
	{
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3)
	{
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int time)
	{
		return null;
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment)
	{
		this.consume(player -> player.removeAttachment(attachment));
	}

	@Override
	public void recalculatePermissions()
	{
		this.consume(Player::recalculatePermissions);
	}

	@Override
	public boolean eject()
	{
		return this.consumeMultiResult(Player::eject);
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions()
	{
		return null;
	}

	@Override
	public boolean isOp()
	{
		return this.getAnyResult(Player::isOp);
	}

	@Override
	public void setOp(boolean op)
	{
		this.consume(player -> player.setOp(op));
	}

	@Override
	@Deprecated
	public String getCustomName()
	{
		return this.name;
	}

	@Override
	public void damage(double amount)
	{
		this.consume(player -> player.damage(amount));
	}

	@Override
	public void damage(double amount, Entity entity)
	{
		this.consume(player -> player.damage(amount, entity));
	}

	@Override
	public double getHealth()
	{
		return this.players.stream().mapToDouble(Player::getHealth).average().orElse(0);
	}

	@Override
	public int getEntityId()
	{
		return -1;
	}

	@Override
	public void setHealth(double health)
	{
		this.consume(player -> player.setHealth(health));
	}

	@Override
	public double getMaxHealth()
	{
		return this.players.stream().mapToDouble(Player::getMaxHealth).max().orElse(0);
	}

	@Override
	public void setMaxHealth(double health)
	{
		this.consume(player -> player.setMaxHealth(health));
	}

	@Override
	public float getFallDistance()
	{
		return (float) this.players.stream().mapToDouble(Player::getFallDistance).average().orElse(0);
	}

	@Override
	public void resetMaxHealth()
	{
		this.consume(Player::resetMaxHealth);
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> clazz)
	{
		this.consume(player -> player.launchProjectile(clazz));

		return null;
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> clazz, Vector vector)
	{
		this.consume(player -> player.launchProjectile(clazz, vector));

		return null;
	}

	@Override
	public int getFireTicks()
	{
		return (int) this.players.stream().mapToInt(Player::getFireTicks).average().orElse(0);
	}

	@Override
	public boolean isConversing()
	{
		return this.getAnyResult(Player::isConversing);
	}

	@Override
	public void acceptConversationInput(String input)
	{
		this.consume(player -> player.acceptConversationInput(input));
	}

	@Override
	public boolean beginConversation(Conversation convo)
	{
		return this.getMultiResult(player -> player.beginConversation(convo));
	}

	@Override
	public EntityDamageEvent getLastDamageCause()
	{
		return null;
	}

	@Override
	public void abandonConversation(Conversation convo)
	{
		this.consume(player -> player.abandonConversation(convo));
	}

	@Override
	public void abandonConversation(Conversation convo, ConversationAbandonedEvent event)
	{
		this.consume(player -> player.abandonConversation(convo, event));
	}

	@Override
	public boolean isOnline()
	{
		return this.getAnyResult(Player::isOnline);
	}

	@Override
	public boolean isBanned()
	{
		return this.getAnyResult(Player::isBanned);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setBanned(boolean ban)
	{
		this.consume(player -> player.setBanned(ban));
	}

	@Override
	public int getMaxFireTicks()
	{
		return (int) this.players.stream().mapToInt(Player::getMaxFireTicks).average().orElse(0);
	}

	@Override
	public boolean isWhitelisted()
	{
		return this.getAnyResult(Player::isWhitelisted);
	}

	@Override
	public void setWhitelisted(boolean whitelist)
	{
		this.consume(player -> player.setWhitelisted(whitelist));
	}

	@Override
	public Player getPlayer()
	{
		return this;
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z)
	{
		ImmutableList.Builder<Entity> builder = ImmutableList.builder();

		this.consume(player -> builder.addAll(player.getNearbyEntities(x, y, z)));

		return builder.build();
	}

	@Override
	public long getFirstPlayed()
	{
		return -1;
	}

	@Override
	public long getLastPlayed()
	{
		return -1;
	}

	@Override
	public boolean hasPlayedBefore()
	{
		return this.getAnyResult(Player::hasPlayedBefore);
	}

	@Override
	public Entity getPassenger()
	{
		return null;
	}

	@Override
	public Map<String, Object> serialize()
	{
		throw new UnsupportedOperationException("You cannot currently serialize a Players instance");
	}

	@Override
	public void sendPluginMessage(Plugin plugin, String message, byte[] arr)
	{
		this.consume(player -> player.sendPluginMessage(plugin, message, arr));
	}

	@Override
	public Set<String> getListeningPluginChannels()
	{
		ImmutableSet.Builder<String> builder = ImmutableSet.builder();

		this.consume(player -> builder.addAll(player.getListeningPluginChannels()));

		return builder.build();
	}

	@Override
	public Server getServer()
	{
		return Bukkit.getServer();
	}

	@Override
	public String getDisplayName()
	{
		return this.name;
	}

	@Override
	public void setDisplayName(String name)
	{
		this.consume(player -> player.setDisplayName(name));
	}

	@Override
	public String getPlayerListName()
	{
		return this.name;
	}

	@Override
	public int getTicksLived()
	{
		return this.players.stream().mapToInt(Player::getTicksLived).sum();
	}

	@Override
	public void setPlayerListName(String name)
	{
		this.consume(player -> player.setPlayerListName(name));
	}

	public static final class NonPlayer extends Players {
		private final WeakReference<CommandSender> sender;

		NonPlayer(CommandSender sender)
		{
			super(sender.getName(), ImmutableList.of());

			this.sender = new WeakReference<>(sender);
		}

		@Override
		public boolean hasPermission(String node)
		{
			CommandSender sender = this.sender.get();

			return sender != null && sender.hasPermission(node);

		}

		@Override
		public boolean isOp()
		{
			CommandSender sender = this.sender.get();

			return sender != null && sender.isOp();

		}
	}

	@Override
	public EntityType getType()
	{
		return EntityType.PLAYER;
	}


	@Override
	public UUID getUniqueId()
	{
		throw new UnsupportedOperationException("Cannot get the UUID of a Players instance");
	}


	@Override
	public Entity getVehicle()
	{
		return null;
	}


	@Override
	public Vector getVelocity()
	{
		return null;
	}


	@Override
	public World getWorld()
	{
		return null;
	}


	@Override
	public boolean isCustomNameVisible()
	{
		return this.getMultiResult(Player::isCustomNameVisible);
	}


	@Override
	public boolean isDead()
	{
		return this.getAnyResult(Player::isDead);
	}


	@Override
	public boolean isEmpty()
	{
		return this.getMultiResult(Player::isEmpty);
	}


	@Override
	public boolean isInsideVehicle()
	{
		return this.getAnyResult(Player::isInsideVehicle);
	}


	@Override
	public boolean isValid()
	{
		return this.getMultiResult(Player::isValid);
	}


	@Override
	public boolean leaveVehicle()
	{
		return this.consumeMultiResult(Player::leaveVehicle);
	}


	@Override
	public void playEffect(EntityEffect effect)
	{
		this.consume(player -> player.playEffect(effect));
	}


	@Override
	public void remove()
	{
		this.consume(Player::remove);
	}


	@Override
	public void setCustomName(String name)
	{
		this.consume(player -> player.setCustomName(name));
	}


	@Override
	public void setCustomNameVisible(boolean flag)
	{
		this.consume(player -> player.setCustomNameVisible(flag));
	}


	@Override
	public void setFallDistance(float distance)
	{
		this.consume(player -> player.setFallDistance(distance));
	}


	@Override
	public void setFireTicks(int ticks)
	{
		this.consume(player -> player.setFireTicks(ticks));
	}


	@Override
	public void setLastDamageCause(EntityDamageEvent event)
	{
		this.consume(player -> player.setLastDamageCause(event));
	}


	@Override
	public boolean setPassenger(Entity entity)
	{
		return this.consumeMultiResult(player -> player.setPassenger(entity));
	}


	@Override
	public void setTicksLived(int ticks)
	{
		this.consume(player -> player.setTicksLived(ticks));
	}


	@Override
	public void setVelocity(Vector vector)
	{
		this.consume(player -> player.setVelocity(vector));
	}


	@Override
	public boolean teleport(Location location)
	{
		return this.consumeMultiResult(player -> player.teleport(location));
	}


	@Override
	public boolean teleport(Entity entity)
	{
		return this.consumeMultiResult(player -> player.teleport(entity));
	}


	@Override
	public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause)
	{
		return this.consumeMultiResult(player -> player.teleport(location, cause));
	}


	@Override
	public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause cause)
	{
		return this.consumeMultiResult(player -> player.teleport(entity, cause));
	}


	@Override
	public void awardAchievement(Achievement ach)
	{
		this.consume(player -> player.awardAchievement(ach));
	}

	@Override
	public boolean canSee(Player player)
	{
		return this.getAnyResult(plr -> plr.canSee(player));
	}

	@Override
	public void chat(String message)
	{
		this.consume(player -> player.chat(message));
	}

	@Override
	public void decrementStatistic(Statistic stat) throws IllegalArgumentException
	{
		this.consume(player -> player.decrementStatistic(stat));
	}

	@Override
	public void decrementStatistic(Statistic stat, int amt) throws IllegalArgumentException
	{
		this.consume(player -> player.decrementStatistic(stat, amt));
	}

	@Override
	public void decrementStatistic(Statistic stat, Material mat) throws IllegalArgumentException
	{
		this.consume(player -> player.decrementStatistic(stat, mat));
	}

	@Override
	public void decrementStatistic(Statistic stat, EntityType ent) throws IllegalArgumentException
	{
		this.consume(player -> player.decrementStatistic(stat, ent));
	}

	@Override
	public void decrementStatistic(Statistic stat, Material mat, int amt) throws IllegalArgumentException
	{
		this.consume(player -> player.decrementStatistic(stat, mat, amt));
	}

	@Override
	public void decrementStatistic(Statistic stat, EntityType ent, int amt)
	{
		this.consume(player -> player.decrementStatistic(stat, ent, amt));
	}

	@Override
	public InetSocketAddress getAddress()
	{
		throw new UnsupportedOperationException("Cannot get the IP Address of a Players instance");
	}

	@Override
	public boolean getAllowFlight()
	{
		return this.getAnyResult(Player::getAllowFlight);
	}

	@Override
	public Location getBedSpawnLocation()
	{
		return null;
	}

	@Override
	public Location getCompassTarget()
	{
		return null;
	}


	@Override
	public float getExhaustion()
	{
		return (float) this.players.stream().mapToDouble(Player::getExhaustion).average().orElse(0);
	}

	@Override
	public float getExp()
	{
		return (float) this.players.stream().mapToDouble(Player::getExhaustion).sum();
	}

	@Override
	public float getFlySpeed()
	{
		return (float) this.players.stream().mapToDouble(Player::getFlySpeed).average().orElse(0);
	}

	@Override
	public int getFoodLevel()
	{
		return (int) this.players.stream().mapToInt(Player::getFoodLevel).average().orElse(0);
	}

	@Override
	public double getHealthScale()
	{
		return this.players.stream().mapToDouble(Player::getHealthScale).average().orElse(0);
	}

	@Override
	public int getLevel()
	{
		return (int) this.players.stream().mapToInt(Player::getLevel).average().orElse(0);
	}


	@Override
	public long getPlayerTime()
	{
		return (long) this.players.stream().mapToLong(Player::getPlayerTime).average().orElse(0);
	}

	@Override
	public long getPlayerTimeOffset()
	{
		return (long) this.players.stream().mapToLong(Player::getPlayerTimeOffset).average().orElse(0);
	}

	@Override
	public WeatherType getPlayerWeather()
	{
		return WeatherType.CLEAR;
	}

	@Override
	public float getSaturation()
	{
		return (float) this.players.stream().mapToDouble(Player::getSaturation).average().orElse(0);
	}

	@Override
	public Scoreboard getScoreboard()
	{
		return Bukkit.getScoreboardManager().getNewScoreboard();
	}

	@Override
	public Entity getSpectatorTarget()
	{
		return null;
	}

	@Override
	public int getStatistic(Statistic stat) throws IllegalArgumentException
	{
		return (int) this.players.stream().mapToInt(player -> player.getStatistic(stat)).average().orElse(0);
	}

	@Override
	public int getStatistic(Statistic stat, Material mat) throws IllegalArgumentException
	{
		return (int) this.players.stream().mapToInt(player -> player.getStatistic(stat, mat)).average().orElse(0);
	}

	@Override
	public int getStatistic(Statistic stat, EntityType ent) throws IllegalArgumentException
	{
		return (int) this.players.stream().mapToInt(player -> player.getStatistic(stat, ent)).average().orElse(0);
	}

	@Override
	public int getTotalExperience()
	{
		return this.players.stream().mapToInt(Player::getTotalExperience).sum();
	}

	@Override
	public float getWalkSpeed()
	{
		return (float) this.players.stream().mapToDouble(Player::getWalkSpeed).average().orElse(0);
	}

	@Override
	public void giveExp(int exp)
	{
		this.consume(player -> player.giveExp(exp));
	}

	@Override
	public void giveExpLevels(int levels)
	{
		this.consume(player -> player.giveExp(levels));
	}

	@Override
	public boolean hasAchievement(Achievement ach)
	{
		return this.getAnyResult(player -> player.hasAchievement(ach));
	}

	@Override
	public void hidePlayer(Player player)
	{
		this.consume(plr -> plr.hidePlayer(player));
	}

	@Override
	public void incrementStatistic(Statistic stat) throws IllegalArgumentException
	{
		this.consume(player -> player.incrementStatistic(stat));
	}

	@Override
	public void incrementStatistic(Statistic stat, int dat) throws IllegalArgumentException
	{
		this.consume(player -> player.incrementStatistic(stat, dat));
	}

	@Override
	public void incrementStatistic(Statistic stat, Material mat) throws IllegalArgumentException
	{
		this.consume(player -> player.incrementStatistic(stat, mat));
	}

	@Override
	public void incrementStatistic(Statistic stat, EntityType type) throws IllegalArgumentException
	{
		this.consume(player -> player.incrementStatistic(stat, type));
	}

	@Override
	public void incrementStatistic(Statistic stat, Material mat, int dat) throws IllegalArgumentException
	{
		this.consume(player -> player.incrementStatistic(stat, mat, dat));
	}

	@Override
	public void incrementStatistic(Statistic stat, EntityType type, int dat) throws IllegalArgumentException
	{
		this.consume(player -> player.incrementStatistic(stat, type, dat));
	}

	@Override
	public boolean isFlying()
	{
		return this.getAnyResult(Player::isFlying);
	}

	@Override
	public boolean isHealthScaled()
	{
		return this.getAnyResult(Player::isHealthScaled);
	}

	@Override
	public boolean isNotifyNoPerms()
	{
		return this.getAnyResult(Player::isNotifyNoPerms);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isOnGround()
	{
		return this.getMultiResult(Player::isOnGround);
	}

	@Override
	public boolean isPlayerTimeRelative()
	{
		return this.getAnyResult(Player::isPlayerTimeRelative);
	}

	@Override
	public boolean isSleepingIgnored()
	{
		return this.getMultiResult(Player::isSleepingIgnored);
	}

	@Override
	public boolean isSneaking()
	{
		return this.getAnyResult(Player::isSneaking);
	}

	@Override
	public boolean isSprinting()
	{
		return this.getAnyResult(Player::isSprinting);
	}

	@Override
	public void kickPlayer(String message)
	{
		this.consume(player -> player.kickPlayer(message));
	}

	@Override
	public void loadData()
	{
		this.consume(Player::loadData);
	}

	@Override
	public boolean performCommand(String command)
	{
		return this.consumeMultiResult(player -> player.performCommand(command));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void playEffect(Location loc, Effect effect, int dat)
	{
		this.consume(player -> player.playEffect(loc, effect, dat));
	}

	@Override
	public <T> void playEffect(Location loc, Effect effect, T dat)
	{
		this.consume(player -> player.playEffect(loc, effect, dat));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void playNote(Location location, byte dat1, byte dat2)
	{
		this.consume(player -> player.playNote(location, dat1, dat2));
	}

	@Override
	public void playNote(Location location, Instrument instrument, Note note)
	{
		this.consume(player -> player.playNote(location, instrument, note));
	}

	@Override
	public void playSound(Location location, Sound sound, float eitherPitchOrSoundIDontFuckingRemember0, float eitherPitchOrSoundIDontFuckingRemember1)
	{
		this.consume(player -> player.playSound(location, sound, eitherPitchOrSoundIDontFuckingRemember0, eitherPitchOrSoundIDontFuckingRemember1));
	}

	@Override
	public void playSound(Location location, String sound, float eitherPitchOrSoundIDontFuckingRemember0, float eitherPitchOrSoundIDontFuckingRemember1)
	{
		this.consume(player -> player.playSound(location, sound, eitherPitchOrSoundIDontFuckingRemember0, eitherPitchOrSoundIDontFuckingRemember1));
	}

	@Override
	public void removeAchievement(Achievement ach)
	{
		this.consume(player -> player.removeAchievement(ach));
	}

	@Override
	public void resetPlayerTime()
	{
		this.consume(Player::resetPlayerTime);
	}

	@Override
	public void resetPlayerWeather()
	{
		this.consume(Player::resetPlayerWeather);
	}

	@Override
	public void resetTitle()
	{
		this.consume(Player::resetTitle);
	}

	@Override
	public void saveData()
	{
		this.consume(Player::saveData);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void sendBlockChange(Location loc, Material mat, byte dat)
	{
		this.consume(player -> player.sendBlockChange(loc, mat, dat));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void sendBlockChange(Location loc, int mat, byte dat)
	{
		this.consume(player -> player.sendBlockChange(loc, mat, dat));
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean sendChunkChange(Location loc, int x, int y, int z, byte[] dat)
	{
		return this.getAnyResult(player -> player.sendChunkChange(loc, x, y, z, dat));
	}

	@Override
	public void sendMap(MapView map)
	{
		this.consume(player -> player.sendMap(map));
	}

	@Override
	public void sendRawMessage(String message)
	{
		this.consume(player -> player.sendRawMessage(message));
	}

	@Override
	public void sendSignChange(Location location, String[] lines) throws IllegalArgumentException
	{
		this.consume(player -> player.sendSignChange(location, lines));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void sendTitle(String main, String sub)
	{
		this.consume(player -> player.sendTitle(main, sub));
	}

	@Override
	public void setAllowFlight(boolean flight)
	{
		this.consume(player -> player.setAllowFlight(flight));
	}

	@Override
	public void setBedSpawnLocation(Location location)
	{
		this.consume(player -> player.setBedSpawnLocation(location));
	}

	@Override
	public void setBedSpawnLocation(Location location, boolean flag)
	{
		this.consume(player -> player.setBedSpawnLocation(location, flag));
	}

	@Override
	public void setCompassTarget(Location location)
	{
		this.consume(player -> player.setCompassTarget(location));
	}


	@Override
	public void setExhaustion(float exhaustion)
	{
		this.consume(player -> player.setExhaustion(exhaustion));
	}

	@Override
	public void setExp(float exp)
	{
		this.consume(player -> player.setExp(exp));
	}

	@Override
	public void setFlySpeed(float speed) throws IllegalArgumentException
	{
		this.consume(player -> player.setFlySpeed(speed));
	}

	@Override
	public void setFlying(boolean flying)
	{
		this.consume(player -> player.setFlying(flying));
	}

	@Override
	public void setFoodLevel(int level)
	{
		this.consume(player -> player.setFoodLevel(level));
	}

	@Override
	public void setHealthScale(double scale) throws IllegalArgumentException
	{
		this.consume(player -> player.setHealthScale(scale));
	}

	@Override
	public void setHealthScaled(boolean scaled)
	{
		this.consume(player -> player.setHealthScaled(scaled));
	}

	@Override
	public void setLevel(int level)
	{
		this.consume(player -> player.setLevel(level));
	}

	@Override
	public void setNotifyNoPerms(boolean notify)
	{
		this.consume(player -> player.setNotifyNoPerms(notify));
	}


	@Override
	public void setPlayerTime(long time, boolean flag)
	{
		this.consume(player -> player.setPlayerTime(time, flag));
	}

	@Override
	public void setPlayerWeather(WeatherType type)
	{
		this.consume(player -> player.setPlayerWeather(type));
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public void setResourcePack(String url)
	{
		this.consume(player -> player.setResourcePack(url));
	}

	@Override
	public void setSaturation(float saturation)
	{
		this.consume(player -> player.setSaturation(saturation));
	}

	@Override
	public void setScoreboard(Scoreboard board) throws IllegalArgumentException, IllegalStateException
	{
		this.consume(player -> player.setScoreboard(board));
	}

	@Override
	public void setSleepingIgnored(boolean ignore)
	{
		this.consume(player -> player.setSleepingIgnored(ignore));
	}

	@Override
	public void setSneaking(boolean sneak)
	{
		this.consume(player -> player.setSneaking(sneak));
	}

	@Override
	public void setSpectatorTarget(Entity ent)
	{
		this.consume(player -> player.setSpectatorTarget(ent));
	}

	@Override
	public void setSprinting(boolean sprint)
	{
		this.consume(player -> player.setSprinting(sprint));
	}

	@Override
	public void setStatistic(Statistic stat, int dat) throws IllegalArgumentException
	{
		this.consume(player -> player.setStatistic(stat, dat));
	}

	@Override
	public void setStatistic(Statistic stat, Material mat, int dat) throws IllegalArgumentException
	{
		this.consume(player -> player.setStatistic(stat, mat, dat));
	}

	@Override
	public void setStatistic(Statistic stat, EntityType ent, int val)
	{
		this.consume(player -> player.setStatistic(stat, ent, val));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setTexturePack(String url)
	{
		this.consume(player -> player.setTexturePack(url));
	}

	@Override
	public void setTotalExperience(int exp)
	{
		this.consume(player -> player.setTotalExperience(exp));
	}

	@Override
	public void setWalkSpeed(float speed) throws IllegalArgumentException
	{
		this.consume(player -> player.setWalkSpeed(speed));
	}

	@Override
	public void showPlayer(Player player)
	{
		this.consume(plr -> plr.showPlayer(player));
	}

	@Override
	public Spigot spigot()
	{
		return PlayerProxy.emptySpigot();
	}

	@Override
	public void updateInventory()
	{
		this.consume(Player::updateInventory);
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

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public void setTitleTimes(int i, int i1, int i2)
	{
		this.consume(player -> player.setTitleTimes(i, i1, i2));
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public void setSubtitle(BaseComponent[] baseComponents)
	{
		this.consume(player -> player.setSubtitle(baseComponents));
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public void setSubtitle(BaseComponent baseComponent)
	{
		this.consume(player -> player.setSubtitle(baseComponent));
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public void showTitle(BaseComponent[] baseComponents)
	{
		this.consume(player -> player.showTitle(baseComponents));
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public void showTitle(BaseComponent baseComponent)
	{
		this.consume(player -> player.showTitle(baseComponent));
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public void showTitle(BaseComponent[] baseComponents, BaseComponent[] baseComponents1, int i, int i1, int i2)
	{
		this.consume(player -> player.showTitle(baseComponents, baseComponents1, i, i1, i2));
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
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
		this.consume(player -> player.updateTitle(title));
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
		return null;
	}

	@Override
	public String getResourcePackHash()
	{
		return null;
	}

	@Override
	public boolean hasResourcePack()
	{
		return false;
	}

	@Override
	public int getArrowsStuck()
	{
		return 0;
	}

	@Override
	public void setArrowsStuck(int i)
	{
		this.consume(player -> player.setArrowsStuck(i));
	}

	@Override
	public boolean useCustomAi()
	{
		return false;
	}

	@Override
	public void setUseCustomAi(boolean b)
	{

	}

	@Override
	public Controller getCustomAiController()
	{
		return null;
	}
}
