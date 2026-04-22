package info.atlasv.decorative_distractions.core.datagen.tags;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public enum StoneLike {

    MINEABLE_WITH_PICKAXE           (BlockTags.MINEABLE_WITH_PICKAXE, null),
    SCULK_REPLACEABLE               (BlockTags.SCULK_REPLACEABLE, null),
    LUSH_GROUND_REPLACEABLE         (BlockTags.LUSH_GROUND_REPLACEABLE, null),

    // Neoforge/Common tags
    STONES                          (Tags.Blocks.STONES, Tags.Items.STONES);

    public final TagKey<Block> blockTag;
    @Nullable
    public final TagKey<Item> itemTag;

    StoneLike(TagKey<Block> blockTag, @Nullable TagKey<Item> itemTag) {
        this.blockTag = blockTag;
        this.itemTag = itemTag;
    }
}
