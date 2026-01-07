package org.leodreamer.wildcard_pattern.gui;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.gui.widget.PhantomSlotWidget;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.function.Predicate;

public class PhantomGTTagSlot extends PhantomSlotWidget {

    public PhantomGTTagSlot(
        IItemHandlerModifiable itemHandler,
        int slotIndex,
        int xPosition,
        int yPosition,
        Predicate<GenericGTTag> validator
    ) {
        super(itemHandler, slotIndex, xPosition, yPosition, (stack) -> validator.test(getItemTag(stack)));
    }

    public void setTag(GenericGTTag tag) {
        setItem(findExampleForTag(tag));
    }

    public GenericGTTag getTag() {
        return getItemTag(getItem());
    }

    private static ItemStack findExampleForTag(GenericGTTag tag) {
        for (var mat : GTCEuAPI.materialManager.getRegisteredMaterials()) {
            var stack = tag.createItemOrBucket(mat);
            if (!stack.isEmpty()) return stack;
        }
        return ItemStack.EMPTY;
    }

    private static GenericGTTag getItemTag(ItemStack stack) {
        return GenericGTTag.fromItemOrBucket(stack.getItem());
    }
}
