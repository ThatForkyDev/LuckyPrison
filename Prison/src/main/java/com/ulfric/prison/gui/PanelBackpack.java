package com.ulfric.prison.gui;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.gui.Panel;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.inventory.ItemBuilder;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.player.PermissionUtils;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.prison.lang.Permissions;

public class PanelBackpack extends Panel {

	public static PanelBackpack create(Player player, OfflinePlayer target, int page)
	{
		return new PanelBackpack(player, target, page);
	}

	private PanelBackpack(Player player, OfflinePlayer target, int page)
	{
		super("backpack", Strings.format(Locale.getMessage(player, "prison.backpack_title")), 36, player, true, -1, 27, 35);

		this.page = page;

		this.target = target == null ? player : target;
	}

	private int page;
	public int getPage() { return this.page; }

	private final OfflinePlayer target;
	public OfflinePlayer getTarget() { return this.target; }

	@Override
	public void onOpen(Player player)
	{
		Tasks.run(() ->
		{
			if (this.target == null) return;

			int max = PermissionUtils.getMax(player, Permissions.MULTIPLE_BACKPACKS)-1;

			if (max < this.page)
			{
				Locale.sendError(player, "prison.backpack_limit", max+1);

				this.close(player);

				return;
			}

			UUID uuid = this.target.getUniqueId();

			Map<String, List<String>> values = Hooks.DATA.getPlayerDataRecursively(uuid, "prison.backpack.page");

			if (this.page > 0 && !player.hasPermission(Permissions.MULTIPLE_BACKPACKS))
			{
				Locale.sendError(player, "prison.backpack_one");

				this.close(player);

				return;
			}

			if (this.getPage() > 0)
			{
				this.setItem(27, new ItemBuilder().withType(Material.ARROW).withAmount(Math.max(Math.min(this.page, this.getSize()), 1)).withName(ChatColor.GOLD + "LAST PAGE").withLore(ChatColor.RED + Strings.format("Click to go to page {0}!", this.page)).build());
			}

			this.setItem(35, new ItemBuilder().withType(Material.ARROW).withAmount(Math.min(this.getPage()+2, this.getSize())).withName(ChatColor.GOLD + "NEXT PAGE").withLore(ChatColor.RED + Strings.format("Click to go to page {0}!", this.page+2)).build());

			if (this.page >= values.size()) return;

			List<String> itemStrings = values.get("prison.backpack.page." + this.page);

			if (CollectUtils.isEmpty(itemStrings)) return;

			for (String itemString : itemStrings)
			{
				try
				{
					this.setItem(Numbers.parseInteger(StringUtils.findOption(itemString, "slot")), ItemUtils.fromString(itemString));
				}
				catch (Exception exception)
				{
					System.out.println("FAILED TO DESERIALIZE ITEMSTACK: " + itemString);
				}
			}
		});
	}

	@Override
	public void onClose(Player player)
	{
		List<String> items = Lists.newArrayList();

		int slot = -1;
		for (ItemStack item : this)
		{
			slot++;

			if (ItemUtils.isEmpty(item)) continue;

			if (ItemUtils.is(item, Material.ARROW) && (slot == 27 || slot == 35)) continue;

			items.add(Strings.format("slot.{0} {1}", slot, ItemUtils.toString(item)));
		}

		OfflinePlayer target = Optional.ofNullable(this.target).orElse(player);

		if (target == null) return;

		Hooks.DATA.setPlayerData(target.getUniqueId(), "prison.backpack.page." + this.page, items);
	}

	@Override
	public boolean isAllowed(int click) {
		return (this.page == 0 && click == 27) || super.isAllowed(click);
	}

	@Override
	public void onClick(Player player, ItemStack item, int slot)
	{
		if (this.page == 0 && slot == 27) return;

		if (slot != 27 && slot != 35) return;

		this.onClose(player);

		this.clear();

		this.page += slot == 35 ? 1 : -1;

		this.onOpen(player);
	}

}