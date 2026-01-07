package org.leodreamer.wildcard_pattern.wildcard.feature;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;

import java.util.function.Predicate;

public interface IWildcardFilterComponent
    extends Predicate<Material>, IWildcardSerializable<IWildcardFilterComponent>, IWildcardComponentUI {

    boolean isWhitelist();

    void setWhitelist(boolean whiteList);
}
