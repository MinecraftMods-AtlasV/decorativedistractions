package info.atlasv.decorative_distractions.core.datagen.tags;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public enum StoneLike {

    SCULK_REPLACEABLE_WORLD_GEN     (BlockTags.SCULK_REPLACEABLE_WORLD_GEN, null),
    MINEABLE_WITH_PICKAXE           (BlockTags.MINEABLE_WITH_PICKAXE, null),
    STONE_ORE_REPLACEABLES          (BlockTags.STONE_ORE_REPLACEABLES, null),
    SCULK_REPLACEABLE               (BlockTags.SCULK_REPLACEABLE, null),
    NETHER_CARVER_REPLACEABLES      (BlockTags.NETHER_CARVER_REPLACEABLES, null),
    OVERWORLD_CARVER_REPLACEABLES   (BlockTags.OVERWORLD_CARVER_REPLACEABLES, null),
    MOSS_REPLACEABLE                (BlockTags.MOSS_REPLACEABLE, null),
    SNAPS_GOAT_HORN                 (BlockTags.SNAPS_GOAT_HORN, null),
    AZALEA_ROOT_REPLACEABLE         (BlockTags.AZALEA_ROOT_REPLACEABLE, null),
    BASE_STONE_OVERWORLD            (BlockTags.BASE_STONE_OVERWORLD, null),
    DRIPSTONE_REPLACEABLE           (BlockTags.DRIPSTONE_REPLACEABLE, null),
    LUSH_GROUND_REPLACEABLE         (BlockTags.LUSH_GROUND_REPLACEABLE, null),

    // Neoforge/Common tags
    STONES                          (Tags.Blocks.STONES, Tags.Items.STONES),
    ORE_BEARING_GROUND_STONE        (Tags.Blocks.ORE_BEARING_GROUND_STONE, Tags.Items.ORE_BEARING_GROUND_STONE);

    public final TagKey<Block> blockTag;
    @Nullable
    public final TagKey<Item> itemTag;

    StoneLike(TagKey<Block> blockTag, @Nullable TagKey<Item> itemTag) {
        this.blockTag = blockTag;
        this.itemTag = itemTag;
    }
}
