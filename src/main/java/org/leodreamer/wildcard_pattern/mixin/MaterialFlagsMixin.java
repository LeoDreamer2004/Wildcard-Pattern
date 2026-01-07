package org.leodreamer.wildcard_pattern.mixin;

import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlag;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import org.leodreamer.wildcard_pattern.api.IMaterialFlags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

@Mixin(MaterialFlags.class)
public class MaterialFlagsMixin implements IMaterialFlags {

    @Shadow(remap = false)
    @Final
    private Set<MaterialFlag> flags;

    @Unique
    @Override
    public Set<MaterialFlag> sftcore$getFlags() {
        return flags;
    }
}
