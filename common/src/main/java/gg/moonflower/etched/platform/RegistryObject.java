package gg.moonflower.etched.platform;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * A lazily available registered value.
 *
 * @param <T> The type of the registered value
 */
public interface RegistryObject<T> extends Supplier<T> {

    /**
     * @return The registry name of the value
     */
    ResourceLocation id();
}
