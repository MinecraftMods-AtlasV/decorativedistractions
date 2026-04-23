package info.atlasv.decorative_distractions.core.datagen;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.basic.block.BasicAmethystBlockSets;
import info.atlasv.decorative_distractions.basic.item.BasicItems;
import info.atlasv.decorative_distractions.core.datagen.tags.CobblestoneLike;
import info.atlasv.decorative_distractions.core.datagen.tags.GrassLike;
import info.atlasv.decorative_distractions.core.datagen.tags.StoneLike;
import info.atlasv.decorative_distractions.tint.block.TintBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CoreItemTagProvider extends ItemTagsProvider {

    public CoreItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
                               @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, DecorativeDistractions.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
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

        // Dye tags - crystal pigments (c:dyes/<colour>)
        // Each AmethystVariant.getColour() returns a vanilla dye colour name,
        // so it maps directly to the common tag convention.
        for (BasicAmethystBlockSets.AmethystVariant variant : BasicAmethystBlockSets.AmethystVariant.values()) {
            TagKey<Item> dyeTag = dyeTag(variant.getColour());
            tag(dyeTag).add(BasicItems.CRYSTAL_PIGMENTS.get(variant).get());
        }

        // Purple pigment uses vanilla amethyst (no variant), maps to c:dyes/purple
        tag(dyeTag("purple")).add(BasicItems.PURPLE_PIGMENT.get());
    }

    /**
     * Builds a {@code c:dyes/<colour>} common tag key.
     * Using ResourceLocation directly rather than Tags.Items to avoid
     * hardcoding a specific NeoForge tags constant per colour.
     */
    private static TagKey<Item> dyeTag(String colour) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyes/" + colour));
    }
}