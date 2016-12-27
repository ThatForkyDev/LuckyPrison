package com.ulfric.lib.api.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Builder;
import com.ulfric.lib.api.java.Strings;
import org.apache.commons.lang3.Validate;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ItemBuilder class, designed to easily create an {@link ItemStack}
 *
 * @author Adam
 */
public final class ItemBuilder implements Builder<ItemStack> {

	/**
	 * The material of the item
	 */
	private Material type;
	/**
	 * The size of the item
	 */
	private int amount;
	/**
	 * The durability of the item
	 */
	private short durability;
	/**
	 * The material data of the item
	 */
	private MaterialData data;
	/**
	 * The display name of the item
	 */
	private String name;
	/**
	 * The lore of the item
	 */
	private List<String> lore;
	/**
	 * The flags of the item
	 */
	private Set<ItemFlag> flags;
	/**
	 * The enchants of the item
	 */
	private Map<Enchantment, Integer> enchantments;
	/**
	 * The stored enchants of t he item
	 */
	private Map<Enchantment, Integer> storedEnchantments;

	/**
	 * If the item is a book, the title of that book
	 */
	private String title;
	/**
	 * If the item is a book, the author of that book
	 */
	private String author;
	/**
	 * If the item is a book, the pages of that book
	 */
	private List<String> pages;

	/**
	 * If the item is a firework, the power of that firework
	 */
	private int power;
	/**
	 * If the item is a firework, the effects of that firework
	 */
	private List<FireworkEffect> effects;

	/**
	 * If the item is a skull, the owner of that skull
	 */
	private String owner;

	/**
	 * If the item is a map, the scaling status of that map
	 */
	private boolean scaling;

	/**
	 * If the item is leather armor, the color of that armor
	 */
	private Color color;

	/**
	 * If the item is a banner, the base color of that banner
	 */
	private DyeColor bannerColor;
	/**
	 * If the item is a banner, the patterns of that banner
	 */
	private List<Pattern> patterns;

	/** */
	private PotionEffectType effect;
	/** */
	private List<PotionEffect> potionEffects;

	/**
	 * Build the ItemBuilder!
	 * <p>
	 * Note; if this has already been
	 * built, it will throw an error
	 *
	 * @return The item built
	 */
	@Override
	public ItemStack build()
	{
		Assert.isFalse(this.type == Material.AIR, "The item cannot be air!");

		ItemStack item = new ItemStack(this.type, Math.max(this.amount, 1), this.durability);

		if (this.data != null)
		{
			item.setData(this.data);
		}

		ItemMeta meta = item.getItemMeta();

		if (this.name != null)
		{
			meta.setDisplayName(this.name);
		}

		if (!CollectUtils.isEmpty(this.lore))
		{
			meta.setLore(this.lore);
		}

		if (!CollectUtils.isEmpty(this.flags))
		{
			for (ItemFlag flag : this.flags)
			{
				meta.addItemFlags(flag);
			}
		}

		if (!CollectUtils.isEmpty(this.enchantments))
		{
			for (Map.Entry<Enchantment, Integer> entry : this.enchantments.entrySet())
			{
				meta.addEnchant(entry.getKey(), entry.getValue(), true);
			}
		}

		if (meta instanceof BookMeta)
		{
			BookMeta bmeta = (BookMeta) meta;

			if (this.title != null)
			{
				bmeta.setTitle(this.title);
			}

			if (this.author != null)
			{
				bmeta.setAuthor(this.author);
			}

			if (this.pages != null)
			{
				bmeta.setPages(this.pages);
			}
		}

		else if (meta instanceof FireworkMeta)
		{
			FireworkMeta fmeta = (FireworkMeta) meta;

			if (this.power > 0)
			{
				fmeta.setPower(this.power);
			}

			if (this.effects != null)
			{
				fmeta.addEffects(this.effects);
			}
		}

		else if (meta instanceof SkullMeta)
		{
			SkullMeta smeta = (SkullMeta) meta;

			if (this.owner != null)
			{
				smeta.setOwner(this.owner);
			}
		}

		else if (meta instanceof MapMeta)
		{
			MapMeta mmeta = (MapMeta) meta;

			if (this.scaling)
			{
				mmeta.setScaling(true);
			}
		}

		else if (meta instanceof LeatherArmorMeta)
		{
			LeatherArmorMeta lmeta = (LeatherArmorMeta) meta;

			if (this.color != null)
			{
				lmeta.setColor(this.color);
			}
		}

		else if (meta instanceof BannerMeta)
		{
			BannerMeta bmeta = (BannerMeta) meta;

			if (this.bannerColor != null)
			{
				bmeta.setBaseColor(this.bannerColor);
			}

			if (this.patterns != null)
			{
				bmeta.setPatterns(this.patterns);
			}
		}

		else if (meta instanceof PotionMeta)
		{
			PotionMeta pmeta = (PotionMeta) meta;

			if (this.effect != null)
			{
				pmeta.setMainEffect(this.effect);
			}

			if (this.potionEffects != null)
			{
				for (PotionEffect effect : this.potionEffects)
				{
					pmeta.addCustomEffect(effect, true);
				}
			}
		}

		else if (meta instanceof EnchantmentStorageMeta)
		{
			EnchantmentStorageMeta esmeta = (EnchantmentStorageMeta) meta;
			if (this.storedEnchantments != null)
			{
				for (Map.Entry<Enchantment, Integer> entry : this.storedEnchantments.entrySet())
				{
					esmeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
				}
			}
		}

		Validate.isTrue(item.setItemMeta(meta), "Failed to set item meta!");

		return item;
	}

