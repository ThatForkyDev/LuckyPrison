package com.ulfric.prison.entity;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class LuckyBlocks {


	public static boolean isLuckyBlock(Block block)
	{
		return block.getType().equals(Material.SPONGE) || LuckyBlocks.isSuperLucky(block);
	}

	public static boolean isSuperLucky(Block block)
	{
		return block.getType().equals(Material.ENDER_STONE);
	}


}