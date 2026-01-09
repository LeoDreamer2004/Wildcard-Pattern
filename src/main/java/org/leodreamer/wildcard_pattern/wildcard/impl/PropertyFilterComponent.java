package org.leodreamer.wildcard_pattern.wildcard.impl;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.SelectorWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.leodreamer.wildcard_pattern.api.IMaterialProperties;
import org.leodreamer.wildcard_pattern.gui.NaiveItemTransfer;
import org.leodreamer.wildcard_pattern.gui.PhantomGTMaterialSlot;
import org.leodreamer.wildcard_pattern.lang.DataGenScanned;
import org.leodreamer.wildcard_pattern.lang.RegisterLanguage;
import org.leodreamer.wildcard_pattern.wildcard.WildcardSerializers;
import org.leodreamer.wildcard_pattern.wildcard.feature.IWildcardFilterComponent;

import java.util.List;

import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.*;

@DataGenScanned
public class PropertyFilterComponent implements IWildcardFilterComponent {

    private Material example;
    @NotNull
    private PropertyKey<?> property;
    @Getter
    private boolean whitelist;

    private PhantomGTMaterialSlot exampleSlot;
    private SelectorWidget propertySelector;
    private WidgetGroup parent = null;

    private static final IGuiTexture GROUP_BG_WHITE = ResourceBorderTexture.BUTTON_COMMON.copy()
        .setColor(ColorPattern.YELLOW.color);
    private static final IGuiTexture GROUP_BG_BLACK = ResourceBorderTexture.BUTTON_COMMON.copy()
        .setColor(ColorPattern.ORANGE.color);

    private static final PropertyKey<?>[] ALL_PROPERTY_KEYS = new PropertyKey[] { EMPTY, BLAST, ALLOY_BLAST, DUST,
        FLUID_PIPE, FLUID, GEM, INGOT, POLYMER, ITEM_PIPE, ORE, TOOL, ROTOR, WIRE, WOOD, HAZARD };

    public static PropertyFilterComponent empty() {
        return new PropertyFilterComponent(EMPTY, GTMaterials.NULL, false);
    }

    public PropertyFilterComponent(@NotNull PropertyKey<?> property, Material example, boolean whitelist) {
        this.example = example;
        this.property = property;
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
        return whitelist == material.hasProperty(property);
    }

    @Override
    public void createUILine(WidgetGroup line) {
        line.setBackground(whitelist ? GROUP_BG_WHITE : GROUP_BG_BLACK);
        parent = line;

        propertySelector = new MySelectorWidget(25, 5, 80, 15, getMaterialPropertyNames(example));
        propertySelector.setOnChanged(this::updateProperty);
        exampleSlot = new PhantomGTMaterialSlot(new NaiveItemTransfer(), 0, 3, 3, this::changeExample);

        if (example != GTMaterials.NULL) {
            exampleSlot.setMaterial(example);
        }
        propertySelector.setValue(property.toString());

        line.addWidget(exampleSlot);
        line.addWidget(propertySelector);
    }

    @Override
    public IWildcardSerializer<IWildcardFilterComponent> getSerializer() {
        return WildcardSerializers.FILTER_PROPERTY;
    }

    private boolean changeExample(Material material) {
        var ok = material != GTMaterials.NULL;
        if (ok) {
            this.example = material;
            var flags = getMaterialPropertyNames(material);
            propertySelector.setCandidates(flags);
            propertySelector.setValue(flags.get(0));
            updateProperty(flags.get(0));
        }
        return ok;
    }

    private void updateProperty(String propName) {
        if (propName == null || propName.isEmpty()) {
            property = EMPTY;
        } else {
            property = getPropertyByName(propName);
        }
    }

    private static PropertyKey<?> getPropertyByName(String propName) {
        for (var key : ALL_PROPERTY_KEYS) {
            if (key.toString().equalsIgnoreCase(propName)) {
                return key;
            }
        }
        return EMPTY;
    }

    @Override
    public void onSave() {
        example = exampleSlot.getMaterial();
        updateProperty(propertySelector.getValue());
    }

    @RegisterLanguage("no property")
    private static final String NO_PROPERTY = "sftcore.item.wildcard_pattern.filter.property.no_property";

    private static List<String> getMaterialPropertyNames(Material material) {
        var flags = ((IMaterialProperties) material.getProperties()).wildcard$getProperties();
        if (flags.isEmpty()) {
            return List.of(Component.translatable(NO_PROPERTY).getString());
        }
        return flags.stream().map(PropertyKey::toString).toList();
    }

    public static class Serializer implements IWildcardSerializer<IWildcardFilterComponent> {

        @Override
        public String key() {
            return "property";
        }

        @Override
        public @NotNull CompoundTag serialize(IWildcardFilterComponent component) {
            var tag = new CompoundTag();
            var simple = (PropertyFilterComponent) component;
            tag.putBoolean("whitelist", simple.whitelist);
            tag.putString("example", simple.example.getResourceLocation().toString());
            tag.putString("flag", simple.property.toString());
            return tag;
        }

        @Override
        public @NotNull IWildcardFilterComponent deserialize(CompoundTag nbt) {
            var whitelist = nbt.getBoolean("whitelist");
            var materialId = nbt.getString("example");
            var flag = getPropertyByName(nbt.getString("flag"));
            var material = GTCEuAPI.materialManager.getMaterial(materialId);
            if (material == null) material = GTMaterials.NULL;
            return new PropertyFilterComponent(flag, material, whitelist);
        }
    }

    private static class MySelectorWidget extends SelectorWidget {

        public MySelectorWidget(int x, int y, int width, int height, List<String> candidates) {
            super(x, y, width, height, candidates, ColorPattern.WHITE.color);
            button.setBackground(GuiTextures.BUTTON);
            textTexture.setColor(ColorPattern.DARK_GRAY.color);
        }
    }
}
