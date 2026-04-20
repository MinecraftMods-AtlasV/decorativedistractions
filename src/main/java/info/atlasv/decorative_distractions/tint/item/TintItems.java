package info.atlasv.decorative_distractions.tint.item;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.List;

public class TintItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, DecorativeDistractions.MODID);

    public static final DeferredHolder<Item, Item> PAINTBRUSH_ITEM =
            ITEMS.register("paintbrush", () -> new Item(new Item.Properties()
                    .stacksTo(1)
            ){
                // Prevents the item being consumed in crafting recipes {
                @Override
                public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
                    return new ItemStack(TintItems.PAINTBRUSH_ITEM.get());
                }

                @Override
                public boolean hasCraftingRemainingItem(ItemStack stack) {
                    return true;
                }
                // }

                // Adds tooltip to the item stating it isn't consumed in recipes
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.decorative_distractions.not_consumed_in_crafting"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static Collection<DeferredHolder<Item, ? extends Item>> getEntries() {
        return ITEMS.getEntries();
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    public static void registerModels(ItemModelProvider provider) {
        provider.withExistingParent(
                PAINTBRUSH_ITEM.getId().getPath(),
                provider.mcLoc("item/generated")
        ).texture("layer0", provider.modLoc("item/paintbrush"));
    }
}