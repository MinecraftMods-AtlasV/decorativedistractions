package info.atlasv.decorative_distractions.tint.client;

import info.atlasv.decorative_distractions.tint.block.TintBlockEntity;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

// TODO: Think of a better way to do this that doesn't utterly obliterate vanilla method
public class GrassOnTintBlockColor {

    public static void registerColors(RegisterColorHandlersEvent.Block event) {

        // SHORT_GRASS
        event.register((state, level, pos, tintIndex) -> {
            if (level == null || pos == null) return GrassColor.getDefaultColor();
            int tint = getTintFromBelow(level, pos.below());
            if (tint != -1) return tint;
            return BiomeColors.getAverageGrassColor(level, pos); // vanilla fallback
        }, Blocks.SHORT_GRASS);

        // TALL_GRASS - upper half samples from pos.below() to match vanilla behaviour
        event.register((state, level, pos, tintIndex) -> {
            if (level == null || pos == null) return GrassColor.getDefaultColor();
            boolean isUpper = state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
            BlockPos samplePos = isUpper ? pos.below() : pos;
            int tint = getTintFromBelow(level, samplePos.below());
            if (tint != -1) return tint;
            return BiomeColors.getAverageGrassColor(level, samplePos); // vanilla fallback
        }, Blocks.TALL_GRASS);
    }

    private static int getTintFromBelow(BlockAndTintGetter level, BlockPos groundPos) {
        if (level.getBlockEntity(groundPos) instanceof TintBlockEntity tbe) {
            return tbe.getColour();
        }
        return -1;
    }
}