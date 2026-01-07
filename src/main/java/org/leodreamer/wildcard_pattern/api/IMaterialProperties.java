package org.leodreamer.wildcard_pattern.api;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;

import java.util.Set;

public interface IMaterialProperties {

    Set<PropertyKey<?>> wildcard$getProperties();
}
