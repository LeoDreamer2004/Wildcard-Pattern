package org.leodreamer.wildcard_pattern.wildcard.feature;

import net.minecraft.nbt.CompoundTag;
import org.leodreamer.wildcard_pattern.api.ICustomSerializable;

public interface IWildcardSerializable<T extends IWildcardSerializable<T>>
    extends ICustomSerializable<T, CompoundTag, IWildcardSerializable.IWildcardSerializer<T>> {

    interface IWildcardSerializer<T> extends ISerializer<T, CompoundTag> {

        String key();
    }
}
