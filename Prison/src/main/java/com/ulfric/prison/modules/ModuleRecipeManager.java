package com.ulfric.prison.modules;

import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.prison.Prison;

public class ModuleRecipeManager extends SimpleModule
{
	public ModuleRecipeManager()
	{
		super("recipemanager", "Adds and modifies crafting recipes", "StaticShadow", "1.0.0-REL");
	}

	@Override
	public void postEnable()
	{
		// Get rid of any conflicting recipes
		Iterator<Recipe> it = Prison.get().getServer().recipeIterator();
		while (it.hasNext())
		{
			Recipe recipe = it.next();
			if (recipe instanceof ShapedRecipe)
			{
				ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;

				// Gold nuggets
				if (shapedRecipe.getIngredientMap().size() == 1)
				{
					ItemStack ingredient = shapedRecipe.getIngredientMap().get(shapedRecipe.getIngredientMap().keySet().toArray(new Character[1])[0]);
					if (ingredient.getType() == Material.GOLD_INGOT)
						if (ingredient.getAmount() == 1)
							it.remove();
				}
			}
		}

		// Gold ingot --> 3 gold nuggets
		ItemStack goldNuggetStack = new ItemStack(Material.GOLD_NUGGET, 3);
		ShapelessRecipe goldNuggetRecipe = new ShapelessRecipe(goldNuggetStack);
		goldNuggetRecipe.addIngredient(Material.GOLD_INGOT);
		Prison.get().getServer().addRecipe(goldNuggetRecipe);
	}
}
