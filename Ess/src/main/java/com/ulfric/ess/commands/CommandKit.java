package com.ulfric.ess.commands;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.ulfric.ess.commands.arg.KitStrategy;
import com.ulfric.ess.entity.Kit;
import com.ulfric.ess.lang.Permissions;
import com.ulfric.ess.modules.ModuleKits;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.inventory.InventoryUtils;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PermissionUtils;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.TimeUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandKit extends SimpleCommand {


	public CommandKit()
	{
		this.withArgument("kit", KitStrategy.INSTANCE);
		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, Permissions.KIT_OTHERS);
	}


	@Override
	public void run()
	{
		Player target = (Player) this.getObject("player");

		CommandSender sender = this.getSender();

		Kit kit = (Kit) this.getObject("kit");

		if (this.getArgs().length == 0)
		{
			Locale.send(this.getSender(), "ess.kit_fetch");
			Tasks.runAsync(() ->
			{
				ComponentBuilder message = new ComponentBuilder(Locale.getMessage(sender, "ess.kits"));
				Set<Map.Entry<String, Kit>> entries = ModuleKits.get().getKits().entrySet();
				String click = Locale.getMessage(sender, "ess.click_to_use");
				String remain = Locale.getMessage(sender, "ess.kit_it_cooldown");

				boolean first = true;
				for (Map.Entry<String, Kit> entry : entries)
				{
					if (!first)
					{
						message.append(", ").color(ChatColor.GRAY);
					}
					else
					{
						first = false;
					}

					String name = entry.getKey().toLowerCase();

					if (!PermissionUtils.hasNestedAccess(this.getSender(), "ess.kit", name)) continue;

					Kit.KitCooldown cooldown = entry.getValue().canUse(target);

					Kit.KitUsageType type = cooldown.getType();

					if (type.equals(Kit.KitUsageType.CANNOT_USE_SINGULAR)) continue;

					message.append(WordUtils.capitalize(name));

					if (type.equals(Kit.KitUsageType.CAN_USE) || type.equals(Kit.KitUsageType.CAN_USE_COOLDOWN))
					{
						message.color(ChatColor.GREEN);

						message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
													 TextComponent.fromLegacyText(click)));

						message.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/kit " + name));
					}
					else
					{
						message.color(ChatColor.RED);

						message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
													 TextComponent.fromLegacyText(Strings.format(remain, TimeUtils.secondsToString(cooldown.getCooldown())))));
					}

				}

				this.getSender().sendMessage(message.create());
			});

			return;
		}
		else if (kit == null)
		{
			Locale.sendError(sender, "ess.kit_not_found");

			return;
		}

		String name = kit.getName();

		if (!PermissionUtils.hasNestedAccess(this.getSender(), "ess.kit", name))
		{
			Locale.sendError(sender, "ess.kit_no_access", name);

			return;
		}

		Kit.KitCooldown cooldownObject = kit.canUse(this.getPlayer());

		switch (cooldownObject.getType())
		{
			case CANNOT_USE_COOLDOWN:
				Locale.sendError(target, "ess.kit_cooldown", TimeUtils.secondsToString(cooldownObject.getCooldown()+1));
				return;

			case CANNOT_USE_SINGULAR:
				Locale.sendError(target, "ess.kit_cooldown_once");
				return;

			case CAN_USE_COOLDOWN:
				Hooks.DATA.setPlayerData(target.getUniqueId(), "cooldown.kits." + name.toLowerCase(), System.currentTimeMillis());

			default:
				break;
		}

		Inventory inventory = target.getInventory();

		Location location = target.getEyeLocation();

		for (ItemStack item : kit.getContents())
		{
			if (item == null) break;

			if (item.getType().equals(Material.WRITTEN_BOOK))
			{
				ItemUtils.replacePagePlaceholders(item, target);
			}

			if (InventoryUtils.isFull(inventory))
			{
				location.getWorld().dropItemNaturally(location, item);

				continue;
			}

			inventory.addItem(item);
		}
	}


}
