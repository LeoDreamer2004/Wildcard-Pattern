package org.leodreamer.wildcard_pattern.gui;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKey;
import com.gregtechceu.gtceu.api.gui.widget.PhantomSlotWidget;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.function.Predicate;

public class PhantomGTMaterialSlot extends PhantomSlotWidget {

    public PhantomGTMaterialSlot(
        IItemHandlerModifiable itemHandler,
        int slotIndex,
        int xPosition,
        int yPosition,
        Predicate<Material> validator
    ) {
        super(itemHandler, slotIndex, xPosition, yPosition, (stack) -> validator.test(getItemMaterial(stack)));
    }

    public void setMaterial(Material material) {
        setItem(findExampleForMaterial(material));
    }

    public Material getMaterial() {
        return getItemMaterial(getItem());
    }

    private static Material getItemMaterial(ItemStack stack) {
        return ChemicalHelper.getMaterialEntry(stack.getItem()).material();
    }

    private static ItemStack findExampleForMaterial(Material material) {
        if (material == GTMaterials.NULL) return ItemStack.EMPTY;

        // dust & fluid first
        var dust = ChemicalHelper.get(TagPrefix.dust, material);
        if (!dust.isEmpty()) return dust;

        try {
            var fluid = material.getFluid();
            return fluid.getBucket().getDefaultInstance();
        } catch (Exception ignored) {}

        // Maybe some very special materials. Scan it.
        for (var tag : TagPrefix.values()) {
            var stack = ChemicalHelper.get(tag, material);
            if (!stack.isEmpty()) return stack;
        }

        for (var tag : FluidStorageKey.allKeys()) {
            try {
                var fluid = material.getProperty(PropertyKey.FLUID).getStorage().get(tag);
                if (fluid == null) continue;
                return fluid.getBucket().getDefaultInstance();
            } catch (Exception ignored) {}
        }
        return ItemStack.EMPTY;
    }
}