	/**
	 * Sets the material of the builder's item.
	 * Note, this is executed instantly so the
	 * item meta won't give us any issues. Must
	 * not be null;
	 *
	 * @param material Change the material of the current item
	 * @return The current ItemBuilder instance
	 */
	public ItemBuilder withType(Material material)
	{
		this.type = Assert.notNull(material, "Material cannot be null!");

		return this;
	}

	/**
	 * Sets the material of the builder's item.
	 * Note, this is executed instantly so the
	 * item meta won't give us any issues. Must
	 * not be null;
	 *
	 * @param pair Change the material and durability of the current item
	 * @return The current ItemBuilder instance
	 */
	public ItemBuilder withType(ItemPair pair)
	{
		Assert.notNull(pair, "ItemPair cannot be null!");

		this.type = pair.getType();

		this.durability = pair.getData() == null ? 0 : pair.getData();

		return this;
	}

	/**
	 * Sets the amount of the builder's item.
	 * Must be greater than 0 [zero].
	 *
	 * @param amount The new amount for the item
	 * @return The current ItemBuilder instance
	 */
	public ItemBuilder withAmount(int amount)
	{
		Assert.isTrue(amount > 0, "The amount must be positive!");

		this.amount = amount;

		return this;
	}

	/**
	 * Sets the durability of the builder's
	 * item. Must be greater than 0 [zero].
	 *
	 * @param durability The new durability for the item
	 * @return The current ItemBuilder instance
	 */
	public ItemBuilder withDurability(short durability)
	{
		this.durability = durability;

		return this;
	}

	/**
	 * Sets the MaterialData of the
	 * item. Must not be null.
	 *
	 * @param data The new {@link MaterialData} for the item
	 * @return The current ItemBuilder instance
	 */
	public ItemBuilder withMaterialData(MaterialData data)
	{
		this.data = Assert.notNull(data, "MaterialData cannot be null!");

		return this;
	}

	/**
	 * Adds an ItemFlag to the
	 * item. Must not be null.
	 *
	 * @param flag The new {@link ItemFlag} for the item
	 * @return The current ItemBuilder instance
	 */
	public ItemBuilder withFlag(ItemFlag flag)
	{
		Assert.notNull(flag);

		if (this.flags == null)
		{
			this.flags = Sets.newHashSet(flag);

			return this;
		}

		this.flags.add(flag);

		return this;
	}

	/**
	 * Sets the display name of the
	 * item. Must not be null or empty.
	 *
	 * @param name The new String for the item name
	 * @return The current ItemBuilder instance
	 */
	public ItemBuilder withName(String name)
	{
		Assert.isNotEmpty(name);

		this.name = name;

		return this;
	}

	/**
	 * Sets the display name of the
	 * item. Must not be null or empty.
	 * Translates color codes with an
	 * '&' symbol.
	 *
	 * @param name The new String for the item name
	 * @return The current ItemBuilder instance
	 */
	public ItemBuilder withColoredName(String name)
	{
		return this.withName(Chat.color(name));
	}

