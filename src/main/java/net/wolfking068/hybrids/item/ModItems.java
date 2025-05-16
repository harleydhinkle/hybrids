package net.wolfking068.hybrids.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.wolfking068.hybrids.Hybrids;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Hybrids.MOD_ID);

    public static final RegistryObject<Item> WOLF_COIN =
            ITEMS.register("wolf_coin",()-> new Item(new Item.Properties().tab(ModCreativeModeTab.hybrids)));
    public static final RegistryObject<Item> RAW_WOLF_COIN =
            ITEMS.register("raw_wolf_coin",()-> new Item(new Item.Properties().tab(ModCreativeModeTab.hybrids)));

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
