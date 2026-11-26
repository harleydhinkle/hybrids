package net.wolfking068.hybrids.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.wolfking068.hybrids.Hybrids;

public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Hybrids.MOD_ID);

    public static final RegistryObject<CreativeModeTab> HYBRIDS = CREATIVE_MODE_TABS.register("hybrids",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hybrids"))
                    .icon(() -> new ItemStack(ModItems.WOLF_COIN.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.WOLF_COIN.get());
                        output.accept(ModItems.RAW_WOLF_COIN.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
