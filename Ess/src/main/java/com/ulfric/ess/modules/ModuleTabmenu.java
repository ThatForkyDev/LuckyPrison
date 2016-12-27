package com.ulfric.ess.modules;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.nms.CraftPlayerVI;
import com.ulfric.lib.api.nms.PacketWrapper;
import com.ulfric.lib.api.nms.Packets;

public class ModuleTabmenu extends SimpleModule {

	public ModuleTabmenu()
	{
		super("tab-menu", "Tab menu mod", "Packet", "1.0.0-SNAPSHOT");

		this.withConf();
	}

	@Override
	public void postEnable()
	{
		FileConfiguration conf = this.getConf().getConf();

		this.joinPacket = Packets.wrap(Packets.newPacketPlayOutPlayerListHeaderFooter(Strings.formatF(conf.getString("join.header")), Strings.formatF(conf.getString("join.footer"))));
		this.quitPacket = Packets.wrap(Packets.newPacketPlayOutPlayerListHeaderFooter(Strings.formatF(conf.getString("quit.header")), Strings.formatF(conf.getString("quit.footer"))));
	}

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener()
		{
			@EventHandler
			public void onJoin(PlayerJoinEvent event)
			{
				CraftPlayerVI.of(event.getPlayer()).sendPacket(ModuleTabmenu.this.joinPacket.getValue());
			}

			@EventHandler
			public void onJoin(PlayerQuitEvent event)
			{
				CraftPlayerVI.of(event.getPlayer()).sendPacket(ModuleTabmenu.this.quitPacket.getValue());
			}
		});
	}

	private PacketWrapper joinPacket;
	private PacketWrapper quitPacket;

}