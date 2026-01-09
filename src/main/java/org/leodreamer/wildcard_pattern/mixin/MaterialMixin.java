package org.leodreamer.wildcard_pattern.mixin;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlag;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import org.jetbrains.annotations.NotNull;
import org.leodreamer.wildcard_pattern.api.IMaterial;
import org.leodreamer.wildcard_pattern.api.IMaterialFlags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

@Mixin(Material.class)
public class MaterialMixin implements IMaterial {
    @Shadow(remap = false)
    @Final
    private @NotNull MaterialFlags flags;

    @Unique
    @Override
    public Set<MaterialFlag> wildcard$getFlags() {
        return ((IMaterialFlags) flags).wildcard$getFlags();
    }
}
