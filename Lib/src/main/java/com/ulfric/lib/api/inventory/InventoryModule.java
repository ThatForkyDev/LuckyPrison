package com.ulfric.lib.api.inventory;

import com.google.common.collect.*;
import com.ulfric.lib.Lib;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.gui.PanelModule;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Booleans;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.math.NumberTranslator;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.module.Module;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.persist.TryFiles;
import com.ulfric.lib.api.reflect.Reflect;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public final class InventoryModule extends SimpleModule {

	public InventoryModule()
	{
		super("inventory", "Inventory utilities module", "Packet", "1.0.0-REL");

		this.withSubModule(new ItemsModule());
		this.withSubModule(new PanelModule());
	}

	@Override
	public void postEnable()
	{
		InventoryUtils.impl = new InventoryUtils.IInventoryUtils() {
			@Override
			public void clear(Inventory inventory)
			{
				inventory.setContents(new ItemStack[0]);
			}

			@Override
			public int count(Inventory inventory, ItemStack item)
			{
				int count = 0;

				for (ItemStack litem : inventory)
				{
					if (ItemUtils.isEmpty(litem)) continue;

					if (!litem.isSimilar(item)) continue;

					count += litem.getAmount();
				}

				return count;
			}

			@Override
			public boolean hasRoomFor(Inventory inventory, ItemStack item, boolean allowOverflow)
			{
				int amt = 0;
				for (ItemStack litem : inventory)
				{
					if (litem == null) return true;

					if (!litem.isSimilar(item)) continue;

					amt += litem.getMaxStackSize() - litem.getAmount();
				}

				return amt != 0 && (amt > item.getAmount() || allowOverflow);
			}

			@Override
			public boolean isFull(Inventory inventory)
			{
				return inventory.firstEmpty() == -1;
			}

			@Override
			public boolean isEmpty(Inventory inventory)
			{
				for (ItemStack item : inventory.getContents())
				{
					if (item == null) continue;

					return false;
				}
				return true;
			}

			@Override
			public boolean giveOrDrop(Player player, ItemStack... items)
			{
				Collection<ItemStack> leftover = player.getInventory().addItem(items).values();

				if (leftover.isEmpty()) return true;

				Location location = player.getLocation();
				World world = location.getWorld();

				leftover.forEach(leftoverItem -> world.dropItem(location, leftoverItem));

				return false;
			}
		};
	}

	private final class ItemsModule extends SimpleModule {
		ItemsModule()
		{
			super("items", "Item utilities module", "Packet", "1.0.0-REL");

			this.withSubModule(new MaterialsModule());
			this.withSubModule(new EnchantsModule());
		}

		@Override
		public void postEnable()
		{
			ItemUtils.impl = new ItemUtils.IItemUtils() {
				private final ItemStack blank = new ItemStack(Material.AIR); // TODO make immutable
				private final ItemStack worthless = new ItemStack(Material.DIRT); // TODO make immutable
				private final Map<ChatColor, DyeColor> colors;

				{
					ImmutableMap.Builder<ChatColor, DyeColor> builder = ImmutableMap.builder();

					builder.put(ChatColor.WHITE, DyeColor.WHITE);
					builder.put(ChatColor.YELLOW, DyeColor.YELLOW);
					builder.put(ChatColor.GRAY, DyeColor.SILVER);
					builder.put(ChatColor.BLACK, DyeColor.BLACK);
					builder.put(ChatColor.GOLD, DyeColor.ORANGE);
					builder.put(ChatColor.DARK_RED, DyeColor.RED);
					builder.put(ChatColor.RED, DyeColor.PINK);
					builder.put(ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA);
					builder.put(ChatColor.DARK_PURPLE, DyeColor.PURPLE);
					builder.put(ChatColor.DARK_BLUE, DyeColor.BLUE);
					builder.put(ChatColor.BLUE, DyeColor.BLUE);
					builder.put(ChatColor.DARK_AQUA, DyeColor.CYAN);
					builder.put(ChatColor.AQUA, DyeColor.LIGHT_BLUE);
					builder.put(ChatColor.DARK_GRAY, DyeColor.GRAY);
					builder.put(ChatColor.GREEN, DyeColor.LIME);
					builder.put(ChatColor.DARK_GREEN, DyeColor.GREEN);

					this.colors = builder.build();
				}

				@Override
				public boolean is(ItemStack item, Material material)
				{
					if (item == null) return material == null;

					return item.getType() == material;
				}

				@Override
				public DyeColor dyeFromChat(ChatColor color)
				{
					DyeColor match = this.colors.get(color);

					return match == null ? DyeColor.WHITE : match;
				}

				@Override
				public void replacePagePlaceholders(ItemStack item, Player player)
				{
					BookMeta meta = (BookMeta) item.getItemMeta();

					String name = player.getName();

					List<String> pages = Lists.newArrayList();

					for (String page : meta.getPages())
					{
						pages.add(page.replace(Strings.PLAYER, name));
					}

					meta.setPages(pages);

					item.setItemMeta(meta);
				}

				@Override
				public boolean isEmpty(ItemStack item)
				{
					return (item == null || this.is(item, Material.AIR));
				}

				@Override
				public boolean hasNameAndLore(ItemStack item)
				{
					if (!item.hasItemMeta()) return false;

					ItemMeta meta = item.getItemMeta();

					return meta.hasDisplayName() && meta.hasLore();
				}

				@Override
				public String toString(ItemStack item)
				{
					if (item == null) return Strings.BLANK;

					StringBuilder builder = new StringBuilder(ItemPart.build(item.getType(), ItemPart.MATERIAL));

					if (item.getDurability() != 0)
					{
						builder.append(ItemPart.build(item.getDurability(), ItemPart.DURABILITY));
					}

					if (item.getAmount() > 1)
					{
						builder.append(ItemPart.build(item.getAmount(), ItemPart.AMOUNT));
					}

					if (item.hasItemMeta())
					{
						ItemMeta meta = item.getItemMeta();

						if (meta.hasDisplayName())
						{
							builder.append(ItemPart.build(meta.getDisplayName(), ItemPart.NAME));
						}

						if (meta.hasLore())
						{
							for (String lore : meta.getLore())
							{
								builder.append(ItemPart.build(lore, ItemPart.LORE));
							}
						}

						if (meta.hasEnchants())
						{
							for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet())
							{
								builder.append(ItemPart.build(Strings.format("{0}.{1}", entry.getKey().getName(), entry.getValue()), ItemPart.ENCHANT));
							}
						}

						for (ItemFlag flag : meta.getItemFlags())
						{
							builder.append(ItemPart.build(flag, ItemPart.ITEM_FLAG));
						}

						if (meta instanceof BookMeta)
						{
							BookMeta bmeta = (BookMeta) meta;

							if (bmeta.hasAuthor())
							{
								builder.append(ItemPart.build(bmeta.getAuthor(), ItemPart.BOOK_AUTHOR));
							}

							if (bmeta.hasTitle())
							{
								builder.append(ItemPart.build(bmeta.getTitle(), ItemPart.BOOK_TITLE));
							}

							if (bmeta.hasPages())
							{
								for (String page : bmeta.getPages())
								{
									builder.append(ItemPart.build(page, ItemPart.BOOK_PAGE));
								}
							}
						}

						else if (meta instanceof FireworkMeta)
						{
							FireworkMeta fmeta = (FireworkMeta) meta;

							builder.append(ItemPart.build(fmeta.getPower(), ItemPart.FIREWORK_POWER));

							if (fmeta.hasEffects())
							{
								for (FireworkEffect effect : fmeta.getEffects())
								{
									builder.append(ItemPart.build(Strings.format("{0}.{1}.{2}.{3}.{4}", effect.getType(), effect.hasTrail(), effect.hasFlicker(), this.colorsToString(effect.getColors()), this.colorsToString(effect.getFadeColors())), ItemPart.FIREWORK_EFFECT));
								}
							}
						}

						else if (meta instanceof SkullMeta)
						{
							SkullMeta smeta = (SkullMeta) meta;
							if (smeta.hasOwner())
							{
								builder.append(ItemPart.build(smeta.getOwner(), ItemPart.SKULL_OWNER));
							}
						}

						else if (meta instanceof MapMeta)
						{
							MapMeta mmeta = (MapMeta) meta;

							if (mmeta.isScaling())
							{
								builder.append(ItemPart.build(mmeta.isScaling(), ItemPart.MAP_SCALING));
							}
						}

						else if (meta instanceof LeatherArmorMeta)
						{
							LeatherArmorMeta lmeta = (LeatherArmorMeta) meta;

							builder.append(ItemPart.build(lmeta.getColor().asRGB(), ItemPart.ARMOR_COLOR));
						}

						else if (meta instanceof BannerMeta)
						{
							BannerMeta bmeta = (BannerMeta) meta;

							builder.append(ItemPart.build(bmeta.getBaseColor(), ItemPart.BANNER_BASE));

							for (Pattern pattern : bmeta.getPatterns())
							{
								builder.append(ItemPart.build(Strings.format("{0}.{1}", pattern.getPattern(), pattern.getColor()), ItemPart.BANNER_PATTERN));
							}
						}

						else if (meta instanceof EnchantmentStorageMeta)
						{
							EnchantmentStorageMeta esmeta = (EnchantmentStorageMeta) meta;
							for (Map.Entry<Enchantment, Integer> entry : esmeta.getStoredEnchants().entrySet())
							{
								builder.append(ItemPart.build(Strings.format("{0}.{1}", entry.getKey().getName(), entry.getValue()), ItemPart.STORED_ENCHANT));
							}
						}
					}

					return Chat.serializeColor(builder.toString().trim());
				}

				@Override
				public ItemStack fromString(String item)
				{
					ItemBuilder builder = new ItemBuilder();

					if (item.startsWith("["))
					{
						item = item.substring(1, item.length() - 1);
					}

					String[] parts = item.split("\\s+");

					for (String part : parts)
					{
						part = Strings.space(part);

						if (part.startsWith(ItemPart.MATERIAL))
						{
							part = part.substring(ItemPart.MATERIAL.length(), part.length());

							ItemPair pair = MaterialUtils.pair(part);

							if (pair == null)
							{
								InventoryModule.this.log("ItemPair '{0}' not found", part);

								return ItemUtils.worthless();
							}

							builder.withType(pair);
						}
						else if (part.startsWith(ItemPart.AMOUNT))
						{
							part = part.substring(ItemPart.AMOUNT.length(), part.length());

							builder.withAmount(Optional.ofNullable(Numbers.parseInteger(part)).orElse(1));
						}
						else if (part.startsWith(ItemPart.DURABILITY))
						{
							part = part.substring(ItemPart.DURABILITY.length(), part.length());

							builder.withDurability(Optional.ofNullable(Numbers.parseShort(part)).orElse((short) 1));
						}
						else if (part.startsWith(ItemPart.NAME))
						{
							part = part.substring(ItemPart.NAME.length(), part.length());

							builder.withName(Chat.color(part));
						}
						else if (part.startsWith(ItemPart.LORE))
						{
							part = part.substring(ItemPart.LORE.length(), part.length());

							builder.withLore(Chat.color(part));
						}
						else if (part.startsWith(ItemPart.ENCHANT))
						{
							part = part.substring(ItemPart.ENCHANT.length(), part.length());

							Enchant enchant = EnchantUtils.parse(part);

							if (enchant.getEnchant() == null)
							{
								InventoryModule.this.log("Bad enchant: {0}", part);

								continue;
							}

							builder.withEnchant(enchant.getEnchant(), Optional.ofNullable(enchant.getLevel()).orElse(1), true, false);
						}
						else if (part.startsWith(ItemPart.STORED_ENCHANT))
						{
							part = part.substring(ItemPart.STORED_ENCHANT.length(), part.length());

							Enchant enchant = EnchantUtils.parse(part);

							if (enchant.getEnchant() == null)
							{
								InventoryModule.this.log("Bad enchant: {0}", part);

								continue;
							}

							builder.withStoredEnchant(enchant.getEnchant(), Optional.ofNullable(enchant.getLevel()).orElse(1));
						}
						else if (part.startsWith(ItemPart.ITEM_FLAG))
						{
							part = part.substring(ItemPart.ITEM_FLAG.length(), part.length());

							builder.withFlag(ItemFlag.valueOf(part.toUpperCase()));
						}
						else if (part.startsWith(ItemPart.BOOK_TITLE))
						{
							part = part.substring(ItemPart.BOOK_TITLE.length(), part.length());

							builder.withTitle(Chat.color(part));
						}
						else if (part.startsWith(ItemPart.BOOK_AUTHOR))
						{
							part = part.substring(ItemPart.BOOK_AUTHOR.length(), part.length());

							builder.withAuthor(Chat.color(part));
						}
						else if (part.startsWith(ItemPart.BOOK_PAGE))
						{
							part = part.substring(ItemPart.BOOK_PAGE.length(), part.length());

							builder.withPage(Chat.color(part.replace(Strings.FAKE_LINEBREAK, "\n")));
						}
						else if (part.startsWith(ItemPart.FIREWORK_POWER))
						{
							part = part.substring(ItemPart.FIREWORK_POWER.length(), part.length());

							builder.withPower(Optional.ofNullable(Numbers.parseInteger(part)).orElse(1));
						}
						else if (part.startsWith(ItemPart.FIREWORK_EFFECT))
						{
							part = part.substring(ItemPart.FIREWORK_EFFECT.length(), part.length());

							String[] effects = part.split("\\.");

							FireworkEffect.Builder effect = FireworkEffect.builder();

							effect.with(FireworkEffect.Type.valueOf(effects[0]));

							if (Booleans.parseBoolean(effects[1])) effect.withTrail();

							if (Booleans.parseBoolean(effects[2])) effect.withFlicker();

							for (String color : effects[3].split(","))
							{
								String[] colors = color.split("\\|");

								int length = colors.length - 1;

								effect.withColor(Color.fromRGB(Optional.ofNullable(Numbers.parseInteger(colors[0])).orElse(0), Optional.ofNullable(Numbers.parseInteger(colors[Math.min(1, length)])).orElse(0), Optional.ofNullable(Numbers.parseInteger(colors[Math.min(2, length)])).orElse(0)));
							}

							for (String color : effects[4].split(","))
							{
								String[] colors = color.split(java.util.regex.Pattern.quote("|"));

								int length = colors.length;

								if (length == 0)
								{
									effect.withFade(Color.fromRGB(0, 0, 0));
								}
								else
								{
									int one = Optional.ofNullable(Numbers.parseInteger(colors[0])).orElse(0);
									int two = Optional.ofNullable(Numbers.parseInteger(colors[Math.min(1, length)])).orElse(0);
									int three = Optional.ofNullable(Numbers.parseInteger(colors[Math.min(2, length)])).orElse(0);

									effect.withFade(Color.fromRGB(one, two, three));
								}
							}

							builder.withEffect(effect.build());
						}
						else if (part.startsWith(ItemPart.SKULL_OWNER))
						{
							part = part.substring(ItemPart.SKULL_OWNER.length(), part.length());

							builder.withOwner(part);
						}
						else if (part.startsWith(ItemPart.MAP_SCALING))
						{
							part = part.substring(ItemPart.MAP_SCALING.length(), part.length());

							if (!Booleans.parseBoolean(part)) continue;

							builder.withScaling();
						}
						else if (part.startsWith(ItemPart.ARMOR_COLOR))
						{
							part = part.substring(ItemPart.ARMOR_COLOR.length(), part.length());

							builder.withArmorColor(Color.fromRGB(Optional.ofNullable(Numbers.parseInteger(part)).orElse(1)));
						}
						else if (part.startsWith(ItemPart.BANNER_BASE))
						{
							part = part.substring(ItemPart.BANNER_BASE.length(), part.length());

							builder.withBaseColor(this.getDyeColor(part));
						}
						else if (part.startsWith(ItemPart.BANNER_PATTERN))
						{
							part = part.substring(ItemPart.BANNER_PATTERN.length(), part.length());

							String[] banner = part.split("\\.");

							builder.withPattern(new Pattern(this.getDyeColor(banner[1]), PatternType.valueOf(banner[0])));
						}
						else if (part.startsWith(ItemPart.POTION_TYPE))
						{
							part = part.substring(ItemPart.POTION_TYPE.length(), part.length());

							builder.withPrimaryEffect(PotionEffectType.getByName(part));
						}
						else if (part.startsWith(ItemPart.POTION_EFFECT))
						{
							part = part.substring(ItemPart.POTION_EFFECT.length(), part.length());

							String[] eff = part.split("\\.");

							builder.withPotionEffect(new PotionEffect(PotionEffectType.getByName(eff[0]), Numbers.parseInteger(eff[1]), Numbers.parseInteger(eff[2]), Booleans.parseBoolean(eff[3]), Booleans.parseBoolean(eff[4])));
						}
					}

					return builder.build();
				}

				@Override
				public String colorsToString(List<Color> colors)
				{
					int size = colors.size();

					StringBuilder scolors = new StringBuilder();

					for (int x = 0; x < size; x++)
					{
						scolors.append(colors.get(x));

						if (x == size - 1) continue;

						scolors.append(',');
					}

					return scolors.toString();
				}

				@Override
				public void decrementHand(Player player)
				{
					ItemStack item = player.getItemInHand();

					int amount = item.getAmount() - 1;

					if (amount > 0)
					{
						item.setAmount(amount);

						return;
					}

					player.setItemInHand(this.blank);
				}

				@Override
				public ItemStack blank()
				{
					return this.blank;
				}

				@Override
				public ItemStack worthless()
				{
					return this.worthless;
				}

				private DyeColor getDyeColor(String color)
				{
					if (color == null) return DyeColor.BLACK;

					return DyeColor.valueOf(color);
				}
			};
		}

		@Override
		public void postDisable()
		{
			ItemUtils.impl = ItemUtils.IItemUtils.EMPTY;
		}
	}

	private static final class EnchantsModule extends SimpleModule {
		EnchantsModule()
		{
			super("enchants", "Enchant utilities module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			EnchantUtils.impl = new EnchantUtils.IEnchantUtils() {
				private final Field field = Reflect.getPrivateField(Enchantment.class, "acceptingNew");
				private Map<Enchantment, EnchantData> data;
				private Map<String, EnchantData> unfound;

				{
					this.load();
				}

				@Override
				public Module getModule()
				{
					return EnchantsModule.this;
				}

				@Override
				public ItemMeta addFakeEnchantMax(ItemMeta meta, Enchant enchant, boolean roman, int max)
				{
					Enchantment enchantment = enchant.getEnchant();
					int level = enchant.getLevel();

					if (meta.hasEnchants())
					{
						enchant:
						{
							if (!meta.hasEnchant(enchantment)) break enchant;

							level += meta.getEnchantLevel(enchantment);
						}
					}

					level = Math.min(level, max);

					meta.addEnchant(enchantment, level, true);

					List<String> lores = meta.getLore();
					if (!meta.hasLore())
					{
						lores = Lists.newArrayListWithExpectedSize(meta.getEnchants().size());

						for (Map.Entry<Enchantment, Integer> enchants : meta.getEnchants().entrySet())
						{
							lores.add(Enchant.of(enchants.getKey(), enchants.getValue()).toLore(roman || enchants.getValue() <= 10));
						}
					}
					else
					{
						for (Map.Entry<Enchantment, Integer> enchants : meta.getEnchants().entrySet())
						{
							String name = this.getHiddenName(enchants.getKey());
							String entry = Enchant.of(enchants.getKey(), enchants.getValue()).toLore(roman || enchants.getValue() <= 10);

							boolean set = false;

							int x = -1;
							for (String lore : lores)
							{
								x++;

								if (!ChatColor.stripColor(lore).startsWith(name)) continue;

								lores.set(x, entry);

								set = true;

								break;
							}

							if (set) continue;

							lores.add(entry);
						}
					}

					meta.setLore(lores);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

					return meta;
				}

				@Override
				public Enchant ench(Enchantment enchant, Integer level)
				{
					return Enchant.of(enchant, level);
				}

				@Override
				public void setAcceptingNewEnchantsMutable(boolean lock)
				{
					Reflect.trySet(this.field, null, lock);
				}

				@Override
				public void registerCustomEnchantments(Enchantment... enchants)
				{
					for (Enchantment enchant : enchants)
					{
						this.registerCustomEnchant(enchant);
					}

					if (CollectUtils.isEmpty(this.unfound))
					{
						this.unfound = null;

						return;
					}

					Iterator<Map.Entry<String, EnchantData>> iter = this.unfound.entrySet().iterator();

					while (iter.hasNext())
					{
						Map.Entry<String, EnchantData> entry = iter.next();

						Enchantment enchant = Enchantment.getByName(entry.getKey());

						if (enchant == null) continue;

						entry.getValue().setEnchant(enchant);

						this.data.put(enchant, entry.getValue());

						iter.remove();
					}
				}

				@Override
				public String getHiddenName(Enchantment enchant)
				{
					EnchantData data = this.data.get(enchant);

					if (data == null)
					{
						data = new EnchantData(enchant);

						this.data.put(enchant, data);

						data.addAlias(enchant.getName().toLowerCase());
					}

					return WordUtils.capitalizeFully(data.getAliases().get(0));
				}

				@SuppressWarnings("deprecation")
				@Override
				public Enchantment getEnchantById(int id)
				{
					return Enchantment.getById(id);
				}

				@Override
				public Enchantment getEnchantByName(String string)
				{
					string = ChatColor.stripColor(string);

					Integer number = Numbers.parseInteger(string);

					if (number != null)
					{
						return this.getEnchantById(number);
					}

					string = string.toLowerCase();

					for (Map.Entry<Enchantment, EnchantData> entry : this.data.entrySet())
					{
						if (entry.getKey().getName().toLowerCase().equals(string))
						{
							return entry.getKey();
						}

						List<String> aliases = entry.getValue().getAliases();

						if (CollectUtils.isEmpty(aliases)) continue;

						for (String alias : aliases)
						{
							if (!alias.equals(string)) continue;

							return entry.getKey();
						}
					}

					EnchantsModule.this.log("MISSING ENCHANT: " + string);

					return null;
				}

				@Override
				public Enchant parse(String string)
				{
					String[] split = string.split("(\\:|\\.)");

					Enchantment enchant = this.getEnchantByName(split[0]);

					if (split.length == 1) return Enchant.of(enchant, 0);

					return Enchant.of(enchant, Numbers.parseInteger(split[1]));
				}

				@Override
				public int getMaxLevel(Enchantment enchant)
				{
					EnchantData data = this.data.get(enchant);

					if (data == null) return 0;

					int max = data.getMax();

					if (max == -1) return enchant.getMaxLevel();

					return max;
				}

				@Override
				public int getLevel(ItemStack item, Enchantment enchant)
				{
					if (ItemUtils.isEmpty(item)) return 0;

					return item.getEnchantmentLevel(enchant);
				}

				@Override
				public ItemMeta addFakeEnchant(ItemMeta meta, Enchant enchant, boolean roman)
				{
					return this.addFakeEnchantMax(meta, enchant, roman, Short.MAX_VALUE);
				}

				@Override
				public Set<String> getFakeEnchantsAsLore(ItemStack item)
				{
					if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return ImmutableSet.of();

					return item.getItemMeta().getLore().stream()
							.filter(string -> string.startsWith(ChatColor.GRAY + Strings.BLANK))
							.filter(string -> {
								String[] split = string.split("\\s+");
								return split.length == 2 && this.getEnchantByName(split[0]) != null;
							})
							.collect(Collectors.toSet());
				}

				@Override
				public Map<Enchantment, Integer> getFakeEnchants(ItemStack item)
				{
					if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return ImmutableMap.of();

					Map<Enchantment, Integer> enchants = Maps.newHashMap();

					ItemMeta meta = item.getItemMeta();
					for (String string : meta.getLore())
					{
						if (!string.startsWith(ChatColor.GRAY + Strings.BLANK)) continue;

						String[] split = string.split("\\s+");

						if (split.length != 2) continue;

						Enchantment enchant = this.getEnchantByName(split[0]);

						if (enchant == null) continue;

						enchants.put(enchant, NumberTranslator.roman(split[1]).intValue());
					}

					return enchants;
				}

				@Override
				public int getWeight(ItemStack item)
				{
					if (!item.hasItemMeta()) return 0;

					Set<Map.Entry<Enchantment, Integer>> enchants = item.getEnchantments().entrySet();

					if (enchants.isEmpty()) return 0;

					int weight = 0;
					for (Map.Entry<Enchantment, Integer> enchant : enchants)
					{
						weight += enchant.getValue();
					}

					if (weight == 0) return 0;

					return (int) (weight / Math.max(enchants.size(), 2.3));
				}

				private void load()
				{
					File folder = Lib.get().getDataFolder();

					File file = new File(folder, "enchants.txt");

					if (!file.exists())
					{
						EnchantsModule.this.log("Missing resource: {0}", "enchants.txt");

						EnchantsModule.this.disable();

						return;
					}

					this.data = Maps.newHashMap();
					this.unfound = Maps.newHashMap();

					for (String string : TryFiles.readLines(file))
					{
						String[] parts = string.split(":");

						String enchantName = parts[0].trim().toUpperCase();

						String alias = parts[1].trim();

						Enchantment enchant = Enchantment.getByName(enchantName);

						EnchantData data = null;

						if (enchant == null)
						{
							if (this.unfound == null)
							{
								this.unfound = Maps.newHashMap();
							}
							else
							{
								data = this.unfound.get(enchantName);
							}

							if (data == null)
							{
								data = new EnchantData(null);

								this.unfound.put(enchantName, data);
							}

							data.addAlias(alias);

							continue;
						}

						data = this.data.get(enchant);

						if (data == null)
						{
							data = new EnchantData(enchant);

							this.data.put(enchant, data);
						}

						data.addAlias(alias);
					}

					file = new File(folder, "levels.txt");

					if (!file.exists())
					{
						EnchantsModule.this.log("Missing resource: {0}", "levels.txt");

						EnchantsModule.this.disable();

						return;
					}

					for (String string : TryFiles.readLines(file))
					{
						String[] parts = string.split(":");

						Enchantment enchant = this.getEnchantByName(parts[0].trim());

						if (enchant == null) continue;

						EnchantData data = this.data.get(enchant);

						if (data == null)
						{
							data = new EnchantData(enchant);

							this.data.put(enchant, data);
						}

						data.setMax(Integer.parseInt(parts[1].trim()));
					}
				}

				private void registerCustomEnchant(Enchantment enchant)
				{
					Assert.notNull(enchant);

					@SuppressWarnings("deprecation")
					int id = enchant.getId();
					if (id < 200)
					{
						throw new IllegalArgumentException(Strings.format("Custom enchants must have IDs of at least 200! {0} has an ID of {1}.", enchant.getName(), id));
					}

					Enchantment.registerEnchantment(enchant);
				}
			};
		}

		@Override
		public void postDisable()
		{
			EnchantUtils.impl = EnchantUtils.IEnchantUtils.EMPTY;
		}
	}

	@SuppressWarnings("deprecation")
	private static final class MaterialsModule extends SimpleModule {
		MaterialsModule()
		{
			super("materials", "Material utilities utilities", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			MaterialUtils.impl = new MaterialUtils.IMaterialUtils() {
				private final Field field = Reflect.getPrivateField(Item.class, "maxStackSize");

				private final Set<Integer> immutableSizes = Sets.newHashSet(PlayerConnection.getInvalidItems());
				private final Map<Material, WeaponType> weapons = Maps.newEnumMap(Material.class);
				private Map<String, ItemPair> items;

				{
					this.immutableSizes.addAll(Sets.newHashSet(124, 149, 150, 176, 177, 178, 181, 193, 194, 195, 196, 197));

					this.load();
				}

				private void load()
				{
					File file = new File(Lib.get().getDataFolder(), "materials.txt");

					if (!file.exists())
					{
						MaterialsModule.this.log("Missing resource: materials.txt");

						MaterialsModule.this.disable();

						return;
					}

					Map<String, ItemPair> pairs = new LinkedHashMap<>();

					for (String string : TryFiles.readLines(file))
					{
						String[] parts = string.split(",");

						pairs.put(parts[0].trim().toLowerCase(), this.pair(Material.getMaterial(parts[1].trim().toUpperCase()), Integer.valueOf(parts[2].trim())));
					}
					// Add every Material#toString as a pair to itself.
					// This is not only convenient, but it also is required for the serialization of items,
					// as they are serialized using Material#toString
					for (Material material : Material.values())
					{
						pairs.put(material.toString().toLowerCase(), this.pair(material));

						if (material == Material.AIR)
						{
							this.weapons.put(Material.AIR, WeaponType.FIST);

							continue;
						}

						if (material == Material.POTION)
						{
							this.weapons.put(Material.POTION, WeaponType.POTION);

							continue;
						}

						String name = material.name().toLowerCase();

						if (name.contains("sword"))
						{
							this.weapons.put(material, WeaponType.SWORD);
						}

						else if (name.contains("axe"))
						{
							this.weapons.put(material, WeaponType.AXE);
						}

						else if (name.contains("bow"))
						{
							this.weapons.put(material, WeaponType.BOW);
						}

						else
						{
							this.weapons.put(material, WeaponType.UNKNOWN);
						}
					}

					this.items = pairs;
				}

				@Override
				public WeaponType getWeaponType(Material material)
				{
					WeaponType type = this.weapons.get(material);

					if (type != null) return type;

					return WeaponType.UNKNOWN;
				}

				@Override
				public Material compress(ItemStack item)
				{
					switch (item.getType())
					{
						case COAL:
							return Material.COAL_BLOCK;

						case IRON_INGOT:
							return Material.IRON_BLOCK;

						case GOLD_INGOT:
							return Material.GOLD_BLOCK;

						case DIAMOND:
							return Material.DIAMOND_BLOCK;

						case EMERALD:
							return Material.EMERALD_BLOCK;

						case REDSTONE:
							return Material.REDSTONE_BLOCK;

						case QUARTZ:
							return Material.QUARTZ_BLOCK;

						case SLIME_BALL:
							return Material.SLIME_BLOCK;

						case WHEAT:
							return Material.HAY_BLOCK;

						case INK_SACK:
							if (item.getDurability() != 4) return null;

							return Material.LAPIS_BLOCK;

						default:
							return null;
					}
				}

				@Override
				public Material smelt(Material material)
				{
					switch (material)
					{
						case COAL_ORE:
							return Material.COAL;

						case DIAMOND_ORE:
							return Material.DIAMOND;

						case EMERALD_ORE:
							return Material.EMERALD;

						case GLOWING_REDSTONE_ORE:
						case REDSTONE_ORE:
							return Material.REDSTONE;

						case GOLD_ORE:
							return Material.GOLD_INGOT;

						case IRON_ORE:
							return Material.IRON_INGOT;

						case QUARTZ_ORE:
							return Material.QUARTZ;

						default:
							return null;
					}
				}

				@Override
				public String getName(ItemPair pair)
				{
					for (Map.Entry<String, ItemPair> entry : this.items.entrySet())
					{
						if (!entry.getValue().equals(pair)) continue;

						return entry.getKey();
					}

					return Strings.format("UNKNOWN [{0}:{1}]", WordUtils.capitalizeFully(pair.getType().name().toLowerCase()), pair.getData() == null ? 0 : pair.getData().intValue());
				}

				@Override
				public void setMaxStackSizeMutable(boolean lock)
				{
					this.field.setAccessible(lock);
				}

				@Override
				public void setMaxStackSize(Material material, int size)
				{
					int id = material.getId();

					if (this.immutableSizes.contains(id)) return;

					Reflect.trySet(this.field, Item.getById(id), size);
				}

				@Override
				public boolean isBlock(Material material)
				{
					return material != null && material.isBlock();
				}

				@Override
				public ItemPair pair(Block block)
				{
					return new ItemPair(block.getType(), (short) block.getData());
				}

				@Override
				public ItemPair pair(ItemStack item)
				{
					return new ItemPair(item.getType(), item.getDurability());
				}

				@Override
				public ItemPair pair(Material type)
				{
					return new ItemPair(type, (short) 0);
				}

				@Override
				public ItemPair pair(Material type, int data)
				{
					return new ItemPair(type, (short) data);
				}

				@Override
				public ItemPair pair(String name)
				{
					return this.items.get(name.toLowerCase());
				}

				@Override
				public WeightedItemPair weight(int weight, ItemPair pair)
				{
					return new WeightedItemPair(weight, pair);
				}
			};
		}

		@Override
		public void postDisable()
		{
			MaterialUtils.impl = MaterialUtils.IMaterialUtils.EMPTY;
		}
	}	@Override
	public void postDisable()
	{
		InventoryUtils.impl = InventoryUtils.IInventoryUtils.EMPTY;
	}




}
