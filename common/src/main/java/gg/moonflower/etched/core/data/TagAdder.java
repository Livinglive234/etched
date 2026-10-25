package gg.moonflower.etched.core.data;

import net.minecraft.tags.TagKey;

import java.util.List;

/**
 * Adds entries to a tag. Implemented by the tag providers of each mod loader so the tags only have to be defined once.
 *
 * @param <T> The type of the tag entries
 */
@FunctionalInterface
public interface TagAdder<T> {

    void add(TagKey<T> tag, List<T> values);
}
