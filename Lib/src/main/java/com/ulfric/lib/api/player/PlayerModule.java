package com.ulfric.lib.api.player;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.block.BlockUtils;
import com.ulfric.lib.api.collect.Sets;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.java.Uuids;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.nms.CraftPlayerVI;
import com.ulfric.lib.api.nms.Packets;
import com.ulfric.lib.api.server.Events;
import com.ulfric.lib.api.task.Tasks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.Metadatable;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("deprecation")
public final class PlayerModule extends SimpleModule {

	public PlayerModule()
	{
		super("playerutils", "Player utilities module", "Packet", "1.0.0-REL");

		this.withSubModule(new GamemodeModule());
		this.withSubModule(new PermissionModule());
		this.withSubModule(new PlayerEventsModule());

		this.addListener(new Listener() {
			@EventHandler(priority = EventPriority.LOWEST)
			public void onJoin(PlayerJoinEvent event)
			{
				event.setJoinMessage(null);

				Player player = event.getPlayer();

				if (!player.hasPermission("lib.vanish.see"))
				{
					for (Player lplayer : PlayerUtils.vanished())
					{
						player.hidePlayer(lplayer);
					}
				}

				if (!(player.hasPermission("ess.vanish"))) return;

				Iterator<Player> iterator = PlayerUtils.vanished().iterator();
				while (iterator.hasNext())
				{
					if (!iterator.next().getUniqueId().equals(player.getUniqueId())) continue;

					iterator.remove();

					PlayerUtils.vanish(player);

					break;
				}
			}
		});
	}

