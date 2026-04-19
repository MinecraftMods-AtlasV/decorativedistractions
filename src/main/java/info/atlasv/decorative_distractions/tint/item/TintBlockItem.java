package info.atlasv.decorative_distractions.tint.item;

import info.atlasv.decorative_distractions.tint.TintDataComponents;
import info.atlasv.decorative_distractions.tint.TintDyeHistory;
import info.atlasv.decorative_distractions.tint.block.TintBlockEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.Block;

/**
 * The BlockItem for any tintable block.
 * Contains only logic, all registration lives in {@link info.atlasv.decorative_distractions.tint.block.TintBlocks}
 * via {@link info.atlasv.decorative_distractions.tint.TintEntry}.
 */
public class TintBlockItem extends BlockItem {

    public TintBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
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