package org.leodreamer.wildcard_pattern.api;

import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlag;

import java.util.Set;

public interface IMaterial {
    Set<MaterialFlag> wildcard$getFlags();
}
