package org.leodreamer.wildcard_pattern.gui;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKey;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.lowdragmc.lowdraglib.gui.widget.PhantomSlotWidget;
import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class PhantomGTMaterialSlot extends PhantomSlotWidget {

    private final Predicate<Material> validator;

    public PhantomGTMaterialSlot(
        IItemTransfer itemHandler,
        int slotIndex,
        int xPosition,
        int yPosition,
        Predicate<Material> validator
    ) {
        super(itemHandler, slotIndex, xPosition, yPosition);
        this.validator = validator;
    }

    public void setMaterial(Material material) {
        var handler = getHandler();
        if (handler == null) return;
        handler.set(findExampleForMaterial(material));
    }

    public Material getMaterial() {
        var handler = getHandler();
        if (handler == null) return GTMaterials.NULL;
        return getItemMaterial(handler.getItem());
    }

    private static Material getItemMaterial(ItemStack stack) {
        var entry = ChemicalHelper.getMaterial(stack.getItem());
        return entry == null ? GTMaterials.NULL : entry.material();
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        validator.test(getMaterial());
    }

    private static ItemStack findExampleForMaterial(Material material) {
        if (material == GTMaterials.NULL) return ItemStack.EMPTY;

        // dust & fluid first
        var dust = ChemicalHelper.get(TagPrefix.dust, material);
        if (!dust.isEmpty()) return dust;

        try {
            var fluid = material.getFluid();
            return fluid.getBucket().getDefaultInstance();
        } catch (Exception ignored) {
        }

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
            } catch (Exception ignored) {
            }
        }
        return ItemStack.EMPTY;
    }
}
