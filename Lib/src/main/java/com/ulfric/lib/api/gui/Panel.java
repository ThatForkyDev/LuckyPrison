package com.ulfric.lib.api.gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.ulfric.lib.api.collect.Sets;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.server.Events;

public class Panel implements Inventory, Cloneable {

	private final String identifier;
	private final boolean flag;
	private final int[] allowed;
	private final Inventory inventory;
	private final Set<Player> players;
	private boolean noneAllowed;
	private boolean permanent;
	private Player owner;

	public Panel(String identifier, String title, int size, Player player, int... allowedClicks)
	{
		this(identifier, title, size, player, false, allowedClicks);
	}

	public Panel(String identifier, String title, int size, Player player, boolean flag, int... allowedClicks)
	{
		this(identifier, title, size, player, flag, true, allowedClicks);
	}

	public Panel(String identifier, String title, int size, Player player, boolean flag, boolean owned, int... allowedClicks)
	{
		this.identifier = identifier;
		this.inventory = Bukkit.createInventory(owned ? player : null, size, title);
		this.players = Sets.newWeakSet();
		this.flag = flag;
		this.allowed = Arrays.copyOf(allowedClicks, allowedClicks.length);

		if (player == null)
		{
			this.permanent = true;
			return;
		}

		this.open(player);
		this.owner = player;
	}

	public String getIdentifier()
	{
		return this.identifier;
	}

	public boolean isSame(Panel panel)
	{
		return panel.identifier.equals(this.identifier);
	}

	public void allowNone()
	{
		this.noneAllowed = true;
	}

	public boolean isAllowed(int click)
	{
		if (this.noneAllowed) return false;

		boolean contains = ArrayUtils.contains(this.allowed, click);

		return this.flag != contains;
	}

	public boolean isPermanent()
	{
		return this.permanent;
	}

	public Inventory getBukkitInventory()
	{
		return this.inventory;
	}

	public Set<Player> getPlayers()
	{
		return this.players;
	}

	public Player getOwner()
	{
		return this.owner;
	}

	public final void open(Player player)
	{
		if (Events.call(new PanelOpenEvent(player)).isCancelled()) return;

		this.players.add(player);

		Metadata.apply(player, "_ulf_panel", this);

		player.openInventory(this.inventory);

		this.onOpen(player);
	}

	public void onOpen(Player player)
	{
	}

	public final void close(Player player)
	{
		if (!this.players.remove(player)) return;

		player.closeInventory();

		Metadata.remove(player, "_ulf_panel");

		this.onClose(player);
	}

	public void onClose(Player player)
	{
	}

	public void onClick(Player player, ItemStack item, int click)
	{
	}

	public void onAllowedClick(Player player, ItemStack item, int click)
	{
	}

	public void onSwap(Player player, ItemStack newItem, ItemStack oldItem, int click)
	{
	}

	@Override
	public int getSize()
	{
		return this.inventory.getSize();
	}

	@Override
	public int getMaxStackSize()
	{
		return this.inventory.getMaxStackSize();
	}

	@Override
	public void setMaxStackSize(int arg0)
	{
		this.inventory.setMaxStackSize(arg0);
	}

	@Override
	public String getName()
	{
		return this.inventory.getName();
	}

	@Override
	public ItemStack getItem(int arg0)
	{
		return this.inventory.getItem(arg0);
	}

	@Override
	public void setItem(int arg0, ItemStack arg1)
	{
		this.inventory.setItem(arg0, arg1);
	}

	@Override
	public HashMap<Integer, ItemStack> addItem(ItemStack... items) throws IllegalArgumentException
	{
		return this.inventory.addItem(items);
	}

	@Override
	public HashMap<Integer, ItemStack> removeItem(ItemStack... arg0) throws IllegalArgumentException
	{
		return this.inventory.removeItem(arg0);
	}

	@Override
	public ItemStack[] getContents()
	{
		return this.inventory.getContents();
	}

	@Override
	public void setContents(ItemStack[] arg0) throws IllegalArgumentException
	{
		this.inventory.setContents(arg0);
	}

	@Override
	public boolean contains(int arg0)
	{
		throw new UnsupportedOperationException("You must use a material or item.");
	}

	@Override
	public boolean contains(Material arg0) throws IllegalArgumentException
	{
		return this.inventory.contains(arg0);
	}

	@Override
	public boolean contains(ItemStack arg0)
	{
		return this.inventory.contains(arg0);
	}

	@Override
	public boolean contains(int arg0, int arg1)
	{
		throw new UnsupportedOperationException("You must use a material or item.");
	}

	@Override
	public boolean contains(Material arg0, int arg1) throws IllegalArgumentException
	{
		return this.inventory.contains(arg0, arg1);
	}

	@Override
	public boolean contains(ItemStack arg0, int arg1)
	{
		return this.inventory.contains(arg0, arg1);
	}

	@Override
	public boolean containsAtLeast(ItemStack arg0, int arg1)
	{
		return this.inventory.containsAtLeast(arg0, arg1);
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(int arg0)
	{
		throw new UnsupportedOperationException("You must use a material or item.");
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(Material arg0) throws IllegalArgumentException
	{
		return this.inventory.all(arg0);
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(ItemStack arg0)
	{
		return this.inventory.all(arg0);
	}

	@Override
	public int first(int arg0)
	{
		throw new UnsupportedOperationException("You must use a material or item.");
	}

	@Override
	public int first(Material arg0) throws IllegalArgumentException
	{
		return this.inventory.first(arg0);
	}

	@Override
	public int first(ItemStack arg0)
	{
		return this.inventory.first(arg0);
	}

	@Override
	public int firstEmpty()
	{
		return this.inventory.firstEmpty();
	}

	@Override
	public void remove(int arg0)
	{
		throw new UnsupportedOperationException("You must use a material or item.");
	}

	@Override
	public void remove(Material arg0) throws IllegalArgumentException
	{
		this.inventory.remove(arg0);
	}

	@Override
	public void remove(ItemStack arg0)
	{
		this.inventory.remove(arg0);
	}

	@Override
	public void clear(int arg0)
	{
		this.inventory.clear(arg0);
	}

	@Override
	public void clear()
	{
		this.inventory.clear();
	}

	@Override
	public List<HumanEntity> getViewers()
	{
		return this.inventory.getViewers();
	}

	@Override
	public String getTitle()
	{
		return this.inventory.getTitle();
	}

	@Override
	public InventoryType getType()
	{
		return this.inventory.getType();
	}

	@Override
	public InventoryHolder getHolder()
	{
		return this.inventory.getHolder();
	}

	@Override
	public ListIterator<ItemStack> iterator()
	{
		return this.inventory.iterator();
	}

	@Override
	public ListIterator<ItemStack> iterator(int arg0)
	{
		return this.inventory.iterator(arg0);
	}

	@Override
	public Panel clone()
	{
		Panel panel = new Panel(this.identifier, this.getName(), this.getSize(), this.owner);

		panel.setContents(this.getContents());

		panel.setMaxStackSize(this.getMaxStackSize());

		return panel;
	}

}
