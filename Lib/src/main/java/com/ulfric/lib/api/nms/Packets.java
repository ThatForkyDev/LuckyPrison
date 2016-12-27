package com.ulfric.lib.api.nms;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;

public final class Packets {

	static IPackets impl = new IPackets() {
	};

	private Packets()
	{
	}

	public static PacketPlayOutPlayerListHeaderFooter newPacketPlayOutPlayerListHeaderFooter(String header, String footer)
	{
		return impl.newPacketPlayOutPlayerListHeaderFooter(header, footer);
	}

	public static PacketWrapper newDisconnect()
	{
		return impl.newDisconnect();
	}

	public static PacketPlayInClientCommand newRespawn()
	{
		return impl.newRespawn();
	}

	public static PacketPlayOutChat newChat(String text, int position)
	{
		return impl.newChat(text, position);
	}

	public static PacketWrapper wrap(Packet<?> packet)
	{
		return impl.wrap(packet);
	}

	protected interface IPackets {
		IPackets EMPTY = new IPackets() {
		};

		default PacketWrapper wrap(Packet<?> packet)
		{
			return null;
		}

		default PacketPlayInClientCommand newRespawn()
		{
			return null;
		}

		default PacketWrapper newDisconnect()
		{
			return null;
		}

		default PacketPlayOutPlayerListHeaderFooter newPacketPlayOutPlayerListHeaderFooter(String header, String footer)
		{
			return null;
		}

		default PacketPlayOutChat newChat(String text, int position)
		{
			return null;
		}
	}

}
