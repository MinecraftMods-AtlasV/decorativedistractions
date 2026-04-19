package info.atlasv.decorative_distractions.core.utils;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public class RecipeHelpers {
    // Helper to make smelting recipes use my mods namespace instead of minecrafts
    public static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> ingredients,
                                   RecipeCategory category, ItemLike result,
                                   float experience, int cookingTime, String group) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new,
                ingredients, category, result, experience, cookingTime, group, "smelting_");
    }

    // Helper to make blasting recipes use my mods namespace instead of minecrafts

    public static void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> ingredients,
                                   RecipeCategory category, ItemLike result,
                                   float experience, int cookingTime, String group) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new,
                ingredients, category, result, experience, cookingTime, group, "blasting_");
    }

    public static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput,
                                                                    RecipeSerializer<T> cookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                    List<ItemLike> ingredients, RecipeCategory category, ItemLike result,
                                                                    float experience, int cookingTime, String group, String recipeName) {
        for (ItemLike itemLike : ingredients) {
            SimpleCookingRecipeBuilder
                    .generic(Ingredient.of(itemLike), category, result, experience, cookingTime, cookingSerializer, factory)
                    .group(group)
                    .unlockedBy(getHasName(itemLike), InventoryChangeTrigger.TriggerInstance.hasItems(itemLike))
                    .save(recipeOutput, DecorativeDistractions.MODID + ":" + recipeName + getItemName(itemLike) + "_" + getItemName(result));
        }
    }


    // Replicates RecipeProvider.getItemName() - gets the registry path of an item
    private static String getItemName(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }

    // Replicates RecipeProvider.getHasName()
    private static String getHasName(ItemLike item) {
        return "has_" + getItemName(item);
    }
}
