package info.atlasv.decorative_distractions.core.blocksets;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Stone {

    // Identity
    public final String name;
    public final String texturePath;

    // Block Holders
    public final DeferredBlock<Block> block;
    public final DeferredBlock<StairBlock> stairs;
    public final DeferredBlock<SlabBlock> slab;
    public final DeferredBlock<WallBlock> wall;
    public final DeferredBlock<PressurePlateBlock> pressurePlate;
    public final DeferredBlock<ButtonBlock> button;

    /**
     * Registers a full stone-type block set and their BlockItems.
     *
     * @param name        The base registry name (e.g. "mossy_cobblestone").
     *                    Variants are suffixed automatically (_stairs, _slab, etc.)
     * @param texturePath The resource location path to the single shared texture
     *                    (e.g. "block/mossy_cobblestone"). Stored for use in datagen.
     * @param blocks      The module's DeferredRegister.Blocks instance.
     * @param items       The module's DeferredRegister<Item> instance.
     */
    public Stone(
            String name,
            String texturePath,
            DeferredRegister.Blocks blocks,
            DeferredRegister<Item> items
    ) {
        this.name = name;
        this.texturePath = texturePath;

        // Shared base properties, override per-variant if needed.
        BlockBehaviour.Properties baseProps = BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .requiresCorrectToolForDrops()
                .strength(1.5f, 6.0f)
                .sound(SoundType.STONE);

        // Lesser properties for not solid variants.
        BlockBehaviour.Properties lightProps = BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE)
                .noCollission()
                .strength(0.5f);

        // Basic Block RegistrationBasic
        this.block = blocks.register(name,
                () -> new Block(baseProps));
        this.stairs = blocks.register(name + "_stairs",
                () -> new StairBlock(this.block.get().defaultBlockState(), baseProps));
        this.slab = blocks.register(name + "_slab",
                () -> new SlabBlock(baseProps));
        this.wall = blocks.register(name + "_wall",
                () -> new WallBlock(baseProps));
        this.pressurePlate = blocks.register(name + "_pressure_plate",
                () -> new PressurePlateBlock(BlockSetType.STONE, lightProps));

        // 20 ticks = standard press time for stone buttons I think?
        this.button = blocks.register(name + "_button",
                () -> new ButtonBlock(BlockSetType.STONE, 20, lightProps));

        // Basic Item RegistrationBasic
        registerItem(items, name, this.block);
        registerItem(items, name + "_stairs", this.stairs);
        registerItem(items, name + "_slab", this.slab);
        registerItem(items, name + "_wall", this.wall);
        registerItem(items, name + "_pressure_plate", this.pressurePlate);
        registerItem(items, name + "_button", this.button);
    }

    private <T extends Block> void registerItem(
            DeferredRegister<Item> items,
            String id,
            DeferredBlock<T> deferredBlock
    ) {
        items.register(id, () -> new BlockItem(deferredBlock.get(), new Item.Properties()));
    }
}