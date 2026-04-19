package info.atlasv.decorative_distractions.tint.recipe;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.tint.TintDataComponents;
import info.atlasv.decorative_distractions.tint.TintDyeHistory;
import info.atlasv.decorative_distractions.tint.item.TintBlockItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers all custom {@link RecipeSerializer}s for the tint module.
 */
public class TintRecipeSerializers {

    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, DecorativeDistractions.MODID);

    /**
     * Serializer for {@link TintDyeRecipe}.
     * Uses {@link SimpleCraftingRecipeSerializer} since the recipe has no extra
     * data beyond the standard {@code category} field.
     */
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<TintDyeRecipe>>
            TINT_DYE = SERIALIZERS.register("tint_dye",
            () -> new SimpleCraftingRecipeSerializer<>(TintDyeRecipe::new));

    public static void register(IEventBus modEventBus) {
        SERIALIZERS.register(modEventBus);
    }

    /**
     * A custom shapeless crafting recipe that handles dyeing of
     * {@link TintBlockItem}s. Functionally identical to vanillas
     * {@code minecraft:crafting_special_armordye} recipe, with one addition:
     * it appends the dyes used in this craft to the items
     * {@link TintDataComponents#DYE_HISTORY} component so the exact dye history
     * can be retrieved using the {@code /GetTint} command.
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
            List<ItemStack> dyes = new ArrayList<>();

            for (int i = 0; i < input.size(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.isEmpty()) continue;

                if (stack.getItem() instanceof TintBlockItem) {
                    if (!tintItem.isEmpty()) return false; // more than one tint item
                    tintItem = stack;
                } else if (stack.getItem() instanceof DyeItem) {
                    dyes.add(stack);
                } else {
                    return false; // unexpected item type
                }
            }

            return !tintItem.isEmpty() && !dyes.isEmpty();
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
                } else if (stack.getItem() instanceof DyeItem dyeItem) {
                    dyeColors.add(dyeItem.getDyeColor());
                }
            }

            if (tintItem.isEmpty() || dyeColors.isEmpty()) return ItemStack.EMPTY;

            ItemStack result = tintItem.copyWithCount(1);

            // Vanilla colour mixing
            // Each dye contributes its textureDiffuseColor (packed ARGB 0xAARRGGBB).
            // also toss in the existing item colour as a virtual first entry

            int totalR = 0, totalG = 0, totalB = 0;
            int maxComponent = 0; // tracks the highest single channel across all inputs
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

        // Boilerplate Bullshittery

        @Override
        public boolean canCraftInDimensions(int width, int height) {
            // Any grid that can hold at least 2 slots works (1 item + 1 dye minimum).
            // Just in case a mod adds another method of crafting shapeless
            return width * height >= 2;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return TINT_DYE.get();
        }
    }
}