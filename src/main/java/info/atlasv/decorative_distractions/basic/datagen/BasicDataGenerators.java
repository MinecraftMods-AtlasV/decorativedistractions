package info.atlasv.decorative_distractions.basic.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BasicDataGenerators {
    @SubscribeEvent
    public static void register(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

//        generator.addProvider(event.includeClient(), new BasicBlockTextures(packOutput));

        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(BasicLootTableProvider::new, LootContextParamSets.BLOCK)),
                lookupProvider));

//        BlockTagsProvider blockTagsProvider = new BasicBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
//        generator.addProvider(event.includeServer(), blockTagsProvider);
//        generator.addProvider(event.includeClient(), new BasicItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));

        generator.addProvider(event.includeClient(), new BasicItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new BasicBlockStateProvider(packOutput, existingFileHelper));

//        generator.addProvider(event.includeServer(), new BasicDataMapProvider(packOutput, lookupProvider));

//        generator.addProvider(event.includeClient(), new BasicLangProvider(packOutput));

    }
}