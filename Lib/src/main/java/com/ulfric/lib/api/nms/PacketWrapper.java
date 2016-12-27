package com.ulfric.lib.api.nms;

import com.ulfric.lib.api.java.Wrapper;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

public final class PacketWrapper extends Wrapper<Packet<?>> {

	PacketWrapper(Packet<?> packet)
	{
		super(packet);
	}

	public void send(Player player)
	{
		this.send(CraftPlayerVI.of(player));
	}

	public void send(CraftPlayerVI player)
	{
		player.sendPacket(this.getValue());
	}

}
