package gg.moonflower.etched.platform;

import java.util.function.Supplier;

/**
 * Registers values to a single registry for the current mod loader.
 *
 * @param <T> The base type of the registry
 */
public interface Registrar<T> {

    /**
     * Registers a new value.
     *
     * @param name    The path of the value in the mod namespace
     * @param factory The factory to create the value when registering
     * @param <I>     The type of value being registered
     * @return A reference to the registered value
     */
    <I extends T> RegistryObject<I> register(String name, Supplier<I> factory);
}
