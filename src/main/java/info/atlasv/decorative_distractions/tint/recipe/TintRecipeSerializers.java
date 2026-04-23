package info.atlasv.decorative_distractions.tint.recipe;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.tint.TintDataComponents;
import info.atlasv.decorative_distractions.tint.TintDyeHistory;
import info.atlasv.decorative_distractions.tint.item.TintBlockItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.Arrays;

/**
 * Registers all custom {@link RecipeSerializer}s for the tint module.
 */
public class TintRecipeSerializers {

    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, DecorativeDistractions.MODID);

    /**
     * Maps "c:dyes/<colour>" tag names to their {@link DyeColor} counterpart.
     * Built once at class load from DyeColor.values() so new entries are
     * covered automatically if Mojang ever adds a 17th dye.
     * Key format matches DyeColor.getName() exactly (e.g. "light_blue").
     */
    private static final Map<TagKey<net.minecraft.world.item.Item>, DyeColor> DYE_TAG_TO_COLOR =
            Arrays.stream(DyeColor.values()).collect(Collectors.toMap(
                    dye -> TagKey.create(
                            Registries.ITEM,
                            ResourceLocation.fromNamespaceAndPath("c", "dyes/" + dye.getName())
                    ),
                    dye -> dye
            ));

    /**
     * Serializer for {@link TintDyeRecipe}.
     * Uses {@link SimpleCraftingRecipeSerializer} since the recipe has no extra
     * data beyond the standard {@code category} field.
     */
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<TintDyeRecipe>>
            TINT_DYE = SERIALIZERS.register("tint_dye",
            () -> new SimpleCraftingRecipeSerializer<>(TintDyeRecipe::new));

    /**
     * Serializer for {@link TintSlabRecipe}.
     */
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<TintSlabRecipe>>
            TINT_SLAB = SERIALIZERS.register("tint_slab",
            () -> new SimpleCraftingRecipeSerializer<>(TintSlabRecipe::new));

    /**
     * Serializer for {@link TintStairsRecipe}.
     */
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<TintStairsRecipe>>
            TINT_STAIRS = SERIALIZERS.register("tint_stairs",
            () -> new SimpleCraftingRecipeSerializer<>(TintStairsRecipe::new));

    public static void register(IEventBus modEventBus) {
        SERIALIZERS.register(modEventBus);
    }

    /**
     * Resolves a {@link DyeColor} from an {@link ItemStack} by checking whether
     * the item is in any of the 16 known {@code c:dyes/<colour>} tags.
     *
     * <p>Items in unrecognised dye tags (e.g. {@code c:dyes/rose_red} from
     * another mod) return {@link Optional#empty()} and are treated as
     * non-dyes by the recipe, so they don't block crafting.
     *
     * @param stack the item to check
     * @param level the current level, used to access the tag registry
     * @return the matching {@link DyeColor}, or empty if none matched
     */
    private static Optional<DyeColor> resolveColor(ItemStack stack, Level level) {
        return DYE_TAG_TO_COLOR.entrySet().stream()
                .filter(entry -> stack.is(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    /**
     * A custom shapeless crafting recipe that handles dyeing of
     * {@link TintBlockItem}s. Functionally identical to vanillas
     * {@code minecraft:crafting_special_armordye} recipe, with one addition:
     * it appends the dyes used in this craft to the items
     * {@link TintDataComponents#DYE_HISTORY} component so the exact dye history
     * can be retrieved using the {@code /GetTint} command.
     *
     * <p>Accepts any item in a {@code c:dyes/<colour>} tag that maps to one of
     * the 16 vanilla {@link DyeColor} values. Items in unrecognised dye tags
     * are treated as non-dyes and silently ignored, so they don't block crafting.
     *
     * <p>Vanilla colour-mixing algorithm:
     * <ol>
     *   <li>Collect the existing item colour (if any) as a "virtual" first dye.</li>
     *   <li>Add the R, G, B channels and track the max channel value seen.</li>
     *   <li>Average the sums.</li>
     *   <li>Scale so the brightest average channel matches the brightest raw channel
     *       seen across all inputs, done to preserve saturation.</li>
     * </ol>
     *
     * <p>This recipe is only active for items that are both a {@link TintBlockItem}
     * and in the {@link ItemTags#DYEABLE} tag. Vanillas armordye recipe continues
     * to handle leather armour and other dyeable items unaffected.
     */
    public static class TintDyeRecipe extends CustomRecipe {

        public TintDyeRecipe(CraftingBookCategory category) {
            super(category);
        }

        // Recipe matching
        @Override
        public boolean matches(CraftingInput input, Level level) {
            ItemStack tintItem = ItemStack.EMPTY;
            boolean hasDye = false;

            for (int i = 0; i < input.size(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.isEmpty()) continue;

                if (stack.getItem() instanceof TintBlockItem) {
                    if (!tintItem.isEmpty()) return false; // more than one tint item
                    tintItem = stack;
                } else if (resolveColor(stack, level).isPresent()) {
                    // Recognised dye tag — counts toward the dye requirement
                    hasDye = true;
                } else {
                    // Not a tint block item and not a recognised dye — reject
                    return false;
                }
            }

            return !tintItem.isEmpty() && hasDye;
        }

        // Recipe assembly
        @Override
        public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
            ItemStack tintItem = ItemStack.EMPTY;
            List<DyeColor> dyeColors = new ArrayList<>();

            for (int i = 0; i < input.size(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.isEmpty()) continue;

                if (stack.getItem() instanceof TintBlockItem) {
                    tintItem = stack;
                } else {
                    // resolveColor returning empty here is safe — matches() already
                    // guaranteed every non-tint slot resolved to a known DyeColor
                    resolveColor(stack, (Level) null).ifPresent(dyeColors::add);
                }
            }

            if (tintItem.isEmpty() || dyeColors.isEmpty()) return ItemStack.EMPTY;

            ItemStack result = tintItem.copyWithCount(1);

            // Vanilla colour mixing
            // Each dye contributes its textureDiffuseColor (packed ARGB 0xAARRGGBB).
            // also toss in the existing item colour as a virtual first entry

            int totalR = 0, totalG = 0, totalB = 0;
            int maxComponent = 0;
            int count = 0;

            // toss in existing colour (if the item already has a dye component)
            DyedItemColor existing = result.get(DataComponents.DYED_COLOR);
            if (existing != null) {
                int rgb = existing.rgb();
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >>  8) & 0xFF;
                int b =  rgb        & 0xFF;
                totalR += r; totalG += g; totalB += b;
                maxComponent = Math.max(maxComponent, Math.max(r, Math.max(g, b)));
                count++;
            }

            // toss in each dye
            for (DyeColor dye : dyeColors) {
                int packed = dye.getTextureDiffuseColor(); // 0xAARRGGBB
                int r = (packed >> 16) & 0xFF;
                int g = (packed >>  8) & 0xFF;
                int b =  packed        & 0xFF;
                totalR += r; totalG += g; totalB += b;
                maxComponent = Math.max(maxComponent, Math.max(r, Math.max(g, b)));
                count++;
            }

            // Average
            float avgR = (float) totalR / count;
            float avgG = (float) totalG / count;
            float avgB = (float) totalB / count;

            // Saturation preserving scale: brightest average channel -> maxComponent
            float avgMax = Math.max(avgR, Math.max(avgG, avgB));
            float scale = (avgMax > 0) ? (float) maxComponent / avgMax : 1.0f;

            int finalR = Math.min(255, Math.round(avgR * scale));
            int finalG = Math.min(255, Math.round(avgG * scale));
            int finalB = Math.min(255, Math.round(avgB * scale));
            int finalRgb = (finalR << 16) | (finalG << 8) | finalB;

            // Write colour component (showInTooltip=true mirrors leather armour)
            result.set(DataComponents.DYED_COLOR, new DyedItemColor(finalRgb, true));

            // Append dye history
            TintDyeHistory history = result.getOrDefault(
                    TintDataComponents.DYE_HISTORY.get(), TintDyeHistory.EMPTY);
            result.set(TintDataComponents.DYE_HISTORY.get(), history.withStep(dyeColors));

            return result;
        }

        @Override
        public boolean canCraftInDimensions(int width, int height) {
            return width * height >= 2;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return TINT_DYE.get();
        }
    }

    // -------------------------------------------------------------------------
    // Shared colour helpers
    // -------------------------------------------------------------------------

    /**
     * Returns the packed RGB int from a stack's {@link DataComponents#DYED_COLOR}
     * component, or {@link OptionalInt#empty()} if the stack has no dye component.
     */
    private static OptionalInt getDyeRgb(ItemStack stack) {
        DyedItemColor color = stack.get(DataComponents.DYED_COLOR);
        return color != null ? OptionalInt.of(color.rgb()) : OptionalInt.empty();
    }

    /**
     * Copies the {@link DataComponents#DYED_COLOR} component from {@code source}
     * onto {@code target} if one is present, otherwise removes it from
     * {@code target} so an undyed result is truly clean.
     */
    private static void transferColor(ItemStack source, ItemStack target) {
        DyedItemColor color = source.get(DataComponents.DYED_COLOR);
        if (color != null) {
            target.set(DataComponents.DYED_COLOR, color);
        } else {
            target.remove(DataComponents.DYED_COLOR);
        }
    }

    // -------------------------------------------------------------------------
    // TintSlabRecipe
    // -------------------------------------------------------------------------

    /**
     * Shaped crafting recipe for tinted slabs.
     *
     * <p>Accepts exactly 3 {@link TintBlockItem}s filling any single complete row
     * of the crafting grid, with all other slots empty. If all 3 inputs share the
     * same {@link DataComponents#DYED_COLOR} value the output slab inherits that
     * colour; otherwise the output is undyed.
     *
     * <p>Yields 6 slabs, matching the vanilla slab recipe ratio.
     */
    public static class TintSlabRecipe extends CustomRecipe {

        public TintSlabRecipe(CraftingBookCategory category) {
            super(category);
        }

        @Override
        public boolean matches(CraftingInput input, Level level) {
            // Need at least 3 columns
            if (input.width() < 3) return false;

            boolean foundRow = false;
            for (int row = 0; row < input.height(); row++) {
                // Check all 3 slots in this row are non-empty TintBlockItems
                boolean rowFilled = true;
                for (int col = 0; col < 3; col++) {
                    ItemStack stack = input.getItem(col, row);
                    if (stack.isEmpty() || !(stack.getItem() instanceof TintBlockItem)) {
                        rowFilled = false;
                        break;
                    }
                }
                if (!rowFilled) continue;

                // Check all 3 items are the same block type
                Item firstItem = input.getItem(0, row).getItem();
                if (input.getItem(1, row).getItem() != firstItem
                        || input.getItem(2, row).getItem() != firstItem) continue;

                // Check all other slots are empty
                boolean otherEmpty = true;
                outer:
                for (int r = 0; r < input.height(); r++) {
                    for (int c = 0; c < input.width(); c++) {
                        if (r == row && c < 3) continue; // this is the filled row
                        if (!input.getItem(c, r).isEmpty()) {
                            otherEmpty = false;
                            break outer;
                        }
                    }
                }
                if (!otherEmpty) continue;

                foundRow = true;
                break;
            }
            return foundRow;
        }

        @Override
        public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
            // Find the filled row
            int filledRow = -1;
            for (int row = 0; row < input.height(); row++) {
                if (!input.getItem(0, row).isEmpty()
                        && input.getItem(0, row).getItem() instanceof TintBlockItem) {
                    filledRow = row;
                    break;
                }
            }
            if (filledRow == -1) return ItemStack.EMPTY;

            ItemStack first  = input.getItem(0, filledRow);
            ItemStack second = input.getItem(1, filledRow);
            ItemStack third  = input.getItem(2, filledRow);

            // Resolve expected output item from the first block's slab counterpart
            if (!(first.getItem() instanceof TintBlockItem tbi)) return ItemStack.EMPTY;
            ItemStack result = new ItemStack(tbi.getSlabItem(), 6);

            // Transfer colour only if all three share exactly the same RGB value
            OptionalInt c1 = getDyeRgb(first);
            OptionalInt c2 = getDyeRgb(second);
            OptionalInt c3 = getDyeRgb(third);

            boolean allColored  = c1.isPresent() && c2.isPresent() && c3.isPresent();
            boolean allUncolored = c1.isEmpty()  && c2.isEmpty()   && c3.isEmpty();
            boolean sameColor   = allColored
                    && c1.getAsInt() == c2.getAsInt()
                    && c1.getAsInt() == c3.getAsInt();

            if (sameColor) {
                transferColor(first, result);
            }
            // allUncolored → result stays undyed (correct)
            // mixed        → result stays undyed (correct)

            return result;
        }

        @Override
        public boolean canCraftInDimensions(int width, int height) {
            return width >= 3 && height >= 1;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return TINT_SLAB.get();
        }
    }

    // -------------------------------------------------------------------------
    // TintStairsRecipe
    // -------------------------------------------------------------------------

    /**
     * Shaped crafting recipe for tinted stairs.
     *
     * <p>Accepts 6 {@link TintBlockItem}s in either of the two staircase patterns
     * anchored to the bottom row:
     * <pre>
     *   Left-hand:   Right-hand:
     *   X . .        . . X
     *   X X .        . X X
     *   X X X        X X X
     * </pre>
     * All 6 inputs must share the same {@link DataComponents#DYED_COLOR} for the
     * output to inherit that colour; otherwise the output is undyed.
     *
     * <p>Yields 4 stairs, matching the vanilla stair recipe ratio.
     */
    public static class TintStairsRecipe extends CustomRecipe {

        /**
         * Staircase slot offsets relative to the bottom-left corner of a 3×3 grid.
         * Row 0 = bottom, row 2 = top (matching {@link CraftingInput#getItem(int,int)}).
         * Each int[] is {col, row}.
         */
        private static final int[][] LEFT_HAND = { // NOTE: top left is {0, 0}, bottom right is {2, 2}
                {0, 0},               // top-left
                {0, 1}, {1, 1},       // middle row
                {0, 2}, {1, 2}, {2, 2} // bottom row
        };
        private static final int[][] RIGHT_HAND = {
                {2, 0},               // top-right
                {1, 1}, {2, 1},       // middle row
                {0, 2}, {1, 2}, {2, 2} // bottom row
        };

        public TintStairsRecipe(CraftingBookCategory category) {
            super(category);
        }

        @Override
        public boolean matches(CraftingInput input, Level level) {
            if (input.width() < 3 || input.height() < 3) return false;
            return matchesPattern(input, LEFT_HAND) || matchesPattern(input, RIGHT_HAND);
        }

        private boolean matchesPattern(CraftingInput input, int[][] filled) {
            // Build a set of filled positions for quick lookup
            java.util.Set<Long> filledSet = new java.util.HashSet<>();
            for (int[] pos : filled) filledSet.add(encode(pos[0], pos[1]));

            Item firstItem = null;
            for (int row = 0; row < input.height(); row++) {
                for (int col = 0; col < input.width(); col++) {
                    ItemStack stack = input.getItem(col, row);
                    boolean shouldBeFilled = filledSet.contains(encode(col, row));
                    if (shouldBeFilled) {
                        if (stack.isEmpty() || !(stack.getItem() instanceof TintBlockItem)) return false;
                        // All filled slots must be the same block type
                        if (firstItem == null) firstItem = stack.getItem();
                        else if (stack.getItem() != firstItem) return false;
                    } else {
                        if (!stack.isEmpty()) return false;
                    }
                }
            }
            return true;
        }

        private static long encode(int col, int row) {
            return ((long) row << 32) | col;
        }

        @Override
        public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
            int[][] pattern = matchesPattern(input, LEFT_HAND) ? LEFT_HAND : RIGHT_HAND;

            List<ItemStack> inputs = new ArrayList<>();
            for (int[] pos : pattern) {
                inputs.add(input.getItem(pos[0], pos[1]));
            }

            if (!(inputs.get(0).getItem() instanceof TintBlockItem tbi)) return ItemStack.EMPTY;
            ItemStack result = new ItemStack(tbi.getStairsItem(), 4);

            // All 6 must share the same RGB value to pass colour through
            OptionalInt firstColor = getDyeRgb(inputs.get(0));
            boolean allSame = inputs.stream().allMatch(s -> {
                OptionalInt c = getDyeRgb(s);
                return firstColor.isPresent()
                        ? c.isPresent() && c.getAsInt() == firstColor.getAsInt()
                        : c.isEmpty();
            });

            if (allSame && firstColor.isPresent()) {
                transferColor(inputs.get(0), result);
            }

            return result;
        }

        @Override
        public boolean canCraftInDimensions(int width, int height) {
            return width >= 3 && height >= 3;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return TINT_STAIRS.get();
        }
    }
}