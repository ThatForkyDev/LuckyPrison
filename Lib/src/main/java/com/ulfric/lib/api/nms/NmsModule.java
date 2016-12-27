package com.ulfric.lib.api.nms;

import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.reflect.Reflect;
import com.ulfric.lib.api.time.Timestamp;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.WeakHashMap;

public final class NmsModule extends SimpleModule {

	public NmsModule()
	{
		super("nms", "NetMinecraftServer utilities module", "Packet", "1.0.0-REL");

		this.withSubModule(new CraftModule());
	}

	private static final class CraftModule extends SimpleModule {
		CraftModule()
		{
			super("craft", "OrgBukkit utilities module", "Packet", "1.0.0-REL");

			this.withSubModule(new PacketsModule());
			this.withSubModule(new CraftPlayerModule());
			this.withSubModule(new CraftEntityModule());
			this.withSubModule(new MineserverModule());
		}
	}

	private static final class MineserverModule extends SimpleModule {
		MineserverModule()
		{
			super("mineserver", "MinecraftServer object proxy module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Mineserver.impl = new Mineserver.IMineserver() {
				private final Timestamp uptime = Timestamp.now();

				@Override
				public MinecraftServer getMinecraft()
				{
					return MinecraftServer.getServer();
				}

				@Override
				public Timestamp uptime()
				{
					return this.uptime;
				}
			};
		}
	}

	private static final class PacketsModule extends SimpleModule {
		PacketsModule()
		{
			super("packets", "Minecraft packet utilities module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Packets.impl = new Packets.IPackets() {
				private PacketWrapper disconnect;

				@Override
				public PacketWrapper wrap(Packet<?> packet)
				{
					return new PacketWrapper(packet);
				}

				@Override
				public PacketPlayInClientCommand newRespawn()
				{
					return new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN);
				}

				@Override
				public PacketWrapper newDisconnect()
				{
					if (this.disconnect != null) return this.disconnect;

					synchronized (this)
					{
						if (this.disconnect == null)
						{
							this.disconnect = this.wrap(new PacketPlayOutBlockChange());
						}

						return this.disconnect;
					}
				}

				@Override
				public PacketPlayOutPlayerListHeaderFooter newPacketPlayOutPlayerListHeaderFooter(String header, String footer)
				{
					PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(Chat.asMinecraftText(header));

					if (footer == null || footer.isEmpty()) return packet;

					Reflect.trySet("b", packet, Chat.asMinecraftText(footer));

					return packet;
				}

				@Override
				public PacketPlayOutChat newChat(String text, int position)
				{
					return new PacketPlayOutChat(Chat.asMinecraftText(text), (byte) position);
				}
			};
		}

		@Override
		public void postDisable()
		{
			Packets.impl = Packets.IPackets.EMPTY;
		}
	}

	private static final class CraftPlayerModule extends SimpleModule {
		CraftPlayerModule()
		{
			super("craftplayer", "CraftPlayerVI class module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			CraftPlayerVI.impl = new CraftPlayerVI.ICraftPlayerVI() {
				private final Map<Player, CraftPlayerVI> weakMap = new WeakHashMap<>();

				@Override
				public CraftPlayerVI of(Player player)
				{
					return this.weakMap.computeIfAbsent(player, CraftPlayerVI::new);
				}
			};
		}

		@Override
		public void postDisable()
		{
			CraftPlayerVI.impl = CraftPlayerVI.ICraftPlayerVI.EMPTY;
		}
	}

	private static final class CraftEntityModule extends SimpleModule {
		CraftEntityModule()
		{
			super("craftentity", "CraftEntityVI class module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			CraftEntityVI.impl = new CraftEntityVI.ICraftEntityVI() {
				private final Map<Entity, CraftEntityVI> weakMap = new WeakHashMap<>();

				@Override
				public CraftEntityVI of(Entity entity)
				{
					return this.weakMap.computeIfAbsent(entity, CraftEntityVI::new);
				}
			};
		}

		@Override
		public void postDisable()
		{
			CraftEntityVI.impl = CraftEntityVI.ICraftEntityVI.EMPTY;
		}
	}

}
