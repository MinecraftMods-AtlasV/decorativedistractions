package info.atlasv.decorative_distractions.core.datagen.tags;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public enum CobblestoneLike {

    MINEABLE_WITH_PICKAXE       (BlockTags.MINEABLE_WITH_PICKAXE, null),
    STONE_CRAFTING_MATERIALS    (null, ItemTags.STONE_CRAFTING_MATERIALS),
    STONE_TOOL_MATERIALS        (null, ItemTags.STONE_TOOL_MATERIALS),

    // Neoforge/Common tags
    COBBLESTONES                (Tags.Blocks.COBBLESTONES, Tags.Items.COBBLESTONES);

    public final TagKey<Block> blockTag;
    @Nullable
    public final TagKey<Item> itemTag;

    CobblestoneLike(TagKey<Block> blockTag, @Nullable TagKey<Item> itemTag) {
        this.blockTag = blockTag;
        this.itemTag = itemTag;
    }
}
