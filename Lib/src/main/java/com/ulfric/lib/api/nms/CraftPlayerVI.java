package com.ulfric.lib.api.nms;

import com.ulfric.lib.api.java.Proxy;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Craft Player Version Independent
 * <p>
 * Provides a wrapper to access a craftplayer without need to update every version
 *
 * @author Adam
 */
public final class CraftPlayerVI implements Proxy<CraftPlayer> {

	static ICraftPlayerVI impl = ICraftPlayerVI.EMPTY;
	private final CraftPlayer player;

	CraftPlayerVI(Player player)
	{
		this.player = (CraftPlayer) player;
	}

	/**
	 * Creates a new CraftPlayerVI object of the player
	 *
	 * @param player The player to wrap the CraftPlayerVI around
	 * @return A new CraftPlayerVI of the player
	 */
	public static CraftPlayerVI of(Player player)
	{
		return impl.of(player);
	}

	public CraftPlayer getPlayer()
	{
		return this.player;
	}

	public EntityPlayer getHandle()
	{
		return this.player.getHandle();
	}

	public void sendPacket(Packet<?> packet)
	{
		this.player.getHandle().playerConnection.sendPacket(packet);
	}

	@Override
	@Deprecated
	public CraftPlayer get()
	{
		return this.player;
	}

	protected interface ICraftPlayerVI {
		ICraftPlayerVI EMPTY = new ICraftPlayerVI() {
		};

		default CraftPlayerVI of(Player player)
		{
			return null;
		}
	}

}
