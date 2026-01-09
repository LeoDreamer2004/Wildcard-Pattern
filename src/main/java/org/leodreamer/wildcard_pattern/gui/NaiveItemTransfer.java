package org.leodreamer.wildcard_pattern.gui;

import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NaiveItemTransfer implements IItemTransfer {

    @NotNull
    private ItemStack stack = ItemStack.EMPTY;

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return stack;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate, boolean notifyChanges) {
        this.stack = stack;
        return stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate, boolean notifyChanges) {
        this.stack = ItemStack.EMPTY;
        return stack;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.getItem() == this.stack.getItem();
    }

    @Override
    @SuppressWarnings("all")
    public @NotNull Object createSnapshot() {
        return stack;
    }

    @Override
    @SuppressWarnings("all")
    public void restoreFromSnapshot(Object snapshot) {
        if (snapshot instanceof ItemStack itemStack) {
            stack = itemStack;
        }
    }
}
