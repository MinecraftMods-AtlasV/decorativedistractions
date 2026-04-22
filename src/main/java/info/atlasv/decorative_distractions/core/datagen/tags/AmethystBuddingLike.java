package info.atlasv.decorative_distractions.core.datagen.tags;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public enum AmethystBuddingLike {
    MINEABLE_WITH_PICKAXE       (BlockTags.MINEABLE_WITH_PICKAXE, null),
    CRYSTAL_SOUND_BLOCKS       (BlockTags.CRYSTAL_SOUND_BLOCKS, null),

    // Neoforge/Common tags
    BUDDING_BLOCKS                (Tags.Blocks.BUDDING_BLOCKS, Tags.Items.BUDDING_BLOCKS);

    public final TagKey<Block> blockTag;
    @Nullable
    public final TagKey<Item> itemTag;

    AmethystBuddingLike(TagKey<Block> blockTag, @Nullable TagKey<Item> itemTag) {
        this.blockTag = blockTag;
        this.itemTag = itemTag;
    }
}
