package info.atlasv.decorative_distractions.core.datagen;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.basic.datagen.BasicBlockTags;
import info.atlasv.decorative_distractions.tint.datagen.TintBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CoreBlockTagProvider extends BlockTagsProvider {

    public CoreBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, DecorativeDistractions.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        BasicBlockTags.addTags(this);
        TintBlockTags.addTags(this);
    }

    public IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> getTag(TagKey<Block> key) {
        return tag(key);
    }

    @Override
    public String getName() {
        return "Block Tags";
    }
}