package com.ulfric.lib.api.player;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public final class PlayerUtils {

	static IPlayerUtils impl = IPlayerUtils.EMPTY;

	private PlayerUtils()
	{
	}

	public static Block getTargetBlock(Player player, int range)
	{
		return impl.getTargetBlock(player, range);
	}

	public static int getPing(Player player)
	{
		return impl.getPing(player);
	}

	public static void sendActionBar(Player player, String text)
	{
		impl.sendActionBar(player, text);
	}

	public static void sendTitle(Player player, String title)
	{
		impl.sendTitle(player, title);
	}

	public static void sendTitle(Player player, String title, String subtitle)
	{
		impl.sendTitle(player, title, subtitle);
	}

	public static void sendTemporaryBlockChange(Player player, Location location, Material newBlock, long resetDelay)
	{
		impl.sendTemporaryBlockChange(player, location, newBlock, resetDelay);
	}

	public static Player getOnline(String nameOrUuid)
	{
		return impl.getOnline(nameOrUuid);
	}

	public static Player getOnlineExact(String nameOrUuid)
	{
		return impl.getOnlineExact(nameOrUuid);
	}

	public static Player getOnline(CommandSender sender, String nameOrUuid)
	{
		return impl.getOnline(sender, nameOrUuid);
	}

	public static OfflinePlayer getOffline(String nameOrUuid)
	{
		return impl.getOffline(nameOrUuid);
	}

	public static OfflinePlayer getOffline(String nameOrUuid, boolean allowFetch)
	{
		return impl.getOffline(nameOrUuid, allowFetch);
	}

	public static boolean hasPlayed(OfflinePlayer player)
	{
		return impl.hasPlayed(player);
	}

	public static Set<Player> getOnlinePlayersExceptFor(Player player, Player... players)
	{
		return impl.getOnlinePlayersExceptFor(player, players);
	}

	public static Set<Player> getOnlinePlayersWithPermission(String node, boolean has)
	{
		return impl.getOnlinePlayersWithPermission(node, has);
	}

	public static Set<Player> getOnlinePlayersWithIP(String ip)
	{
		return impl.getOnlinePlayersWithIP(ip);
	}

	public static String getIP(Player player)
	{
		return impl.getIP(player);
	}

	public static int countVanished()
	{
		return impl.countVanished();
	}

	public static boolean isVanished(Player player)
	{
		return impl.isVanished(player);
	}

	public static int vanish(Player player)
	{
		return impl.vanish(player);
	}

	public static void unvanish(Player player)
	{
		impl.unvanish(player);
	}

	public static Set<Player> vanished()
	{
		return impl.vanished();
	}

	public static Player getLinked(Object object)
	{
		return impl.getLinked(object);
	}

	public static PlayerProxy proxy(Player player)
	{
		return impl.proxy(player);
	}

	public static PlayerProxy proxy(UUID uuid)
	{
		return impl.proxy(uuid);
	}

	protected interface IPlayerUtils {
		IPlayerUtils EMPTY = new IPlayerUtils() {
		};

		default Player getLinked(Object object)
		{
			return null;
		}

		default Block getTargetBlock(Player player, int range)
		{
			return null;
		}

		default Player getOnlineExact(String nameOrUuid)
		{
			return null;
		}

		default String getIP(Player player)
		{
			return null;
		}

		default void sendActionBar(Player player, String text)
		{
		}

		default int countVanished()
		{
			return 0;
		}

		default int getPing(Player player)
		{
			return 0;
		}

		default void sendTitle(Player player, String title)
		{
		}

		default void sendTitle(Player player, String title, String subtitle)
		{
		}

		default void sendTemporaryBlockChange(Player player, Location location, Material newBlock, long resetDelay)
		{
		}

		default Player getOnline(String nameOrUuid)
		{
			return null;
		}

		default Player getOnline(CommandSender sender, String nameOrUuid)
		{
			return null;
		}

		default OfflinePlayer getOffline(String nameOrUuid)
		{
			return null;
		}

		default OfflinePlayer getOffline(String nameOrUuid, boolean allowFetch)
		{
			return null;
		}

		default boolean hasPlayed(OfflinePlayer player)
		{
			return false;
		}

		default Set<Player> getOnlinePlayersExceptFor(Player player1, Player... players)
		{
			return null;
		}

		default Set<Player> getOnlinePlayersWithPermission(String node, boolean has)
		{
			return null;
		}

		default Set<Player> getOnlinePlayersWithIP(String ip)
		{
			return null;
		}

		default boolean isVanished(Player player)
		{
			return false;
		}

		default int vanish(Player player)
		{
			return 0;
		}

		default void unvanish(Player player)
		{
		}

		default Set<Player> vanished()
		{
			return null;
		}

		default PlayerProxy proxy(Player player)
		{
			return null;
		}

		default PlayerProxy proxy(UUID uuid)
		{
			return null;
		}

		default PlayerProxy proxy(String name)
		{
			return null;
		}
	}

}
