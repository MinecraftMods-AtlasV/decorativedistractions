package info.atlasv.decorative_distractions.tint.datagen;

import info.atlasv.decorative_distractions.lights.datagen.LightsBlockStateProvider;
import info.atlasv.decorative_distractions.lights.datagen.LightsItemModelProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class TintDataGenerators {
    @SubscribeEvent
    public static void register(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

//        generator.addProvider(event.includeClient(), new BaseBlockTextures(packOutput));
//
//        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
//                List.of(new LootTableProvider.SubProviderEntry(BaseBlockLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));
//        generator.addProvider(event.includeServer(), new BaseRecipeProvider(packOutput, lookupProvider));
//
//        BlockTagsProvider blockTagsProvider = new BaseBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
//        generator.addProvider(event.includeServer(), blockTagsProvider);
//        generator.addProvider(event.includeClient(), new BaseItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
//
//        generator.addProvider(event.includeClient(), new BaseItemModelProvider(packOutput, existingFileHelper));
//        generator.addProvider(event.includeClient(), new BaseBlockStateProvider(packOutput, existingFileHelper));
//
//        generator.addProvider(event.includeServer(), new BaseDataMapProvider(packOutput, lookupProvider));
//
//        generator.addProvider(event.includeClient(), new BaseLangProvider(packOutput));
        generator.addProvider(event.includeClient(), new TintBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new TintRecipeProvider(packOutput, lookupProvider));
        TintBlockTagProvider blockTags = generator.addProvider(event.includeServer(), new TintBlockTagProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeClient(), new TintItemTagProvider(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
    }
}
