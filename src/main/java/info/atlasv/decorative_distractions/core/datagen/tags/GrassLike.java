package info.atlasv.decorative_distractions.core.datagen.tags;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public enum GrassLike {

    MOSS_REPLACEABLE                (BlockTags.MOSS_REPLACEABLE, null),
    AZALEA_GROWS_ON                 (BlockTags.AZALEA_GROWS_ON, null),
    WOLVES_SPAWNABLE_ON             (BlockTags.WOLVES_SPAWNABLE_ON, null),
    ARMADILLO_SPAWNABLE_ON          (BlockTags.ARMADILLO_SPAWNABLE_ON, null),
    SCULK_REPLACEABLE               (BlockTags.SCULK_REPLACEABLE, null),
    DIRT                            (BlockTags.DIRT, ItemTags.DIRT),
    AZALEA_ROOT_REPLACEABLE         (BlockTags.AZALEA_ROOT_REPLACEABLE,null),
    SCULK_REPLACEABLE_WORLD_GEN     (BlockTags.SCULK_REPLACEABLE_WORLD_GEN, null),
    MINEABLE_WITH_SHOVEL            (BlockTags.MINEABLE_WITH_SHOVEL, null),
    RABBITS_SPAWNABLE_ON            (BlockTags.RABBITS_SPAWNABLE_ON, null),
    VALID_SPAWN                     (BlockTags.VALID_SPAWN, null),
    BIG_DRIPLEAF_PLACEABLE          (BlockTags.BIG_DRIPLEAF_PLACEABLE, null),
    NETHER_CARVER_REPLACEABLES      (BlockTags.NETHER_CARVER_REPLACEABLES, null),
    SNIFFER_DIGGABLE_BLOCK          (BlockTags.SNIFFER_DIGGABLE_BLOCK, null),
    OVERWORLD_CARVER_REPLACEABLES   (BlockTags.OVERWORLD_CARVER_REPLACEABLES, null),
    LUSH_GROUND_REPLACEABLE         (BlockTags.LUSH_GROUND_REPLACEABLE, null),
    BAMBOO_PLANTABLE_ON             (BlockTags.BAMBOO_PLANTABLE_ON, null),
    ENDERMAN_HOLDABLE               (BlockTags.ENDERMAN_HOLDABLE, null),
    FOXES_SPAWNABLE_ON              (BlockTags.FOXES_SPAWNABLE_ON, null),
    GOATS_SPAWNABLE_ON              (BlockTags.GOATS_SPAWNABLE_ON, null),
    FROGS_SPAWNABLE_ON              (BlockTags.FROGS_SPAWNABLE_ON, null),
    PARROTS_SPAWNABLE_ON            (BlockTags.PARROTS_SPAWNABLE_ON, null),
    DEAD_BUSH_MAY_PLACE_ON          (BlockTags.DEAD_BUSH_MAY_PLACE_ON, null),
    ANIMALS_SPAWNABLE_ON            (BlockTags.ANIMALS_SPAWNABLE_ON, null);

    public final TagKey<Block> blockTag;
    @Nullable public final TagKey<Item> itemTag;

    GrassLike(TagKey<Block> blockTag, @Nullable TagKey<Item> itemTag) {
        this.blockTag = blockTag;
        this.itemTag = itemTag;
    }
}
