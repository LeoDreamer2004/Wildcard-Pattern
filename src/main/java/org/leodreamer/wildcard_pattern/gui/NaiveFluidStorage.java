package org.leodreamer.wildcard_pattern.gui;

import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidStorage;
import org.jetbrains.annotations.NotNull;

public class NaiveFluidStorage implements IFluidStorage {

    @NotNull
    private FluidStack stack = FluidStack.empty();

    public NaiveFluidStorage() {
    }

    @Override
    @SuppressWarnings("all")
    public long fill(int i, FluidStack fluidStack, boolean b, boolean b1) {
        stack = fluidStack;
        return 1;
    }

    @Override
    public boolean supportsFill(int i) {
        return false;
    }

    @Override
    @SuppressWarnings("all")
    public @NotNull FluidStack drain(int i, FluidStack fluidStack, boolean b, boolean b1) {
        return null;
    }

    @Override
    public boolean supportsDrain(int i) {
        return false;
    }

    @Override
    @SuppressWarnings("all")
    public @NotNull Object createSnapshot() {
        return stack;
    }

    @Override
    @SuppressWarnings("all")
    public void restoreFromSnapshot(Object snapshot) {
        if (snapshot instanceof FluidStack fluidStack) {
            stack = fluidStack;
        }
    }

    @Override
    public @NotNull FluidStack getFluid() {
        return stack;
    }

    @Override
    public void setFluid(FluidStack fluidStack) {
        stack = fluidStack;
    }

    @Override
    public long getCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isFluidValid(FluidStack fluidStack) {
        return true;
    }
}
