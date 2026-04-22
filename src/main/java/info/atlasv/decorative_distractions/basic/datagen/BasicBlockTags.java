package info.atlasv.decorative_distractions.basic.datagen;

import info.atlasv.decorative_distractions.basic.block.BasicStoneBlockSets;
import info.atlasv.decorative_distractions.basic.block.BasicStoneBlockSets.StoneVariant;
import info.atlasv.decorative_distractions.core.blocksets.Stone;
import info.atlasv.decorative_distractions.core.datagen.CoreBlockTagProvider;
import info.atlasv.decorative_distractions.core.datagen.tags.StoneLike;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;

public class BasicBlockTags {
    public static void addTags(CoreBlockTagProvider p) {
        for (StoneLike entry : StoneLike.values()) {
            IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> tag = p.getTag(entry.blockTag);

            for (Map<StoneVariant, Stone> map : List.of(
                    BasicStoneBlockSets.SMOOTH_VARIANTS,
                    BasicStoneBlockSets.SMOOTH_BRICKS_VARIANTS)) {
                for (Stone stone : map.values()) {
                    tag.add(
                            stone.block.get(),
                            stone.stairs.get(),
                            stone.slab.get(),
                            stone.wall.get(),
                            stone.pressurePlate.get(),
                            stone.button.get()
                    );
                }
            }
        }

    }
}