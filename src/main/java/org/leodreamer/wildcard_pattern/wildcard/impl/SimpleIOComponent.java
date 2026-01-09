package org.leodreamer.wildcard_pattern.wildcard.impl;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.leodreamer.wildcard_pattern.gui.NaiveItemTransfer;
import org.leodreamer.wildcard_pattern.util.MathUtils;
import org.leodreamer.wildcard_pattern.wildcard.WildcardSerializers;
import org.leodreamer.wildcard_pattern.wildcard.feature.IWildcardIOComponent;

public class SimpleIOComponent implements IWildcardIOComponent {

    @NotNull
    private GenericStack stack;
    private SlotWidget itemSlot;
    private TextFieldWidget amountEdit;
    private static final IGuiTexture GROUP_BG = ResourceBorderTexture.BUTTON_COMMON.copy()
        .setColor(ColorPattern.CYAN.color);

    public static SimpleIOComponent empty() {
        return new SimpleIOComponent(new GenericStack(AEItemKey.of(Items.AIR), 1));
    }

    public SimpleIOComponent(@NotNull GenericStack stack) {
        this.stack = stack;
    }

    @Override
    public GenericStack apply(Material material) {
        if (stack.what() instanceof AEItemKey item && item.getItem() instanceof BucketItem bucket) {
            return GenericStack.fromFluidStack(new FluidStack(bucket.getFluid(), MathUtils.saturatedCast(stack.amount())));
        }
        return stack;
    }

    @Override
    public IWildcardSerializer<IWildcardIOComponent> getSerializer() {
        return WildcardSerializers.IO_SIMPLE;
    }

    @Override
    public void createUILine(WidgetGroup line) {
        line.setBackground(GROUP_BG);

        itemSlot = new PhantomSlotWidget(new NaiveItemTransfer(), 0, 3, 3);
        if (stack.what() instanceof AEItemKey item) {
            var handler = itemSlot.getHandler();
            if (handler != null) {
                handler.set(item.toStack());
            }
        }
        amountEdit = new TextFieldWidget(80, 5, 50, 15, this::getAmount, this::setAmount);
        amountEdit.setNumbersOnly(0, Integer.MAX_VALUE);

        line.addWidget(itemSlot);
        line.addWidget(new LabelWidget(70, 7, "x"));
        line.addWidget(amountEdit);
    }

    private String getAmount() {
        return Integer.toString(MathUtils.saturatedCast(stack.amount()));
    }

    private void setAmount(String str) {
        stack = new GenericStack(stack.what(), Integer.parseInt(str));
    }

    @Override
    public void onSave() {
        var handler = itemSlot.getHandler();
        if (handler == null) return;

        var itemStack = handler.getItem().copy();
        int count = Integer.parseInt(amountEdit.getCurrentString());
        itemStack.setCount(count);
        var genericStack = GenericStack.fromItemStack(itemStack);
        stack = genericStack == null ? empty().stack : genericStack;
    }

    @Override
    public String toString() {
        return "Component " + stack;
    }

    public static class Serializer implements IWildcardSerializer<IWildcardIOComponent> {

        @Override
        public String key() {
            return "simple";
        }

        @Override
        public @NotNull CompoundTag serialize(IWildcardIOComponent component) {
            return GenericStack.writeTag(((SimpleIOComponent) component).stack);
        }

        @Override
        public @NotNull SimpleIOComponent deserialize(CompoundTag nbt) {
            var stack = GenericStack.readTag(nbt);
            return stack == null ? empty() : new SimpleIOComponent(stack);
        }
    }
}