	@Override
	public void postEnable()
	{
		PlayerUtils.impl = new PlayerUtils.IPlayerUtils() {
			private final Map<UUID, PlayerProxy> proxies = Maps.newHashMap();
			private final Set<Player> hidden = Sets.newWeakSet();

			@Override
			public Player getLinked(Object object)
			{
				if (object == null) return null;

				if (object instanceof Metadatable)
				{
					return this.getPlayerFromMetadatable((Metadatable) object);
				}

				return null;
			}

			@Override
			public Block getTargetBlock(Player player, int range)
			{
				return player.getTargetBlock(BlockUtils.getTransparent(), range);
			}

			@Override
			public Player getOnlineExact(String nameOrUuid)
			{
				return this.getOnline(null, nameOrUuid, true);
			}

			@Override
			public String getIP(Player player)
			{
				String ip = Metadata.getValueAsString(player, "_ulf_inetaddr");

				if (ip == null)
				{
					ip = StringUtils.formatIP(player.getAddress().getHostString());

					Metadata.apply(player, "_ulf_inetaddr", ip);
				}

				return ip;
			}

			@Override
			public void sendActionBar(Player player, String text)
			{
				Packets.wrap(Packets.newChat(text, 2)).send(player);
			}

			@Override
			public int countVanished()
			{
				return this.hidden.size();
			}

			@Override
			public int getPing(Player player)
			{
				return CraftPlayerVI.of(player).getHandle().ping;
			}

			@Override
			public void sendTitle(Player player, String title)
			{
				this.sendTitle(player, title, null);
			}

			@Override
			public void sendTitle(Player player, String title, String subtitle)
			{
				player.sendTitle(title, subtitle);
			}

			@Override
			public void sendTemporaryBlockChange(Player player, Location location, Material newBlock, long resetDelay)
			{
				Tasks.runLater(() ->
							   {
								   player.sendBlockChange(location, newBlock, location.getBlock().getData());
								   Tasks.runLater(() ->
												  {
													  if (!player.isValid() || !player.isOnline()) return;

													  player.sendBlockChange(location, location.getBlock().getType(), location.getBlock().getData());
												  }, resetDelay);
							   }, 2L);
			}

			@Override
			public Player getOnline(String nameOrUuid)
			{
				return this.getOnline(null, nameOrUuid);
			}

			@Override
			public Player getOnline(CommandSender sender, String nameOrUuid)
			{
				return this.getOnline(sender, nameOrUuid, false);
			}

			@Override
			public OfflinePlayer getOffline(String nameOrUuid)
			{
				return this.getOffline(nameOrUuid, false);
			}

			@Override
			public OfflinePlayer getOffline(String nameOrUuid, boolean allowFetch)
			{
				OfflinePlayer player;

				UUID uuid = Uuids.parse(nameOrUuid);
				if (uuid != null)
				{
					player = Bukkit.getPlayer(uuid);

					if (player == null)
					{
						player = Bukkit.getOfflinePlayer(uuid);
					}
				}
				else
				{
					player = Bukkit.getPlayerExact(nameOrUuid);

					if (player == null)
					{
						player = Bukkit.getOfflinePlayer(nameOrUuid);
					}
				}

				if (player == null) return null;

				if (!allowFetch && !this.hasPlayed(player)) return null;

				return player;
			}

			@Override
			public boolean hasPlayed(OfflinePlayer player)
			{
				return player.hasPlayedBefore() || player.isOnline();
			}

			@Override
			public Set<Player> getOnlinePlayersExceptFor(Player player, Player... extraPlayers)
			{
				Set<Player> players = Sets.newHashSet(Bukkit.getOnlinePlayers());

				players.remove(player);

				for (Player extraPlayer : extraPlayers)
				{
					players.remove(extraPlayer);
				}

				return players;
			}

			@Override
			public Set<Player> getOnlinePlayersWithPermission(String node, boolean has)
			{
				Set<Player> players = Sets.newHashSet();

				if (has)
				{
					for (Player player : Bukkit.getOnlinePlayers())
					{
						if (!player.hasPermission(node)) continue;

						players.add(player);
					}

					return players;
				}

				for (Player player : Bukkit.getOnlinePlayers())
				{
					if (player.hasPermission(node)) continue;

					players.add(player);
				}

				return players;
			}

			@Override
			public Set<Player> getOnlinePlayersWithIP(String ip)
			{
				Set<Player> players = Sets.newHashSet();

				for (Player player : Bukkit.getOnlinePlayers())
				{
					if (!this.getIP(player).equals(ip)) continue;

					players.add(player);
				}

				return players;
			}

			@Override
			public boolean isVanished(Player player)
			{
				return this.hidden.contains(player);
			}

			/*
			public Player getPlayer(CommandSender sender, String nameOrUuid)
			{
				Player player = sender instanceof Player ? (Player) sender : Players.of(sender);

				UUID uuid = Uuids.parse(nameOrUuid);

				Player found = null;

				if (uuid != null)
				{
					found = Bukkit.getPlayer(uuid);
				}
				else
				{
					found = this.getPlayer(nameOrUuid);
				}

				if (found == null || !player.canSee(found)) return null;

				return found;
			}

			private final Pattern all = Pattern.compile("[\\*]+");
			private final Pattern random = Pattern.compile("[\\?]+([\\^][0-9]+)?([\\%]([\\-])?[a-zA-Z0-9\\.]*)?");
			private Player getPlayer(String longName)
			{
				Player found = this.getPlayerShortName(longName);

				if (found != null) return found;

				Set<Player> players = Sets.newHashSet();

				String[] names = longName.split(",");

				for (String name : names)
				{
					boolean remove;
					if (remove = name.startsWith("-"))
					{
						name = name.substring(1);
					}

					found = this.getPlayerShortName(name);

					if (found != null)
					{
						if (remove)
						{
							players.remove(found);

							continue;
						}

						players.add(found);

						continue;
					}

					if (this.all.matcher(name).matches())
					{
						if (remove)
						{
							players.removeAll(Bukkit.getOnlinePlayers());

							continue;
						}

						players.addAll(Bukkit.getOnlinePlayers());

						continue;
					}

					if (this.random.matcher(name).matches())
					{
						String[] random = name.split("[\\^]");

						if (random.length <= 1)
						{
							Player randomPlayer = RandomUtils.randomValueFromCollection(Bukkit.getOnlinePlayers());

							if (remove)
							{
								players.remove(randomPlayer);

								continue;
							}

							players.add(randomPlayer);

							continue;
						}
					}
				}

				if (players.isEmpty()) return null;

				if (players.size() == 1) return players.iterator().next();

				return new Players(players);
			}

			private Player getPlayerShortName(String name)
			{
				Player found = Bukkit.getPlayerExact(name);

				if (found != null) return found;

				return Bukkit.getPlayer(name);
			}
			*/

			@Override
			public int vanish(Player player)
			{
				if (!this.hidden.add(player)) return 0;

				Set<Player> players = this.getOnlinePlayersWithPermission("lib.vanish.see", false);

				players.remove(player);

				for (Player lplayer : players)
				{
					lplayer.hidePlayer(player);
				}

				Events.call(new PlayerVanishEvent(player, true));

				return players.size();
			}

			@Override
			public void unvanish(Player player)
			{
				if (!this.hidden.remove(player)) return;

				for (Player lplayer : Bukkit.getOnlinePlayers())
				{
					lplayer.showPlayer(player);
				}

				Events.call(new PlayerVanishEvent(player, false));
			}

			@Override
			public Set<Player> vanished()
			{
				return this.hidden;
			}

			@Override
			public PlayerProxy proxy(Player player)
			{
				Assert.notNull(player);

				if (player instanceof PlayerProxy)
				{
					return (PlayerProxy) player;
				}

				if (!this.proxies.isEmpty())
				{
					PlayerProxy proxy = this.proxies.get(player.getUniqueId());

					if (proxy != null)
					{
						return proxy;
					}
				}

				PlayerProxy proxy = new PlayerProxy(player);

				this.proxies.put(player.getUniqueId(), proxy);

				return proxy;
			}

			@Override
			public PlayerProxy proxy(UUID uuid)
			{
				Assert.notNull(uuid);

				if (!this.proxies.isEmpty())
				{
					PlayerProxy proxy = this.proxies.get(uuid);

					if (proxy != null)
					{
						return proxy;
					}
				}

				PlayerProxy proxy = new PlayerProxy(uuid);

				this.proxies.put(uuid, proxy);

				return proxy;
			}

			private Player getPlayerFromMetadatable(Metadatable metadatable)
			{
				if (metadatable == null) return null;

				if (metadatable instanceof Player) return (Player) metadatable;

				Player tied = Metadata.getTied(metadatable);

				if (tied != null) return tied;

				if (metadatable instanceof Projectile)
				{
					ProjectileSource sauce = ((Projectile) metadatable).getShooter();

					if (sauce instanceof Entity)
					{
						return this.getPlayerFromMetadatable((Metadatable) sauce);
					}

					return null;
				}

				if (metadatable instanceof TNTPrimed)
				{
					return this.getPlayerFromMetadatable(((TNTPrimed) metadatable).getSource());
				}

				return null;
			}

			private Player getOnline(CommandSender sender, String nameOrUuid, boolean exact)
			{
				Player player;

				UUID uuid = Uuids.parse(nameOrUuid);

				if (uuid != null)
				{
					player = Bukkit.getPlayer(uuid);
				}
				else
				{
					player = exact ? Bukkit.getPlayerExact(nameOrUuid) : Bukkit.getPlayer(nameOrUuid);
				}

				if (sender != null && player != null && this.hidden.contains(player) && !sender.hasPermission("ess.vanish.see"))
				{ return null; }

				if (sender instanceof Player)
				{
					if (((Player) sender).canSee(player)) return player;

					return null;
				}

				return player;
			}
		};
	}

