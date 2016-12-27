package com.ulfric.chat.modules;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.ulfric.chat.lang.Meta;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.gui.Panel;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.task.Tasks;

class ModuleTags extends SimpleModule {

	public static final ModuleTags INSTANCE = new ModuleTags();

	private ModuleTags()
	{
		super("tags", "Chat tag management module", "Packet", "1.0.0-REL");

		this.withConf();

		this.addCommand("tags", new CommandTags());
	}

	@Override
	public void postEnable()
	{
		this.tags = new TreeSet<>();

		FileConfiguration conf = this.getConf().getConf();

		for (String path : conf.getKeys(false))
		{
			if (path.equals("none"))
			{
				this.none = new ChatTag(path, conf.getString(path + ".text"), ItemUtils.fromString(conf.getString(path + ".item")));

				continue;
			}

			this.tags.add(new ChatTag(path, Chat.color(conf.getString(path + ".text")), ItemUtils.fromString(conf.getString(path + ".item"))));
		}
	}

	private Set<ChatTag> tags;
	public Set<ChatTag> getTags()
	{
		if (!this.isModuleEnabled()) return null;

		return this.tags;
	}
	public ChatTag getTag(String id)
	{
		if (!this.isModuleEnabled() || id.equals("none") || id.equals("off"))
		{
			return ModuleTags.INSTANCE.none;
		}

		for (ChatTag tag : this.tags)
		{
			if (!tag.getId().equals(id)) continue;
		
			return tag;
		}

		return null;
	}

	class CommandTags extends SimpleCommand
	{
		public CommandTags()
		{
			this.withEnforcePlayer();

			this.withArgument("tag", ArgStrategy.STRING);
		}

		@Override
		public void run()
		{
			if (!this.hasObjects())
			{
				PanelTags.make(this.getPlayer());

				return;
			}

			String tagId = (String) this.getObject("tag");

			if (!this.hasPermission("chat.tags." + tagId))
			{
				Locale.sendError(this.getPlayer(), "chat.tag_err_access");

				return;
			}

			ChatTag tag = ModuleTags.this.getTag(tagId);

			if (tag == null)
			{
				Locale.sendError(this.getPlayer(), "chat.tag_err_not_found");

				return;
			}

			Hooks.DATA.setPlayerData(this.getUniqueId(), Meta.TAG, tag.getId());
		}
	}

	private static List<ChatTag> populate(Player player)
	{
		List<ChatTag> tags = Lists.newArrayList();

		if (ModuleTags.INSTANCE.none != null)
		{
			tags.add(ModuleTags.INSTANCE.none);
		}

		if (player == null) return tags;

		for (ChatTag tag : ModuleTags.INSTANCE.getTags())
		{
			if (!player.hasPermission("chat.tags." + tag.getId())) continue;

			tags.add(tag);
		}

		return tags;
	}
	static class PanelTags extends Panel
	{
		public static PanelTags make(Player player)
		{
			return new PanelTags(player, ModuleTags.populate(player));
		}

		private PanelTags(Player player, List<ChatTag> tags)
		{
			super("chattags", Locale.getMessage(player, "chat.gui_tags"), (int) Numbers.roundUp(tags.size(), 9), player);

			this.tags = tags;
		}

		private List<ChatTag> tags;

		@Override
		public void onOpen(Player player)
		{
			Tasks.run(() ->
			{
				if (CollectUtils.isEmpty(this.tags)) return;

				for (ChatTag tag : this.tags)
				{
					this.addItem(tag.getItem());
				}
			});
		}

		@Override
		public void onClick(Player player, ItemStack item, int click)
		{
			if (ItemUtils.isEmpty(item)) return;

			ChatTag clicked = null;

			if (item.equals(ModuleTags.INSTANCE.none.getItem()))
			{
				clicked = ModuleTags.INSTANCE.none;
			}
			else
			{
				for (ChatTag tag : ModuleTags.INSTANCE.getTags())
				{
					if (!ItemUtils.isPrettyMuchTheSame(tag.getItem(), item)) continue;

					clicked = tag;

					break;
				}
			}

			if (clicked == null)
			{
				Locale.sendError(player, "chat.tag_error");

				return;
			}

			Hooks.DATA.setPlayerData(player.getUniqueId(), Meta.TAG, clicked.getId());

			Locale.sendSuccess(player, "chat.tag_changed", clicked.getId());

			this.close(player);
		}
	}

	public ChatTag none;

	public final class ChatTag implements Comparable<ChatTag>
	{
		public ChatTag(String id, String text, ItemStack item)
		{
			this.id = id;

			this.text = text;

			this.item = item;
		}

		private final String id;
		public String getId() { return this.id; }

		private final String text;
		public String getText() { return this.text; }

		private final ItemStack item;
		public ItemStack getItem() { return this.item; }

		@Override
		public boolean equals(Object object)
		{
			if (!(object instanceof ChatTag)) return false;

			return ((ChatTag) object).id.equals(this.id);
		}

		@Override
		public String toString()
		{
			return this.id;
		}

		@Override
		public int compareTo(ChatTag other)
		{
			if (other == null) return 1;

			return this.id.compareToIgnoreCase(other.id);
		}
	}

}