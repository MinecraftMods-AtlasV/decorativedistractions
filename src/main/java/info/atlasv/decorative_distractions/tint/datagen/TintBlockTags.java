package info.atlasv.decorative_distractions.tint.datagen;

import info.atlasv.decorative_distractions.core.datagen.CoreBlockTagProvider;
import info.atlasv.decorative_distractions.core.datagen.tags.CobblestoneLike;
import info.atlasv.decorative_distractions.core.datagen.tags.GrassLike;
import info.atlasv.decorative_distractions.core.datagen.tags.StoneLike;
import info.atlasv.decorative_distractions.tint.block.TintBlocks;

public class TintBlockTags {
    public static void addTags(CoreBlockTagProvider p) {
        for (GrassLike entry : GrassLike.values()) {
            if (entry.blockTag != null) {
                p.getTag(entry.blockTag).add(
                        TintBlocks.TINTED_GRASS_BLOCK.block.get(),
                        TintBlocks.TINTED_GRASS_BLOCK.stairs.get(),
                        TintBlocks.TINTED_GRASS_BLOCK.slab.get()
                );
            }
        }

        for (StoneLike entry : StoneLike.values()) {
            p.getTag(entry.blockTag).add(
                    TintBlocks.TINTED_STONE.block.get(),
                    TintBlocks.TINTED_STONE.stairs.get(),
                    TintBlocks.TINTED_STONE.slab.get()
            );
        }

        for (CobblestoneLike entry : CobblestoneLike.values()) {
            if (entry.blockTag != null) {
                p.getTag(entry.blockTag).add(
                        TintBlocks.TINTED_COBBLESTONE.block.get(),
                        TintBlocks.TINTED_COBBLESTONE.stairs.get(),
                        TintBlocks.TINTED_COBBLESTONE.slab.get()
                );
            }
        }
    }
}