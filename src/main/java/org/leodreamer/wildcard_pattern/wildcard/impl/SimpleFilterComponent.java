package org.leodreamer.wildcard_pattern.wildcard.impl;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.leodreamer.wildcard_pattern.gui.NaiveItemTransfer;
import org.leodreamer.wildcard_pattern.gui.PhantomGTMaterialSlot;
import org.leodreamer.wildcard_pattern.lang.DataGenScanned;
import org.leodreamer.wildcard_pattern.lang.RegisterLanguage;
import org.leodreamer.wildcard_pattern.wildcard.WildcardSerializers;
import org.leodreamer.wildcard_pattern.wildcard.feature.IWildcardFilterComponent;

@DataGenScanned
public class SimpleFilterComponent implements IWildcardFilterComponent {

    private Material material;
    @Getter
    private boolean whitelist;
    private PhantomGTMaterialSlot materialSlot;
    private LabelWidget materialLabel;
    private WidgetGroup parent = null;

    private static final IGuiTexture GROUP_BG_WHITE = ResourceBorderTexture.BUTTON_COMMON.copy()
        .setColor(ColorPattern.LIGHT_BLUE.color);
    private static final IGuiTexture GROUP_BG_BLACK = ResourceBorderTexture.BUTTON_COMMON.copy()
        .setColor(ColorPattern.BLUE.color);

    public static SimpleFilterComponent empty() {
        return new SimpleFilterComponent(GTMaterials.NULL, false);
    }

    public SimpleFilterComponent(Material material, boolean whitelist) {
        this.material = material;
        this.whitelist = whitelist;
    }

    @Override
    public void setWhitelist(boolean whiteList) {
        this.whitelist = whiteList;
        if (parent != null) {
            parent.setBackground(whiteList ? GROUP_BG_WHITE : GROUP_BG_BLACK);
        }
    }

    @Override
    public boolean test(Material material) {
        return whitelist == (this.material == material);
    }

    @Override
    public void createUILine(WidgetGroup line) {
        line.setBackground(whitelist ? GROUP_BG_WHITE : GROUP_BG_BLACK);
        parent = line;
        materialLabel = new LabelWidget(25, 7, getMaterialString(material));

        materialSlot = new PhantomGTMaterialSlot(new NaiveItemTransfer(), 0, 3, 3, this::updateMaterial);

        if (material != GTMaterials.NULL) {
            materialSlot.setMaterial(material);
        }

        line.addWidget(materialSlot);
        line.addWidget(materialLabel);
    }

    @Override
    public IWildcardSerializer<IWildcardFilterComponent> getSerializer() {
        return WildcardSerializers.FILTER_SIMPLE;
    }

    private boolean updateMaterial(Material material) {
        var ok = material != GTMaterials.NULL;
        if (ok) {
            this.material = material;
            materialLabel.setText(getMaterialString(material));
        }
        return ok;
    }

    @Override
    public void onSave() {
        updateMaterial(materialSlot.getMaterial());
    }

    @RegisterLanguage("No material")
    private static final String NO_MATERIAL = "sftcore.item.wildcard_pattern.filter.simple.no_material";

    private static String getMaterialString(Material material) {
        if (material == GTMaterials.NULL) {
            return Component.translatable(NO_MATERIAL).getString();
        } else {
            return material.getLocalizedName().getString();
        }
    }

    public static class Serializer implements IWildcardSerializer<IWildcardFilterComponent> {

        @Override
        public String key() {
            return "simple";
        }

        @Override
        public @NotNull CompoundTag serialize(IWildcardFilterComponent component) {
            var tag = new CompoundTag();
            var simple = (SimpleFilterComponent) component;
            tag.putBoolean("whitelist", simple.whitelist);
            tag.putString("material", simple.material.getResourceLocation().toString());
            return tag;
        }

        @Override
        public @NotNull IWildcardFilterComponent deserialize(CompoundTag nbt) {
            var whitelist = nbt.getBoolean("whitelist");
            var materialId = nbt.getString("material");
            var material = GTCEuAPI.materialManager.getMaterial(materialId);
            if (material == null) material = GTMaterials.NULL;
            return new SimpleFilterComponent(material, whitelist);
        }
    }
}
