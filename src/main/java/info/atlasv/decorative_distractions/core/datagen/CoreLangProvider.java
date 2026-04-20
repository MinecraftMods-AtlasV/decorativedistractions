package info.atlasv.decorative_distractions.core.datagen;

import info.atlasv.decorative_distractions.basic.block.BasicAmethystBlockSets;
import info.atlasv.decorative_distractions.basic.block.BasicStoneBlockSets;
import info.atlasv.decorative_distractions.basic.item.BasicItems;
import info.atlasv.decorative_distractions.core.utils.TextFormatting;
import info.atlasv.decorative_distractions.lights.block.LightsBlocks;
import info.atlasv.decorative_distractions.lights.item.LightsItems;
import info.atlasv.decorative_distractions.tint.block.TintBlocks;
import info.atlasv.decorative_distractions.tint.item.TintItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.Collection;
import java.util.stream.Stream;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CoreLangProvider extends LanguageProvider {

    public CoreLangProvider(PackOutput output) {
        super(output, "decorative_distractions", "en_us");
    }

    @Override
    protected void addTranslations() {
        // MANUAL
        // Creative Tabs
        add("creativetab.decorative_distractions.decorative_distractions_tab", "Decorative Distractions");

        // Lights
        add("block.decorative_distractions.light_panel.inverted", "Light Mode: Inverted");
        add("block.decorative_distractions.light_panel.normal", "Light Mode: Normal");

        // Tooltips
        add("tooltip.decorative_distractions.not_consumed_in_crafting", "§7This item is not consumed in crafting");

        // AUTOMATED BULLSHITTERY
        // Blocks (and their BlockItems via inherited translation key)
        getBlockEntries().forEach(holder -> {
            String path = holder.getKey().location().getPath();
            String stem = path.endsWith("_block") ? path.substring(0, path.length() - 6) : null;
            add(holder.get(), stem != null && BasicAmethystBlockSets.AMETHYST_VARIANT_NAMES.contains(stem)
                    ? TextFormatting.altTitleCase(path)
                    : TextFormatting.titleCase(path));
        });

        // Standalone items only (shards, etc.)
        getItemEntries()
                .filter(holder -> !(holder.get() instanceof BlockItem))
                .forEach(holder -> {
                    String path = holder.getKey().location().getPath();
                    add(holder.get(), TextFormatting.titleCase(path));
                });
    }

    private Stream<DeferredHolder<Block, ? extends Block>> getBlockEntries() {
        // TintBlocks.getEntries() returns List<TintEntry> rather than block holders directly,
        // so its blocks are extracted per-entry and concated separately.
        Stream<DeferredHolder<Block, ? extends Block>> tintBlockStream = TintBlocks.getEntries().stream()
                .flatMap(entry -> Stream.of(entry.block, entry.slab, entry.stairs));

        return Stream.concat(
                Stream.of(
                        BasicAmethystBlockSets.getEntries(),
                        BasicStoneBlockSets.getEntries(),
                        LightsBlocks.getEntries()
                ).flatMap(Collection::stream),
                tintBlockStream
        );
    }

    private Stream<DeferredHolder<Item, ? extends Item>> getItemEntries() {
        return Stream.of(
                BasicItems.getEntries(),
                LightsItems.getEntries(),
                TintItems.getEntries()
        ).flatMap(Collection::stream);
    }
}