package org.leodreamer.wildcard_pattern.api;

/**
 * An interface for objects that can be serialized and deserialized using a custom serializer.
 */
public interface ICustomSerializable<SELF extends ICustomSerializable<SELF, DATA, SER>, DATA,
    SER extends ICustomSerializable.ISerializer<SELF, DATA>> extends ISerializable<SELF, DATA, SER> {

    SER getSerializer();

    @Override
    @SuppressWarnings("unchecked")
    default DATA encode() {
        return getSerializer().serialize((SELF) this);
    }

    interface ISerializer<SELF, DATA> extends IDeserializer<SELF, DATA> {

        DATA serialize(SELF obj);
    }
}
