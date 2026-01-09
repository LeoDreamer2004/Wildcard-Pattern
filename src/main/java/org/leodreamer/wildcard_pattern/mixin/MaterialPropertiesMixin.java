package org.leodreamer.wildcard_pattern.mixin;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.IMaterialProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.MaterialProperties;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import org.leodreamer.wildcard_pattern.api.IMaterialProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Set;

@Mixin(MaterialProperties.class)
public class MaterialPropertiesMixin implements IMaterialProperties {

    @Shadow(remap = false)
    @Final
    private Map<PropertyKey<? extends IMaterialProperty<?>>, IMaterialProperty<?>> propertyMap;

    @Override
    public Set<PropertyKey<?>> wildcard$getProperties() {
        return propertyMap.keySet();
    }
}
