package org.leodreamer.wildcard_pattern.gui;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.lowdragmc.lowdraglib.gui.widget.PhantomSlotWidget;
import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class PhantomGTTagSlot extends PhantomSlotWidget {
    private final Predicate<GenericGTTag> validator;

    public PhantomGTTagSlot(
        IItemTransfer itemHandler,
        int slotIndex,
        int xPosition,
        int yPosition,
        Predicate<GenericGTTag> validator
    ) {
        super(itemHandler, slotIndex, xPosition, yPosition);
        this.validator = validator;
    }

    public void setTag(GenericGTTag tag) {
        var handler = getHandler();
        if (handler == null) return;
        handler.set(findExampleForTag(tag));
    }

    public GenericGTTag getTag() {
        var handler = getHandler();
        if (handler == null) return GenericGTTag.EMPTY;
        return getItemTag(handler.getItem());
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        validator.test(getTag());
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
