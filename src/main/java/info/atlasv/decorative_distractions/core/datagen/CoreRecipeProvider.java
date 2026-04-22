package info.atlasv.decorative_distractions.core.datagen;

import info.atlasv.decorative_distractions.basic.block.BasicStoneBlockSets;
import info.atlasv.decorative_distractions.basic.item.BasicItems;
import info.atlasv.decorative_distractions.tint.TintEntry;
import info.atlasv.decorative_distractions.tint.block.TintBlocks;
import info.atlasv.decorative_distractions.tint.item.TintItems;
import info.atlasv.decorative_distractions.tint.recipe.TintRecipeSerializers;
import info.atlasv.decorative_distractions.DecorativeDistractions;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class CoreRecipeProvider extends RecipeProvider {

    public CoreRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        buildBasicRecipes(output);
        buildTintRecipes(output);
    }

    // Basic module
    private void buildBasicRecipes(RecipeOutput output) {
        BasicStoneBlockSets.addRecipes(output);
        BasicItems.addRecipes(output);          // mortar & pestle + crystal dusts
    }

    // Tint module
    private void buildTintRecipes(RecipeOutput output) {
        var cobblestoneItem = TintBlocks.TINTED_COBBLESTONE.item.get();
        var stoneItem       = TintBlocks.TINTED_STONE.item.get();
        var grassItem       = TintBlocks.TINTED_GRASS_BLOCK.item.get();

        // Paintbrush
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TintItems.PAINTBRUSH_ITEM.get())
                .pattern("  F")
                .pattern(" P ")
                .pattern("S  ")
                .define('F', ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "feathers")))
                .define('P', ItemTags.PLANKS)
                .define('S', ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "rods/wooden")))
                .unlockedBy("has_feather", has(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "feathers"))))
                .save(output);

        // Regular block -> tinted block
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, cobblestoneItem)
                .requires(Items.COBBLESTONE)
                .requires(TintItems.PAINTBRUSH_ITEM.get())
                .unlockedBy("has_paintbrush", has(TintItems.PAINTBRUSH_ITEM.get()))
                .save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, stoneItem)
                .requires(Items.STONE)
                .requires(TintItems.PAINTBRUSH_ITEM.get())
                .unlockedBy("has_paintbrush", has(TintItems.PAINTBRUSH_ITEM.get()))
                .save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, grassItem)
                .requires(Items.GRASS_BLOCK)
                .requires(TintItems.PAINTBRUSH_ITEM.get())
                .unlockedBy("has_paintbrush", has(TintItems.PAINTBRUSH_ITEM.get()))
                .save(output);

        // Tinted -> tinted (strips dye / resets colour)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, cobblestoneItem)
                .requires(cobblestoneItem)
                .unlockedBy("has_tinted_cobblestone_block", has(cobblestoneItem))
                .save(output, "tinted_cobblestone_block_clean");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, stoneItem)
                .requires(stoneItem)
                .unlockedBy("has_tinted_stone_block", has(stoneItem))
                .save(output, "tinted_stone_block_clean");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, grassItem)
                .requires(grassItem)
                .unlockedBy("has_tinted_grass_block", has(grassItem))
                .save(output, "tinted_grass_block_clean");

        // Special dyeing recipe
        SpecialRecipeBuilder.special(TintRecipeSerializers.TintDyeRecipe::new)
                .save(output, DecorativeDistractions.MODID + ":tint_dye");

        // Slab & stair recipes for every TintEntry
        for (TintEntry entry : TintBlocks.getEntries()) {
            registerTintSlabAndStairRecipes(output, entry);
        }
    }

    private void registerTintSlabAndStairRecipes(RecipeOutput output, TintEntry entry) {
        var parentItem = entry.item.get();
        var slabItem   = entry.slabItem.get();
        var stairsItem = entry.stairsItem.get();
        String baseName = entry.name;

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, slabItem, 6)
                .requires(parentItem).requires(parentItem).requires(parentItem)
                .unlockedBy("has_" + baseName, has(parentItem))
                .save(output, DecorativeDistractions.MODID + ":" + baseName + "_slab_from_" + baseName);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, slabItem)
                .requires(slabItem)
                .unlockedBy("has_" + baseName + "_slab", has(slabItem))
                .save(output, DecorativeDistractions.MODID + ":" + baseName + "_slab_clean");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, stairsItem, 4)
                .requires(parentItem).requires(parentItem).requires(parentItem)
                .requires(parentItem).requires(parentItem).requires(parentItem)
                .unlockedBy("has_" + baseName, has(parentItem))
                .save(output, DecorativeDistractions.MODID + ":" + baseName + "_stairs_from_" + baseName);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, stairsItem)
                .requires(stairsItem)
                .unlockedBy("has_" + baseName + "_stairs", has(stairsItem))
                .save(output, DecorativeDistractions.MODID + ":" + baseName + "_stairs_clean");
    }
}