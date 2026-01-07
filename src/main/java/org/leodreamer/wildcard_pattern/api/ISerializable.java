package org.leodreamer.wildcard_pattern.api;

/**
 * Generic serialization interface.
 *
 * @param <SELF> The type of the implementing class.
 * @param <DATA> The type of the serialized data.
 * @param <DE>   The type of the deserializer.
 */
public interface ISerializable<SELF extends ISerializable<SELF, DATA, DE>, DATA,
    DE extends ISerializable.IDeserializer<SELF, DATA>> {

    /**
     * Encodes the object into the specified data format.
     */
    DATA encode();

    /**
     * Decodes the object from the specified data using the provided deserializer.
     * <p>
     * Note that Java does not allow static methods in interfaces to be overridden,
     * so this method cannot be abstract like <code>static decode(DATA data)</code>.
     * </p>
     */
    static <SELF, DATA, DE extends IDeserializer<SELF, DATA>> SELF decodeBy(DE deserializer, DATA data) {
        return deserializer.deserialize(data);
    }

    @FunctionalInterface
    interface IDeserializer<SELF, DATA> {

        SELF deserialize(DATA data);
    }
}
