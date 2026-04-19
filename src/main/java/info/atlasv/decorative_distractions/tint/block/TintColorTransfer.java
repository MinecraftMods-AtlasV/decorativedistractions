package info.atlasv.decorative_distractions.tint.block;

import info.atlasv.decorative_distractions.tint.TintDataComponents;
import info.atlasv.decorative_distractions.tint.TintDyeHistory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

    // helpers that transfer tint colour and dye history between a
    // Block and it's item stack
    // Needed or else the undyed version is dropped, placed, picked
public final class TintColorTransfer {

    private TintColorTransfer() {}

    /** Item -> BlockEntity on placement. Call from {@code setPlacedBy}. */
    public static void itemToEntity(Level level, BlockPos pos, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof TintBlockEntity be) {
            DyedItemColor dyedColor = stack.get(DataComponents.DYED_COLOR);
            if (dyedColor != null) {
                be.setColour(dyedColor.rgb());
            }
            TintDyeHistory history = stack.getOrDefault(
                    TintDataComponents.DYE_HISTORY.get(), TintDyeHistory.EMPTY);
            be.setDyeHistory(history);
        }
    }

    /** BlockEntity -> drop item on survival break. Call from {@code playerDestroy}. */
    public static void entityToDrop(Level level, BlockPos pos,
                                    @Nullable BlockEntity blockEntity,
                                    Block block) {
        if (!level.isClientSide && blockEntity instanceof TintBlockEntity be) {
            ItemStack drop = new ItemStack(block.asItem());

            int color = be.getColour();
            if (color != TintBlockEntity.DEFAULT_COLOR) {
                drop.set(DataComponents.DYED_COLOR, new DyedItemColor(color, true));
            }

            TintDyeHistory history = be.getDyeHistory();
            if (!history.isEmpty()) {
                drop.set(TintDataComponents.DYE_HISTORY.get(), history);
            }

            Block.popResource(level, pos, drop);
        }
    }

    /** BlockEntity -> item on middle-click pick. Call from {@code getCloneItemStack}. */
    public static void entityToStack(LevelReader level, BlockPos pos, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof TintBlockEntity be) {
            int color = be.getColour();
            if (color != TintBlockEntity.DEFAULT_COLOR) {
                stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color, true));
            }
            TintDyeHistory history = be.getDyeHistory();
            if (!history.isEmpty()) {
                stack.set(TintDataComponents.DYE_HISTORY.get(), history);
            }
        }
    }
}