package info.atlasv.decorative_distractions.tint.item;

import info.atlasv.decorative_distractions.tint.TintDataComponents;
import info.atlasv.decorative_distractions.tint.TintDyeHistory;
import info.atlasv.decorative_distractions.tint.block.TintBlockEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * The BlockItem for any tintable block.
 * Contains only logic, all registration lives in {@link info.atlasv.decorative_distractions.tint.block.TintBlocks}
 * via {@link info.atlasv.decorative_distractions.tint.TintEntry}.
 */
public class TintBlockItem extends BlockItem {

    public static final String TOOLTIP_KEY = "item.decorative_distractions.tint_block.tooltip";

    /**
     * Lazily-resolved supplier for the slab item that corresponds to this block.
     * Null for slab and stair items themselves - only set on the base block item.
     */
    private java.util.function.Supplier<Item> slabItemSupplier;

    /**
     * Lazily-resolved supplier for the stair item that corresponds to this block.
     * Null for slab and stair items themselves - only set on the base block item.
     */
    private java.util.function.Supplier<Item> stairsItemSupplier;

    public TintBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    /**
     * Called by {@link info.atlasv.decorative_distractions.tint.TintEntry} after
     * both the slab and stairs items have been registered, wiring up the
     * cross-item references needed by the crafting recipes.
     */
    public void setSlabAndStairsSuppliers(
            java.util.function.Supplier<Item> slab,
            java.util.function.Supplier<Item> stairs
    ) {
        this.slabItemSupplier  = slab;
        this.stairsItemSupplier = stairs;
    }

    /**
     * Returns the slab {@link Item} that corresponds to this base block item.
     *
     * @throws IllegalStateException if called on a slab or stair item, or before
     *                               suppliers have been wired up by {@link info.atlasv.decorative_distractions.tint.TintEntry}.
     */
    public Item getSlabItem() {
        if (slabItemSupplier == null)
            throw new IllegalStateException(
                    "getSlabItem() called on a TintBlockItem with no slab supplier — " +
                            "this item may be a slab or stair, not a base block.");
        return slabItemSupplier.get();
    }

    /**
     * Returns the stairs {@link Item} that corresponds to this base block item.
     *
     * @throws IllegalStateException if called on a slab or stair item, or before
     *                               suppliers have been wired up by {@link info.atlasv.decorative_distractions.tint.TintEntry}.
     */
    public Item getStairsItem() {
        if (stairsItemSupplier == null)
            throw new IllegalStateException(
                    "getStairsItem() called on a TintBlockItem with no stairs supplier — " +
                            "this item may be a slab or stair, not a base block.");
        return stairsItemSupplier.get();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(TOOLTIP_KEY));
    }

    /**
     * Reads the stored colour from the item's {@link DataComponents#DYED_COLOR} component.
     * Returns {@link TintBlockEntity#DEFAULT_COLOR} if no colour has been applied.
     */
    public int getColor(ItemStack stack) {
        DyedItemColor dyed = stack.get(DataComponents.DYED_COLOR);
        return dyed != null ? dyed.rgb() : TintBlockEntity.DEFAULT_COLOR;
    }

    /**
     * Reads the dye history from the items
     * {@link TintDataComponents#DYE_HISTORY} component.
     * Returns {@link TintDyeHistory#EMPTY} if no history has been recorded
     * (e.g. a block dyed before this feature was added).
     */
    public TintDyeHistory getDyeHistory(ItemStack stack) {
        return stack.getOrDefault(TintDataComponents.DYE_HISTORY.get(), TintDyeHistory.EMPTY);
    }
}