package info.atlasv.decorative_distractions.basic.datagen;

import info.atlasv.decorative_distractions.basic.block.BasicStoneBlockSets;
import info.atlasv.decorative_distractions.basic.block.BasicStoneBlockSets.StoneVariant;
import info.atlasv.decorative_distractions.basic.block.BasicAmethystBlockSets;
import info.atlasv.decorative_distractions.basic.block.BasicAmethystBlockSets.AmethystVariant;
import info.atlasv.decorative_distractions.core.blocksets.Amethyst;
import info.atlasv.decorative_distractions.core.blocksets.Stone;
import info.atlasv.decorative_distractions.core.datagen.CoreBlockTagProvider;
import info.atlasv.decorative_distractions.core.datagen.tags.*;
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
        // Custom crystal variants
        for (AmethystVariant variant : AmethystVariant.values()) {
            Amethyst crystal = BasicAmethystBlockSets.CRYSTALS.get(variant);

            // AmethystLike: block, slab, stairs
            for (AmethystLike entry : AmethystLike.values()) {
                if (entry.blockTag == null) continue;
                IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> tag = p.getTag(entry.blockTag);
                tag.add(
                        crystal.block.get(),
                        crystal.slab.get(),
                        crystal.stairs.get()
                );
            }

            // AmethystBuddingLike: budding block
            for (AmethystBuddingLike entry : AmethystBuddingLike.values()) {
                if (entry.blockTag == null) continue;
                p.getTag(entry.blockTag).add(crystal.buddingBlock.get());
            }

            // AmethystClusterLike: cluster
            for (AmethystClusterLike entry : AmethystClusterLike.values()) {
                if (entry.blockTag == null) continue;
                p.getTag(entry.blockTag).add(crystal.cluster.get());
            }

            // AmethystBudLike: large, medium, small buds
            for (AmethystBudLike entry : AmethystBudLike.values()) {
                if (entry.blockTag == null) continue;
                IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> tag = p.getTag(entry.blockTag);
                tag.add(
                        crystal.largeBud.get(),
                        crystal.mediumBud.get(),
                        crystal.smallBud.get()
                );
            }
        }

        // Vanilla amethyst slab & stairs
        // Tagged with AmethystLike (same as their base block) - item tags skipped for now.
        for (AmethystLike entry : AmethystLike.values()) {
            if (entry.blockTag == null) continue;
            IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> tag = p.getTag(entry.blockTag);
            tag.add(
                    BasicAmethystBlockSets.VANILLA_AMETHYST_SLAB.get(),
                    BasicAmethystBlockSets.VANILLA_AMETHYST_STAIRS.get()
            );
        }


    }
}