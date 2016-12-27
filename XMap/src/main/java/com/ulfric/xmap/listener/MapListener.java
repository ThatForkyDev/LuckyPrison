package com.ulfric.xmap.listener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.xmap.map.MapLoader;
import com.ulfric.xmap.map.XMapCanvas;
import com.ulfric.xmap.map.XMapRenderer;

@SuppressWarnings("deprecation")
public class MapListener implements Listener {

	@EventHandler
	public void onMap(MapInitializeEvent event)
	{
		ItemStack item = event.getItem();

		if (item == null)
		{
			if (event.getPlayer() == null) return;

			item = event.getPlayer().getItemInHand();

			if (item == null) return;
		}

		if (!item.hasItemMeta()) return;

		ItemMeta meta = item.getItemMeta();

		if (!meta.hasLore()) return;

		XMapCanvas xmap = null;

		for (String lore : meta.getLore())
		{
			lore = ChatColor.stripColor(lore).toLowerCase();

			if (!lore.startsWith("book:")) continue;

			xmap = MapLoader.getMap(lore.split("\\:")[1].trim());

			break;
		}

		if (xmap == null) return;

		MapView view = event.getMap();

		view.setScale(xmap.getScale());

		//view.setCenterX(xmap.getCenterX());

		//view.setCenterZ(xmap.getCenterZ());

		view.getRenderers().clear();

		view.addRenderer(xmap.getRenderer(0));
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event)
	{
		int indexOffset = 0;

		switch (event.getAction())
		{
			case LEFT_CLICK_AIR:
			case LEFT_CLICK_BLOCK:
				indexOffset = -1;
				break;

			case RIGHT_CLICK_AIR:
			case RIGHT_CLICK_BLOCK:
				indexOffset = 1;
				break;

			default:
				return;
		}

		ItemStack item = event.getItem();

		if (!ItemUtils.is(item, Material.MAP)) return;

		MapView view = Bukkit.getMap(item.getDurability());

		if (view == null) return;

		if (view.getRenderers().isEmpty()) return;

		XMapRenderer xrenderer = null;
		XMapCanvas xmap = null;
		int index = indexOffset;

		for (MapRenderer renderer : view.getRenderers())
		{
			if (!(renderer instanceof XMapRenderer)) continue;

			xrenderer = (XMapRenderer) renderer;

			xmap = xrenderer.getXMap();

			index += xrenderer.getIndex();

			break;
		}

		if (xmap == null) return;

		Player player = event.getPlayer();

		if (index < 0)
		{
			Locale.sendError(player, "xmap.page_first");

			return;
		}

		if (index >= xmap.getTotalRenderers())
		{
			Locale.sendError(player, "xmap.page_last");

			return;
		}

		MapRenderer renderer = xmap.getRenderer(index);

		if (renderer == null) return; // Better be safe than sorry (and not crashy)

		item.setDurability((short) (item.getDurability()+indexOffset));

		ItemMeta meta = item.getItemMeta();

		if (xmap.getDisplayName() != null)
		{
			meta.setDisplayName(xmap.getDisplayName());
		}

		List<String> loreClone = xmap.getLore();

		if (!CollectUtils.isEmpty(loreClone))
		{
			List<String> lore = Lists.newArrayListWithExpectedSize(loreClone.size());

			String indexStr = String.valueOf(++index);

			for (String loreEntry : loreClone)
			{
				lore.add(loreEntry.replace("<index>", indexStr));
			}

			meta.setLore(lore);
		}

		item.setItemMeta(meta);
	}

}