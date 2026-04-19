package info.atlasv.decorative_distractions.core;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.basic.block.BasicAmethystBlockSets;
import info.atlasv.decorative_distractions.basic.block.BasicStoneBlockSets;
import info.atlasv.decorative_distractions.core.blocksets.Amethyst;
import info.atlasv.decorative_distractions.core.blocksets.Stone;
import info.atlasv.decorative_distractions.lights.block.LightsBlocks;
import info.atlasv.decorative_distractions.tint.block.TintBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class CreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DecorativeDistractions.MODID);

    public static final Supplier<CreativeModeTab> DECORATIVE_DISTRACTIONS_TAB = CREATIVE_MODE_TAB.register("decorative_distractions_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(BasicStoneBlockSets.SMOOTH_VARIANTS.get(BasicStoneBlockSets.StoneVariant.ANDESITE).block.get()))
                    .title(Component.translatable("creativetab.decorative_distractions.decorative_distractions_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        for (BasicAmethystBlockSets.AmethystVariant variant : BasicAmethystBlockSets.AmethystVariant.values()) {
                            Amethyst set = BasicAmethystBlockSets.CRYSTALS.get(variant);
                            output.accept(set.block.get());
                            output.accept(set.buddingBlock.get());
                            output.accept(set.cluster.get());
                            output.accept(set.largeBud.get());
                            output.accept(set.mediumBud.get());
                            output.accept(set.smallBud.get());
                            output.accept(set.shard.get());
                            output.accept(set.slab.get());
                            output.accept(set.stairs.get());
                        }
                        if (BasicAmethystBlockSets.VANILLA_AMETHYST_SLAB != null) {
                            output.accept(BasicAmethystBlockSets.VANILLA_AMETHYST_SLAB.get());
                            output.accept(BasicAmethystBlockSets.VANILLA_AMETHYST_STAIRS.get());
                        }
                        for (BasicStoneBlockSets.StoneVariant variant : BasicStoneBlockSets.StoneVariant.values()) {
                            Stone set = BasicStoneBlockSets.SMOOTH_VARIANTS.get(variant);
                            output.accept(set.block.get());
                            output.accept(set.stairs.get());
                            output.accept(set.slab.get());
                            output.accept(set.wall.get());
                            output.accept(set.pressurePlate.get());
                            output.accept(set.button.get());
                        }
                        for (BasicStoneBlockSets.StoneVariant variant : BasicStoneBlockSets.StoneVariant.values()) {
                            Stone set = BasicStoneBlockSets.SMOOTH_BRICKS_VARIANTS.get(variant);
                            output.accept(set.block.get());
                            output.accept(set.stairs.get());
                            output.accept(set.slab.get());
                            output.accept(set.wall.get());
                            output.accept(set.pressurePlate.get());
                            output.accept(set.button.get());
                        }
                        for (LightsBlocks.MetalVariant metal : LightsBlocks.MetalVariant.values()) {
                                Map<DyeColor, DeferredBlock<Block>> colourMap = LightsBlocks.LIGHT_PANELS.get(metal);

                                for (DyeColor dye : DyeColor.values()) {
                                    output.accept(colourMap.get(dye).get());
                                }

                        }
                        for (var holder : TintBlocks.ITEMS.getEntries()) {
                            output.accept(holder.get());
                        }
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
