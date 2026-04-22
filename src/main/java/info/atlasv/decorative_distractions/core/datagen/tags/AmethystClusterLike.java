package info.atlasv.decorative_distractions.core.datagen.tags;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public enum AmethystClusterLike {
    MINEABLE_WITH_PICKAXE       (BlockTags.MINEABLE_WITH_PICKAXE, null),

    // Neoforge/Common tags
    CLUSTERS                (Tags.Blocks.CLUSTERS, Tags.Items.CLUSTERS);

    public final TagKey<Block> blockTag;
    @Nullable
    public final TagKey<Item> itemTag;

    AmethystClusterLike(TagKey<Block> blockTag, @Nullable TagKey<Item> itemTag) {
        this.blockTag = blockTag;
        this.itemTag = itemTag;
    }
}
