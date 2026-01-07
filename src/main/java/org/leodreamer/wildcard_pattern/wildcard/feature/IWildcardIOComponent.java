package org.leodreamer.wildcard_pattern.wildcard.feature;

import appeng.api.stacks.GenericStack;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import org.jetbrains.annotations.Nullable;

public interface IWildcardIOComponent extends IWildcardSerializable<IWildcardIOComponent>, IWildcardComponentUI {

    @Nullable
    GenericStack apply(Material material);
}
