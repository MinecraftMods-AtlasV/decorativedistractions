package info.atlasv.decorative_distractions.core.datagen.tags;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public enum AmethystLike {
    MINEABLE_WITH_PICKAXE       (BlockTags.MINEABLE_WITH_PICKAXE, null),
    CRYSTAL_SOUND_BLOCKS       (BlockTags.CRYSTAL_SOUND_BLOCKS, null),
    VIBRATION_RESONATORS       (BlockTags.VIBRATION_RESONATORS, null),

    // Neoforge/Common tags
    GEMS                (null, Tags.Items.GEMS);

    public final TagKey<Block> blockTag;
    @Nullable
    public final TagKey<Item> itemTag;

    AmethystLike(TagKey<Block> blockTag, @Nullable TagKey<Item> itemTag) {
        this.blockTag = blockTag;
        this.itemTag = itemTag;
    }
}
