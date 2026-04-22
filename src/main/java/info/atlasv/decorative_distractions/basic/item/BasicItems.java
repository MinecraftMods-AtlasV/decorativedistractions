package info.atlasv.decorative_distractions.basic.item;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.basic.block.BasicAmethystBlockSets;
import info.atlasv.decorative_distractions.core.blocksets.Amethyst;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class BasicItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DecorativeDistractions.MODID);

    // -------------------------------------------------------------------------
    // Mortar and pestle — stays in the crafting grid after each use
    // -------------------------------------------------------------------------
    public static final DeferredItem<Item> MORTAR_AND_PESTLE =
            ITEMS.register("mortar_and_pestle", () -> new Item(new Item.Properties()
                    .stacksTo(1)
            ) {
                @Override
                public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
                    return new ItemStack(MORTAR_AND_PESTLE.get());
                }

                @Override
                public boolean hasCraftingRemainingItem(ItemStack stack) {
                    return true;
                }

                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.decorative_distractions.not_consumed_in_crafting"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    // -------------------------------------------------------------------------
    // Vanilla amethyst dust — standalone, not part of AmethystVariant
    // -------------------------------------------------------------------------
    public static final DeferredItem<Item> AMETHYST_DUST =
            ITEMS.register("amethyst_dust", () -> new Item(new Item.Properties()));

    // -------------------------------------------------------------------------
    // Crystal dust — one entry per AmethystVariant
    // -------------------------------------------------------------------------
    public static final Map<BasicAmethystBlockSets.AmethystVariant, DeferredItem<Item>> CRYSTAL_DUSTS =
            new EnumMap<>(BasicAmethystBlockSets.AmethystVariant.class);

    static {
        for (BasicAmethystBlockSets.AmethystVariant variant : BasicAmethystBlockSets.AmethystVariant.values()) {
            DeferredItem<Item> dust = ITEMS.register(
                    variant.getName() + "_dust",
                    () -> new Item(new Item.Properties())
            );
            CRYSTAL_DUSTS.put(variant, dust);
        }
    }

    // -------------------------------------------------------------------------
    // Purple (vanilla amethyst) pigment — standalone, not part of AmethystVariant
    // -------------------------------------------------------------------------
    public static final DeferredItem<Item> PURPLE_PIGMENT =
            ITEMS.register("purple_pigment", () -> new Item(new Item.Properties()));

    // -------------------------------------------------------------------------
    // Crystal pigments — one entry per AmethystVariant
    // -------------------------------------------------------------------------
    public static final Map<BasicAmethystBlockSets.AmethystVariant, DeferredItem<Item>> CRYSTAL_PIGMENTS =
            new EnumMap<>(BasicAmethystBlockSets.AmethystVariant.class);

    static {
        for (BasicAmethystBlockSets.AmethystVariant variant : BasicAmethystBlockSets.AmethystVariant.values()) {
            DeferredItem<Item> pigment = ITEMS.register(
                    variant.getColour() + "_pigment",
                    () -> new Item(new Item.Properties())
            );
            CRYSTAL_PIGMENTS.put(variant, pigment);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
    public static Collection<DeferredHolder<Item, ? extends Item>> getEntries() {
        return ITEMS.getEntries();
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    // -------------------------------------------------------------------------
    // Datagen — models
    // -------------------------------------------------------------------------
    public static void registerModels(ItemModelProvider provider) {

        // Crystal shards
        for (Map.Entry<BasicAmethystBlockSets.AmethystVariant, Amethyst> entry
                : BasicAmethystBlockSets.CRYSTALS.entrySet()) {

            Amethyst set    = entry.getValue();
            String   colour = entry.getKey().getColour();

            provider.withExistingParent(
                    set.shard.getId().getPath(),
                    provider.mcLoc("item/generated")
            ).texture("layer0", provider.modLoc("item/crystals/" + colour + "/crystal"));
        }

        // Crystal dusts
        for (Map.Entry<BasicAmethystBlockSets.AmethystVariant, DeferredItem<Item>> entry
                : CRYSTAL_DUSTS.entrySet()) {

            String colour   = entry.getKey().getColour();
            String itemPath = entry.getValue().getId().getPath();

            provider.withExistingParent(
                    itemPath,
                    provider.mcLoc("item/generated")
            ).texture("layer0", provider.modLoc("item/crystals/" + colour + "/dust"));
        }

        // Crystal pigments
        for (Map.Entry<BasicAmethystBlockSets.AmethystVariant, DeferredItem<Item>> entry
                : CRYSTAL_PIGMENTS.entrySet()) {

            String colour   = entry.getKey().getColour();
            String itemPath = entry.getValue().getId().getPath();

            provider.withExistingParent(
                    itemPath,
                    provider.mcLoc("item/generated")
            ).texture("layer0", provider.modLoc("item/pigments/" + colour));
        }

        // Mortar and pestle
        provider.withExistingParent(
                MORTAR_AND_PESTLE.getId().getPath(),
                provider.mcLoc("item/generated")
        ).texture("layer0", provider.modLoc("item/mortarandpestle"));

        // Vanilla amethyst dust
        provider.withExistingParent(
                AMETHYST_DUST.getId().getPath(),
                provider.mcLoc("item/generated")
        ).texture("layer0", provider.modLoc("item/crystals/purple/dust"));

        // Purple pigment
        provider.withExistingParent(
                PURPLE_PIGMENT.getId().getPath(),
                provider.mcLoc("item/generated")
        ).texture("layer0", provider.modLoc("item/pigments/purple"));
    }

    // -------------------------------------------------------------------------
    // Datagen — recipes
    // -------------------------------------------------------------------------
    public static void addRecipes(RecipeOutput output) {
        addMortarAndPestleRecipe(output);
        addCrystalDustRecipes(output);
        addVanillaAmethystDustRecipe(output);
        addCrystalPigmentRecipes(output);
        addVanillaAmethystPigmentRecipe(output);
    }

    private static void addMortarAndPestleRecipe(RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, MORTAR_AND_PESTLE.get())
                .requires(net.minecraft.world.item.Items.DIRT)
                .unlockedBy("has_dirt",
                        InventoryChangeTrigger.TriggerInstance.hasItems(net.minecraft.world.item.Items.STONE))
                .save(output);
    }

    private static void addCrystalDustRecipes(RecipeOutput output) {
        for (BasicAmethystBlockSets.AmethystVariant variant : BasicAmethystBlockSets.AmethystVariant.values()) {
            Amethyst           crystal = BasicAmethystBlockSets.CRYSTALS.get(variant);
            DeferredItem<Item> dust    = CRYSTAL_DUSTS.get(variant);
            String             name    = variant.getName();

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, dust.get())
                    .requires(crystal.shard.get())
                    .requires(MORTAR_AND_PESTLE.get())
                    .unlockedBy("has_" + name,
                            InventoryChangeTrigger.TriggerInstance.hasItems(crystal.shard.get()))
                    .save(output, DecorativeDistractions.MODID + ":" + name + "_dust_from_" + name);
        }
    }

    private static void addVanillaAmethystDustRecipe(RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AMETHYST_DUST.get())
                .requires(net.minecraft.world.item.Items.AMETHYST_SHARD)
                .requires(MORTAR_AND_PESTLE.get())
                .unlockedBy("has_amethyst_shard",
                        InventoryChangeTrigger.TriggerInstance.hasItems(net.minecraft.world.item.Items.AMETHYST_SHARD))
                .save(output, DecorativeDistractions.MODID + ":amethyst_dust_from_amethyst");
    }

    private static void addCrystalPigmentRecipes(RecipeOutput output) {
        for (BasicAmethystBlockSets.AmethystVariant variant : BasicAmethystBlockSets.AmethystVariant.values()) {
            DeferredItem<Item> dust    = CRYSTAL_DUSTS.get(variant);
            DeferredItem<Item> pigment = CRYSTAL_PIGMENTS.get(variant);
            String             colour  = variant.getColour();

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, pigment.get(), 4)
                    .requires(dust.get())
                    .requires(MORTAR_AND_PESTLE.get())
                    .unlockedBy("has_" + colour + "_dust",
                            InventoryChangeTrigger.TriggerInstance.hasItems(dust.get()))
                    .save(output, DecorativeDistractions.MODID + ":" + colour + "_pigment_from_" + colour + "_dust");
        }
    }

    private static void addVanillaAmethystPigmentRecipe(RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PURPLE_PIGMENT.get(), 4)
                .requires(AMETHYST_DUST.get())
                .requires(MORTAR_AND_PESTLE.get())
                .unlockedBy("has_amethyst_dust",
                        InventoryChangeTrigger.TriggerInstance.hasItems(AMETHYST_DUST.get()))
                .save(output, DecorativeDistractions.MODID + ":purple_pigment_from_amethyst_dust");
    }
}