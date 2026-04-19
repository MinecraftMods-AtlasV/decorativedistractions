package info.atlasv.decorative_distractions.tint.datagen;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.tint.TintEntry;
import info.atlasv.decorative_distractions.tint.block.TintBlocks;
import info.atlasv.decorative_distractions.tint.recipe.TintRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class TintRecipeProvider extends RecipeProvider {

    public TintRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {

        // Base block recipes
        var cobblestoneItem = TintBlocks.TINTED_COBBLESTONE.item.get();

        // TODO: Make a better recipe than 1 dirt -> 1 undyed tinted_cobblestone block
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, cobblestoneItem)
                .requires(Items.DIRT)
                .unlockedBy("has_dirt", has(Items.DIRT))
                .save(output);

        // tinted_cobblestone -> tinted_cobblestone (strips dye + history / resets colour)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, cobblestoneItem)
                .requires(cobblestoneItem)
                .unlockedBy("has_tinted_cobblestone_block", has(cobblestoneItem))
                .save(output, "tinted_cobblestone_block_clean");

        // Special dyeing recipe - handles all TintBlockItems in the DYEABLE tag.
        SpecialRecipeBuilder.special(TintRecipeSerializers.TintDyeRecipe::new)
                .save(output, DecorativeDistractions.MODID + ":tint_dye");

        // Slab & stair recipes, auto-generated for every TintEntry

        for (TintEntry entry : TintBlocks.getEntries()) {
            registerSlabAndStairRecipes(output, entry);
        }
    }

    // Per-entry slab + stair recipe helper

    /**
     * Registers two recipes for {@code entry}:
     *
     * <ul>
     *   <li><b>Slab</b>: 3x (any tinted parent block item) -> 6x undyed slab.
     *       The input ingredient matches the base {@link info.atlasv.decorative_distractions.tint.item.TintBlockItem}
     *       regardless of its {@code DYED_COLOR} component, so any colour of parent
     *       block can be used. Output is always the default (undyed) slab.</li>
     *   <li><b>Stairs</b>: 6x (any tinted parent block item) -> 4x undyed stair.
     *       Same colour-stripping behaviour.</li>
     * </ul>
     *
     * <p>Recipe IDs follow the pattern {@code <modid>:<block_name>_slab_from_<block_name>}
     * and {@code <modid>:<block_name>_stairs_from_<block_name>} so they never collide.
     *
     * <p><b>Colour stripping</b>: Because these are vanilla {@link ShapelessRecipeBuilder}
     * recipes the output item is created fresh from the registry - it carries the default
     * component values ({@code DYED_COLOR = white}, {@code DYE_HISTORY = empty}) just
     * like a freshly crafted base block.
     */
    private void registerSlabAndStairRecipes(RecipeOutput output, TintEntry entry) {
        var parentItem = entry.item.get();
        var slabItem   = entry.slabItem.get();
        var stairsItem = entry.stairsItem.get();

        String baseName = entry.name; // e.g. "tinted_cobblestone"

        // slab: 3 parent blocks -> 6 slabs (vanilla stonecut ratio)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, slabItem, 6)
                .requires(parentItem)
                .requires(parentItem)
                .requires(parentItem)
                .unlockedBy("has_" + baseName, has(parentItem))
                .save(output, DecorativeDistractions.MODID + ":" + baseName + "_slab_from_" + baseName);

        // slab reset: 1 tinted slab -> 1 undyed slab (strips dye)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, slabItem)
                .requires(slabItem)
                .unlockedBy("has_" + baseName + "_slab", has(slabItem))
                .save(output, DecorativeDistractions.MODID + ":" + baseName + "_slab_clean");

        // stairs: 6 parent blocks -> 4 stairs (vanilla ratio)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, stairsItem, 4)
                .requires(parentItem)
                .requires(parentItem)
                .requires(parentItem)
                .requires(parentItem)
                .requires(parentItem)
                .requires(parentItem)
                .unlockedBy("has_" + baseName, has(parentItem))
                .save(output, DecorativeDistractions.MODID + ":" + baseName + "_stairs_from_" + baseName);

        // stairs reset: 1 tinted stair -> 1 undyed stair (strips dye)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, stairsItem)
                .requires(stairsItem)
                .unlockedBy("has_" + baseName + "_stairs", has(stairsItem))
                .save(output, DecorativeDistractions.MODID + ":" + baseName + "_stairs_clean");
    }
}