	/**
	 * Adds a line of text to the lore
	 * of the item. Must not be null
	 * or empty.
	 *
	 * @param lore A new string added to the item's lore
	 * @return The current ItemBuilder instance
	 */
	public ItemBuilder withLore(String lore)
	{
		if (lore == null) lore = Strings.BLANK;

		if (this.lore == null)
		{
			this.lore = Lists.newArrayList(lore);

			return this;
		}

		this.lore.add(lore);

		return this;
	}

	/**
	 * Adds multiple lines of text to the
	 * lore of the item. Must not be empty,
	 * and none of the elements can be null.
	 *
	 * @param lore A collection of new strings added to the item's lore
	 * @return The current ItemBuilder instance
	 */
	public ItemBuilder withLore(Collection<String> lore)
	{
		Assert.noneNull(lore);

		if (this.lore == null)
		{
			this.lore = Lists.newArrayList(lore);

			return this;
		}

		this.lore.addAll(lore);

		return this;
	}

	/**
	 * Adds multiple lines of text to the
	 * lore of the item. Must not be empty,
	 * and none of the elements can be null.
	 *
	 * @param lore Vararg of new strings added to the item's lore
	 * @return The current ItemBuilder instance
	 */
	public ItemBuilder withLore(String... lore)
	{
		Assert.noneNull(lore);

		if (this.lore == null)
		{
			this.lore = Lists.newArrayList();
		}
		else
		{
			this.lore.clear();
		}

		for (String loreString : lore)
		{
			Assert.notNull(loreString);

			this.lore.add(loreString);
		}

		return this;
	}

	/**
	 * Adds an enchantment to the item.
	 *
	 * @param enchant The enchant to add. Must not be null.
	 * @param level   The level of the enchant to add. Must be greater than 0.
	 * @return The current ItemBuilder instance
	 */
	public ItemBuilder withEnchant(Enchantment enchant, int level)
	{
		return this.withEnchant(enchant, level, false, false);
	}

	/**
	 * Adds an enchantment to the item.
	 *
	 * @param enchant The enchant to add. Must not be null.
	 * @param level   The level of the enchant to add. Must be greater than 0.
	 * @param fake    Whether or not to make this a fake enchantment
	 * @param roman   If fake is true, this controls the usage of Roman Numerals past 10
	 * @return The current ItemBuilder instance
	 */
	public ItemBuilder withEnchant(Enchantment enchant, int level, boolean fake, boolean roman)
	{
		Assert.notNull(enchant, "The enchantment must not be null!");

		Assert.isTrue(level > 0, "The level must be at least one!");

		if (this.enchantments == null)
		{
			this.enchantments = Maps.newHashMap();
		}

		this.enchantments.put(enchant, level);

		if (!fake) return this;

		this.withFlag(ItemFlag.HIDE_ENCHANTS);

		boolean empty = true;

		if (this.lore == null) { this.lore = Lists.newArrayList(); }
		else { empty = this.lore.isEmpty(); }

		for (Map.Entry<Enchantment, Integer> enchants : this.enchantments.entrySet())
		{
			String name = EnchantUtils.getHiddenName(enchants.getKey());

			String entry = EnchantUtils.ench(enchants.getKey(), enchants.getValue()).toLore(roman);

			if (empty)
			{
				this.lore.add(entry);

				continue;
			}

			boolean set = false;
			for (int x = 0; x < this.lore.size(); x++)
			{
				String lore = this.lore.get(x);

				if (!ChatColor.stripColor(lore).startsWith(name)) continue;

				this.lore.set(x, entry);

				set = true;
			}

			if (set) continue;

			this.lore.add(entry);
		}

		return this;
	}

	public ItemBuilder withStoredEnchant(Enchantment enchant, int level)
	{
		Assert.notNull(enchant, "The enchantment must not be null!");

		Assert.isTrue(level > 0, "The level must be at least one!");

		if (this.storedEnchantments == null)
		{
			this.storedEnchantments = Maps.newHashMap();
		}

		this.storedEnchantments.put(enchant, level);

		return this;
	}

	/**
	 * Applies a title to the current item if it's a book.
	 *
	 * @param title The title to apply to the book. Must not be empty or null.
	 * @return The current ItemBuilder instance
	 */
	public ItemBuilder withTitle(String title)
	{
		//Assert.isInstanceof(BookMeta.class, this.meta);

		Assert.isNotEmpty(title);

		this.title = title;

		return this;
	}

