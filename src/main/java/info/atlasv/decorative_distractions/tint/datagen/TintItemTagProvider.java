package info.atlasv.decorative_distractions.tint.datagen;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.core.datagen.tags.CobblestoneLike;
import info.atlasv.decorative_distractions.core.datagen.tags.GrassLike;
import info.atlasv.decorative_distractions.core.datagen.tags.StoneLike;
import info.atlasv.decorative_distractions.tint.block.TintBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TintItemTagProvider extends ItemTagsProvider {

    public TintItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
                               @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, DecorativeDistractions.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // All tint block items must be in DYEABLE so vanilla's armordye recipe applies.
        // Iterating TintBlocks.ITEMS means new entries are covered automatically.
        // Not needed now because of custom implimentation that isn't as relyable as vanilla but lets me have more
        // control over colours and the crafting steps for /TintCalc (or /GetTint)
//        var dyeableTag = tag(ItemTags.DYEABLE);
//        TintBlocks.ITEMS.getEntries().forEach(holder ->
//                dyeableTag.add(holder.get())
//        );

        for (GrassLike entry : GrassLike.values()) {
            if (entry.itemTag != null) {
                tag(entry.itemTag).add(TintBlocks.TINTED_GRASS_BLOCK.item.get());
            }
        }

        for (StoneLike entry : StoneLike.values()) {
            if (entry.itemTag != null) {
                tag(entry.itemTag).add(TintBlocks.TINTED_STONE.item.get());
            }
        }

        for (CobblestoneLike entry : CobblestoneLike.values()) {
            if (entry.itemTag != null) {
                tag(entry.itemTag).add(TintBlocks.TINTED_COBBLESTONE.item.get());
            }
        }
    }
}