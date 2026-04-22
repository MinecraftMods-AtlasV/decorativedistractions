package info.atlasv.decorative_distractions.basic.block;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.basic.item.BasicItems;
import info.atlasv.decorative_distractions.core.blocksets.Stone;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class BasicStoneBlockSets {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(DecorativeDistractions.MODID);

    public static Collection<DeferredHolder<Block, ? extends Block>> getEntries() {
        return BLOCKS.getEntries();
    }

    public static final Map<StoneVariant, Stone> SMOOTH_VARIANTS = new EnumMap<>(StoneVariant.class);
    public static final Map<StoneVariant, Stone> SMOOTH_BRICKS_VARIANTS = new EnumMap<>(StoneVariant.class);

    // Smooth stone variant registration
    static {
        for (StoneVariant variant : StoneVariant.values()) {
            SMOOTH_VARIANTS.put(variant, new Stone(
                    "smooth_" + variant.getName(),
                    "block/smooth_" + variant.getName(),
                    BLOCKS,
                    BasicItems.ITEMS
            ));
        }
    }

    // Smooth stone bricks variant registration
    static {
        for (StoneVariant variant : StoneVariant.values()) {
            SMOOTH_BRICKS_VARIANTS.put(variant, new Stone(
                    "smooth_" + variant.getName() + "_bricks",
                    "block/smooth_" + variant.getName() + "_bricks",
                    BLOCKS,
                    BasicItems.ITEMS
            ));
        }
    }

    // Recipe Generation
    /**
     * Called from BasicRecipeProvider.buildRecipes() to add all recipes for
     * the smooth and smooth brick block sets.
     */
    public static void addRecipes(RecipeOutput output) {
        for (StoneVariant variant : StoneVariant.values()) {
            Stone smooth = SMOOTH_VARIANTS.get(variant);
            Stone bricks = SMOOTH_BRICKS_VARIANTS.get(variant);
            Block vanilla = variant.getBaseBlock();
            Block polished = variant.getPolishedBlock();

            addSmoothVariantRecipes(output, smooth, vanilla, polished, variant.getName());
            addSmoothBricksVariantRecipes(output, bricks, smooth, vanilla, polished, variant.getName());
        }
    }

    /**
     * Crafting + stonecutting recipes for a smooth variant set.
     * - Block:         4 polished    -> 4 smooth block (2x2 crafting)
     * - Sub-variants:  smooth block  -> stairs/slab/wall/etc (crafting)
     * - Stonecutting:  polished OR vanilla OR smooth block as source
     */
    private static void addSmoothVariantRecipes(RecipeOutput output, Stone stone, Block vanilla, Block polished, String variantName) {
        Item vanillaItem  = vanilla.asItem();
        Item polishedItem = polished.asItem();
        Item smoothItem   = stone.block.get().asItem();
        String prefix       = "smooth_" + variantName;
        String polishedName = "polished_" + variantName;

        // 4 polished -> 4 smooth block (2×2 crafting)
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, stone.block.get(), 4)
                .pattern("XX")
                .pattern("XX")
                .define('X', polishedItem)
                .unlockedBy("has_" + polishedName, InventoryChangeTrigger.TriggerInstance.hasItems(polishedItem))
                .save(output, id(prefix));

        // vanilla -> 1 smooth block (stonecutting)
        addStonecutting(output, vanillaItem,  stone.block.get(), 1, prefix + "_from_" + variantName + "_stonecutting");
        // polished -> 1 smooth block (stonecutting)
        addStonecutting(output, polishedItem, stone.block.get(), 1, prefix + "_from_" + polishedName + "_stonecutting");

        // smooth block -> 4 smooth stairs (crafting)
        addStairsRecipe(output, stone, smoothItem, prefix, prefix + "_stairs", smoothItem);
        // polished -> 1 smooth stairs (stonecutting)
        addStonecutting(output, polishedItem, stone.stairs.get(), 1, prefix + "_stairs_from_" + polishedName + "_stonecutting");
        // vanilla -> 1 smooth stairs (stonecutting)
        addStonecutting(output, vanillaItem, stone.stairs.get(), 1, prefix + "_stairs_from_" + variantName + "_stonecutting");
        // smooth block -> 1 smooth stairs (stonecutting)
        addStonecutting(output, smoothItem, stone.stairs.get(), 1, prefix + "_stairs_from_" + prefix + "_stonecutting");

        // smooth block -> 6 smooth slabs (crafting)
        addSlabRecipe(output, stone, smoothItem, prefix, prefix + "_slab", smoothItem);
        // polished -> 2 smooth slabs (stonecutting)
        addStonecutting(output, polishedItem, stone.slab.get(), 2, prefix + "_slab_from_" + polishedName + "_stonecutting");
        // vanilla -> 2 smooth slabs (stonecutting)
        addStonecutting(output, vanillaItem, stone.slab.get(), 2, prefix + "_slab_from_" + variantName + "_stonecutting");
        // smooth block -> 2 smooth slabs (stonecutting)
        addStonecutting(output, smoothItem, stone.slab.get(), 2, prefix + "_slab_from_" + prefix + "_stonecutting");

        // smooth block -> 6 smooth walls (crafting)
        addWallRecipe(output, stone, smoothItem, prefix, prefix + "_wall", smoothItem);
        // polished -> 1 smooth wall (stonecutting)
        addStonecutting(output, polishedItem, stone.wall.get(), 1, prefix + "_wall_from_" + polishedName + "_stonecutting");
        // vanilla -> 1 smooth wall (stonecutting)
        addStonecutting(output, vanillaItem, stone.wall.get(), 1, prefix + "_wall_from_" + variantName + "_stonecutting");
        // smooth block -> 1 smooth wall (stonecutting)
        addStonecutting(output, smoothItem, stone.wall.get(), 1, prefix + "_wall_from_" + prefix + "_stonecutting");

        // smooth block -> smooth pressure plate (crafting)
        addPressurePlateRecipe(output, stone, smoothItem, prefix, prefix + "_pressure_plate", smoothItem);

        // smooth block -> smooth button (crafting)
        addButtonRecipe(output, stone, smoothItem, prefix, prefix + "_button", smoothItem);
    }

    /**
     * Crafting + stonecutting recipes for a smooth-bricks variant set.
     * - Block:         4 smooth block -> 4 bricks block (2x2 crafting)
     * - Block:         vanilla OR polished OR smooth block -> 1 bricks block (stonecutting)
     * - Sub-variants:  bricks block   -> stairs/slab/wall/etc (crafting)
     * - Stonecutting:  vanilla OR polished OR smooth block as source for all sub-variants
     * - Pressure plate & button: crafting only
     */
    private static void addSmoothBricksVariantRecipes(RecipeOutput output, Stone bricks, Stone smooth, Block vanilla, Block polished, String variantName) {
        Item smoothItem   = smooth.block.get().asItem();
        Item vanillaItem  = vanilla.asItem();
        Item polishedItem = polished.asItem();
        Item bricksItem   = bricks.block.get().asItem();
        String prefix       = "smooth_" + variantName + "_bricks";
        String smoothName   = "smooth_" + variantName;
        String polishedName = "polished_" + variantName;

        // 4 smooth -> 4 bricks block (2×2 crafting)
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, bricks.block.get(), 4)
                .pattern("XX")
                .pattern("XX")
                .define('X', smoothItem)
                .unlockedBy("has_" + smoothName, InventoryChangeTrigger.TriggerInstance.hasItems(smoothItem))
                .save(output, id(prefix));

        // vanilla -> 1 bricks block (stonecutting)
        addStonecutting(output, vanillaItem,  bricks.block.get(), 1, prefix + "_from_" + variantName + "_stonecutting");
        // polished -> 1 bricks block (stonecutting)
        addStonecutting(output, polishedItem, bricks.block.get(), 1, prefix + "_from_" + polishedName + "_stonecutting");
        // smooth -> 1 bricks block (stonecutting)
        addStonecutting(output, smoothItem,   bricks.block.get(), 1, prefix + "_from_" + smoothName + "_stonecutting");

        // bricks block -> 4 bricks stairs (crafting)
        addStairsRecipe(output, bricks, bricksItem, prefix, prefix + "_stairs", bricksItem);
        // vanilla -> 1 bricks stairs (stonecutting)
        addStonecutting(output, vanillaItem,  bricks.stairs.get(), 1, prefix + "_stairs_from_" + variantName + "_stonecutting");
        // polished -> 1 bricks stairs (stonecutting)
        addStonecutting(output, polishedItem, bricks.stairs.get(), 1, prefix + "_stairs_from_" + polishedName + "_stonecutting");
        // smooth -> 1 bricks stairs (stonecutting)
        addStonecutting(output, smoothItem,   bricks.stairs.get(), 1, prefix + "_stairs_from_" + smoothName + "_stonecutting");
        // bricks block -> 1 bricks stairs (stonecutting)
        addStonecutting(output, bricksItem,   bricks.stairs.get(), 1, prefix + "_stairs_from_" + prefix + "_stonecutting");

        // bricks block -> 6 bricks slabs (crafting)
        addSlabRecipe(output, bricks, bricksItem, prefix, prefix + "_slab", bricksItem);
        // vanilla -> 2 bricks slabs (stonecutting)
        addStonecutting(output, vanillaItem,  bricks.slab.get(), 2, prefix + "_slab_from_" + variantName + "_stonecutting");
        // polished -> 2 bricks slabs (stonecutting)
        addStonecutting(output, polishedItem, bricks.slab.get(), 2, prefix + "_slab_from_" + polishedName + "_stonecutting");
        // smooth -> 2 bricks slabs (stonecutting)
        addStonecutting(output, smoothItem,   bricks.slab.get(), 2, prefix + "_slab_from_" + smoothName + "_stonecutting");
        // bricks block -> 2 bricks slabs (stonecutting)
        addStonecutting(output, bricksItem,   bricks.slab.get(), 2, prefix + "_slab_from_" + prefix + "_stonecutting");

        // bricks block -> 6 bricks walls (crafting)
        addWallRecipe(output, bricks, bricksItem, prefix, prefix + "_wall", bricksItem);
        // vanilla -> 1 bricks wall (stonecutting)
        addStonecutting(output, vanillaItem,  bricks.wall.get(), 1, prefix + "_wall_from_" + variantName + "_stonecutting");
        // polished -> 1 bricks wall (stonecutting)
        addStonecutting(output, polishedItem, bricks.wall.get(), 1, prefix + "_wall_from_" + polishedName + "_stonecutting");
        // smooth -> 1 bricks wall (stonecutting)
        addStonecutting(output, smoothItem,   bricks.wall.get(), 1, prefix + "_wall_from_" + smoothName + "_stonecutting");
        // bricks block -> 1 bricks wall (stonecutting)
        addStonecutting(output, bricksItem,   bricks.wall.get(), 1, prefix + "_wall_from_" + prefix + "_stonecutting");

        // bricks block -> bricks pressure plate (crafting only)
        addPressurePlateRecipe(output, bricks, bricksItem, prefix, prefix + "_pressure_plate", bricksItem);

        // bricks block -> bricks button (crafting only)
        addButtonRecipe(output, bricks, bricksItem, prefix, prefix + "_button", bricksItem);
    }

    // Shared shaped recipe helpers
    //  6 input -> 4 stairs (standard staircase pattern)
    private static void addStairsRecipe(RecipeOutput output, Stone stone, Item ingredient,
                                        String unlockName, String recipeId, Item unlockItem) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, stone.stairs.get(), 4)
                .pattern("X  ")
                .pattern("XX ")
                .pattern("XXX")
                .define('X', ingredient)
                .unlockedBy("has_" + unlockName, InventoryChangeTrigger.TriggerInstance.hasItems(unlockItem))
                .save(output, id(recipeId));
    }

    //  6 input in a 3-wide row -> 6 slabs
    private static void addSlabRecipe(RecipeOutput output, Stone stone, Item ingredient,
                                      String unlockName, String recipeId, Item unlockItem) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, stone.slab.get(), 6)
                .pattern("XXX")
                .define('X', ingredient)
                .unlockedBy("has_" + unlockName, InventoryChangeTrigger.TriggerInstance.hasItems(unlockItem))
                .save(output, id(recipeId));
    }

    //  6 input in a 3-wide two-row pattern -> 6 walls
    private static void addWallRecipe(RecipeOutput output, Stone stone, Item ingredient,
                                      String unlockName, String recipeId, Item unlockItem) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, stone.wall.get(), 6)
                .pattern("XXX")
                .pattern("XXX")
                .define('X', ingredient)
                .unlockedBy("has_" + unlockName, InventoryChangeTrigger.TriggerInstance.hasItems(unlockItem))
                .save(output, id(recipeId));
    }

    // 2 input in a row -> 1 pressure plate
    private static void addPressurePlateRecipe(RecipeOutput output, Stone stone, Item ingredient,
                                               String unlockName, String recipeId, Item unlockItem) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, stone.pressurePlate.get(), 1)
                .pattern("XX")
                .define('X', ingredient)
                .unlockedBy("has_" + unlockName, InventoryChangeTrigger.TriggerInstance.hasItems(unlockItem))
                .save(output, id(recipeId));
    }

    // 1 input -> 1 button
    private static void addButtonRecipe(RecipeOutput output, Stone stone, Item ingredient,
                                        String unlockName, String recipeId, Item unlockItem) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, stone.button.get(), 1)
                .pattern("X")
                .define('X', ingredient)
                .unlockedBy("has_" + unlockName, InventoryChangeTrigger.TriggerInstance.hasItems(unlockItem))
                .save(output, id(recipeId));
    }

    // Stonecutting recipe: 1 ingredient -> count result
    private static void addStonecutting(RecipeOutput output, Item ingredient, Block result,
                                        int count, String recipeId) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), RecipeCategory.BUILDING_BLOCKS, result, count)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingredient))
                .save(output, id(recipeId));
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(DecorativeDistractions.MODID, path);
    }

    // TODO: Decide whether to move this to its own enum file
    public enum StoneVariant {
        DIORITE("diorite", Blocks.DIORITE, Blocks.POLISHED_DIORITE),
        ANDESITE("andesite", Blocks.ANDESITE, Blocks.POLISHED_ANDESITE),
        GRANITE("granite", Blocks.GRANITE, Blocks.POLISHED_GRANITE),
        DEEPSLATE("deepslate", Blocks.DEEPSLATE, Blocks.POLISHED_DEEPSLATE),
        TUFF("tuff", Blocks.TUFF, Blocks.POLISHED_TUFF),
        BLACKSTONE("blackstone", Blocks.BLACKSTONE, Blocks.POLISHED_BLACKSTONE);

        private final String name;
        private final Block baseBlock;
        private final Block polishedBlock;

        StoneVariant(String name, Block baseBlock, Block polishedBlock) {
            this.name = name;
            this.baseBlock = baseBlock;
            this.polishedBlock = polishedBlock;
        }

        public String getName() { return name; }
        public Block getBaseBlock() { return baseBlock; }
        public Block getPolishedBlock() { return polishedBlock; }

        public BlockBehaviour.Properties copyProps() {
            return BlockBehaviour.Properties.ofFullCopy(baseBlock);
        }
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}