	private static final class PermissionModule extends SimpleModule {
		PermissionModule()
		{
			super("permissionutils", "Permission utilities module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			PermissionUtils.impl = new PermissionUtils.IPermissionUtils() {
				@Override
				public int getMax(Permissible permissible, String path)
				{
					return this.getMax(permissible.getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).iterator(), path);
				}

				@Override
				public int getMax(Iterable<String> permissions, String path)
				{
					return this.getMax(permissions.iterator(), path);
				}

				@Override
				public boolean hasNestedAccess(Permissible permissible, String path, String extra)
				{
					return permissible.hasPermission(path + ".all") || permissible.hasPermission(Strings.format("{0}.{1}", path, extra));
				}

				private int getMax(Iterator<String> permissions, String path)
				{

					path = path.toLowerCase() + '.';

					int max = 1;
					while (permissions.hasNext())
					{
						String node = permissions.next().toLowerCase();

						if (!node.startsWith(path)) continue;

						if (node.endsWith("unlimited")) return Integer.MAX_VALUE;

						String[] split = node.split("\\.");

						Integer current = Numbers.parseInteger(split[split.length - 1]);

						if (current == null) continue;

						int currentVal = current;

						if (currentVal == Integer.MAX_VALUE) return Integer.MAX_VALUE;

						if (currentVal <= max) continue;

						max = currentVal;
					}

					return max;
				}
			};
		}

		@Override
		public void postDisable()
		{
			PermissionUtils.impl = PermissionUtils.IPermissionUtils.EMPTY;
		}
	}

	private static final class GamemodeModule extends SimpleModule {
		GamemodeModule()
		{
			super("gamemodeutils", "GameMode utilities module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Gamemodes.impl = new Gamemodes.IGamemodes() {
				@Override
				public GameMode parse(String string)
				{
					GameMode mode = null;
					try
					{
						mode = GameMode.getByValue(Integer.parseInt(string));
					}
					catch (NumberFormatException exception)
					{
						try
						{
							mode = GameMode.valueOf(string.toUpperCase());
						}
						catch (IllegalArgumentException exceptionIA) { }
					}
					return mode;
				}
			};
		}

		@Override
		public void postDisable()
		{
			Gamemodes.impl = Gamemodes.IGamemodes.EMPTY;
		}
	}

	@Override
	public void postDisable()
	{
		PlayerUtils.impl = PlayerUtils.IPlayerUtils.EMPTY;
	}


}
