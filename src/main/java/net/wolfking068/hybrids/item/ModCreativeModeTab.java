package net.wolfking068.hybrids.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeModeTab {
    public static final CreativeModeTab hybrids = new CreativeModeTab("hybrids") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.WOLF_COIN.get());
        }
    };
}
