package info.atlasv.decorative_distractions.tint.datagen;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.core.datagen.tags.CobblestoneLike;
import info.atlasv.decorative_distractions.core.datagen.tags.GrassLike;
import info.atlasv.decorative_distractions.core.datagen.tags.StoneLike;
import info.atlasv.decorative_distractions.tint.block.TintBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TintBlockTagProvider extends BlockTagsProvider {

    public TintBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, DecorativeDistractions.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Add block tags here as needed (e.g. mineable/pickaxe, needs_stone_tool).

        // TINTED MOSS tags
        for (GrassLike entry : GrassLike.values()) {
            if (entry.blockTag != null) {
                tag(entry.blockTag).add(TintBlocks.TINTED_GRASS_BLOCK.block.get());
            }
        }

        // TINTED STONE tags
        for (StoneLike entry : StoneLike.values()) {
            tag(entry.blockTag).add(TintBlocks.TINTED_STONE.block.get());
        }

          // TINTED COBBLESTONE tags
        for (CobblestoneLike entry : CobblestoneLike.values()) {
            if (entry.blockTag != null) {
                tag(entry.blockTag).add(TintBlocks.TINTED_COBBLESTONE.block.get());
            }
        }
    }
}