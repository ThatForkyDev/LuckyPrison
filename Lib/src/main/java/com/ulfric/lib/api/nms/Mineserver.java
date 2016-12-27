package com.ulfric.lib.api.nms;

import com.ulfric.lib.api.time.Timestamp;
import net.minecraft.server.v1_8_R3.MinecraftServer;

public final class Mineserver {

	static IMineserver impl = IMineserver.EMPTY;

	private Mineserver()
	{
	}

	public static MinecraftServer getMinecraft()
	{
		return impl.getMinecraft();
	}

	public static Timestamp uptime()
	{
		return impl.uptime();
	}

	protected interface IMineserver {
		IMineserver EMPTY = new IMineserver() {
		};

		default MinecraftServer getMinecraft()
		{
			return null;
		}

		default Timestamp uptime()
		{
			return null;
		}
	}

}