	/**
	 * Applies an author to the current item if it's a book.
	 *
	 * @param author The author who wrote the book. Must not be empty or null.
	 * @return The current ItemBuilder instance
	 */
	public ItemBuilder withAuthor(String author)
	{
		//Assert.isInstanceof(BookMeta.class, this.meta);

		Assert.isNotEmpty(author);

		this.author = author;

		return this;
	}

	/**
	 * Adds a page to the current item if it's a book.
	 *
	 * @param page The page to add to the book. Must not be empty or null.
	 * @return The current ItemBuilder instance
	 */
	public ItemBuilder withPage(String page)
	{
		Assert.isNotEmpty(page);

		if (this.pages != null)
		{
			this.pages.add(page);

			return this;
		}

		this.pages = Lists.newArrayList(page);

		return this;
	}

	public ItemBuilder withPages(String... pages)
	{
		Assert.noneNull(pages);

		if (this.pages == null) this.pages = Lists.newArrayList();

		CollectUtils.addAll(this.pages, pages);

		return this;
	}

	public ItemBuilder withPages(Collection<String> pages)
	{
		Assert.isFalse(pages.isEmpty());

		if (this.pages == null) this.pages = Lists.newArrayList();

		this.pages.addAll(pages);

		return this;
	}

	public ItemBuilder clearPages()
	{
		if (CollectUtils.isEmpty(this.pages)) return this;

		this.pages.clear();

		return this;
	}

	public ItemBuilder withPower(int power)
	{
		Assert.isTrue(power > 0);

		this.power = power;

		return this;
	}

	public ItemBuilder withEffect(FireworkEffect effect)
	{
		Assert.notNull(effect);

		if (this.effects == null) this.effects = Lists.newArrayList();

		this.effects.add(effect);

		return this;
	}

	public ItemBuilder withEffects(Collection<FireworkEffect> effects)
	{
		Assert.noneNull(effects);

		if (this.effects == null)
		{
			if (effects instanceof List)
			{
				this.effects = (List<FireworkEffect>) effects;

				return this;
			}

			this.effects = Lists.newArrayList();
		}

		this.effects.addAll(effects);

		return this;
	}

	public ItemBuilder withOwner(String skullOwner)
	{
		Assert.isNotEmpty(skullOwner);

		this.owner = skullOwner;

		return this;
	}

	public ItemBuilder withScaling()
	{
		this.scaling = true;

		return this;
	}

	public ItemBuilder withArmorColor(int r, int g, int b)
	{
		Assert.isTrue(r >= 0);
		Assert.isTrue(g >= 0);
		Assert.isTrue(b >= 0);

		return this.withArmorColor(Color.fromRGB(r, g, b));
	}

	public ItemBuilder withArmorColor(int rgb)
	{
		Assert.isTrue(rgb >= 0);

		return this.withArmorColor(Color.fromRGB(rgb));
	}

	public ItemBuilder withArmorColor(Color color)
	{
		Assert.notNull(color);

		this.color = color;

		return this;
	}

	public ItemBuilder withBaseColor(DyeColor color)
	{
		Assert.notNull(color);

		this.bannerColor = color;

		return this;
	}

	public ItemBuilder withPattern(Pattern pattern)
	{
		Assert.notNull(pattern);

		if (this.patterns == null) this.patterns = Lists.newArrayList();

		this.patterns.add(pattern);

		return this;
	}

	public ItemBuilder withPatterns(Collection<Pattern> patterns)
	{
		Assert.noneNull(patterns);

		if (this.patterns == null) this.patterns = Lists.newArrayList();

		this.patterns.addAll(patterns);

		return this;
	}

	public ItemBuilder withPrimaryEffect(PotionEffectType type)
	{
		Assert.notNull(type);

		this.effect = type;

		return this;
	}

	public ItemBuilder withPotionEffect(PotionEffect effect)
	{
		Assert.notNull(effect);

		if (this.potionEffects == null) this.potionEffects = Lists.newArrayList();

		this.potionEffects.add(effect);

		return this;
	}

	public ItemBuilder withPotionEffects(Collection<PotionEffect> effects)
	{
		Assert.noneNull(effects);

		if (this.potionEffects == null) this.potionEffects = Lists.newArrayList();

		this.potionEffects.addAll(effects);

		return this;
	}

}
