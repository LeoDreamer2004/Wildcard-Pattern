package org.leodreamer.wildcard_pattern.wildcard.impl;

import appeng.api.stacks.GenericStack;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.leodreamer.wildcard_pattern.gui.NaiveItemTransfer;
import org.leodreamer.wildcard_pattern.gui.GenericGTTag;
import org.leodreamer.wildcard_pattern.gui.PhantomGTTagSlot;
import org.leodreamer.wildcard_pattern.wildcard.WildcardSerializers;
import org.leodreamer.wildcard_pattern.wildcard.feature.IWildcardIOComponent;

public class TagIOComponent implements IWildcardIOComponent {

    private GenericGTTag tag;
    private int amount;

    private PhantomGTTagSlot tagSlot;
    private LabelWidget tagLabel;
    private TextFieldWidget amountEdit;
    private static final IGuiTexture GROUP_BG = ResourceBorderTexture.BUTTON_COMMON.copy()
        .setColor(ColorPattern.PURPLE.color);

    public static TagIOComponent empty() {
        return new TagIOComponent(GenericGTTag.EMPTY, 1);
    }

    public TagIOComponent(GenericGTTag tag, int amount) {
        this.tag = tag;
        this.amount = amount;
    }

    @Override
    public GenericStack apply(Material material) {
        return tag.toGenericStack(material, amount);
    }

    @Override
    public IWildcardSerializer<IWildcardIOComponent> getSerializer() {
        return WildcardSerializers.IO_TAG;
    }

    @Override
    public void createUILine(WidgetGroup line) {
        line.setBackground(GROUP_BG);
        tagLabel = new LabelWidget(25, 7, tag.name());

        tagSlot = new PhantomGTTagSlot(new NaiveItemTransfer(), 0, 3, 3, this::updateTag);

        if (tag != GenericGTTag.EMPTY) {
            tagSlot.setTag(tag);
        }

        amountEdit = new TextFieldWidget(80, 5, 50, 15, this::getAmount, this::setAmount);
        amountEdit.setNumbersOnly(0, Integer.MAX_VALUE);
        amountEdit.setCurrentString(amount);

        line.addWidget(tagSlot);
        line.addWidget(tagLabel);
        line.addWidget(new LabelWidget(70, 7, "x"));
        line.addWidget(amountEdit);
    }

    private boolean updateTag(GenericGTTag tag) {
        var ok = tag != GenericGTTag.EMPTY;
        if (ok) {
            tagLabel.setText(tag.name());
            this.tag = tag;
        }
        return ok;
    }

    private String getAmount() {
        return Integer.toString(amount);
    }

    private void setAmount(String str) {
        amount = Integer.parseInt(str);
    }

    @Override
    public void onSave() {
        updateTag(tagSlot.getTag());
        amount = Integer.parseInt(amountEdit.getCurrentString());
    }

    @Override
    public String toString() {
        return "Component " + tag.name() + " x " + amount;
    }

    public static class Serializer implements IWildcardSerializer<IWildcardIOComponent> {

        @Override
        public String key() {
            return "tag_prefix";
        }

        @Override
        public @NotNull CompoundTag serialize(IWildcardIOComponent component) {
            var tagComponent = (TagIOComponent) component;
            var nbt = new CompoundTag();
            nbt.put("tag", tagComponent.tag.toNBT());
            nbt.putInt("amount", tagComponent.amount);
            return nbt;
        }

        @Override
        public @NotNull IWildcardIOComponent deserialize(CompoundTag nbt) {
            var amount = nbt.getInt("amount");
            if (nbt.get("tag") instanceof CompoundTag tagNBT) {
                var tag = GenericGTTag.fromNBT(tagNBT);
                return new TagIOComponent(tag, amount);
            }
            return empty();
        }
    }
}
