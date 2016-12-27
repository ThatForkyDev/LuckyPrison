package com.ulfric.lib.api.inventory;

import com.ulfric.lib.api.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemPair extends Pair<Material, Short> {

	private int hashcode;

	protected ItemPair(Material material, Short data)
	{
		super(material, data);
	}

	public Material getType()
	{
		return this.getA();
	}

	public Short getData()
	{
		return this.getB();
	}

	public byte getBlockData()
	{
		return this.getData().byteValue();
	}

	public ItemStack toItem()
	{
		return this.toItem(1);
	}

	public ItemStack toItem(int amount)
	{
		return new ItemStack(this.getType(), amount, this.getData());
	}

	public boolean isBlock()
	{
		return this.getType().isBlock();
	}

	@Override
	public int hashCode()
	{
		if (this.hashcode == 0)
		{
			this.hashcode = this.getType().hashCode() * this.getData().hashCode();
		}

		return this.hashcode;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == null) return false;

		if (other == this) return true;

		if (other instanceof Material)
		{
			return this.getData() == 0 && other.equals(this.getType());

		}

		if (!(other instanceof ItemPair)) return false;

		ItemPair otherItem = (ItemPair) other;

		return (otherItem.getType() == this.getType() && otherItem.getData().equals(this.getData()));
	}

	@Override
	public String toString()
	{
		return MaterialUtils.getName(this);
	}

}
