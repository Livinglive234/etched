package gg.moonflower.etched.core.registry;

import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.platform.EtchedPlatform;
import gg.moonflower.etched.platform.Registrar;
import gg.moonflower.etched.platform.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class EtchedSounds {

    public static final Registrar<SoundEvent> REGISTRY = EtchedPlatform.get().registrar(BuiltInRegistries.SOUND_EVENT);

    public static final RegistryObject<SoundEvent> UI_ETCHER_TAKE_RESULT = registerSound("ui.etching_table.take_result");

    private static RegistryObject<SoundEvent> registerSound(String id) {
        return REGISTRY.register(id, () -> SoundEvent.createVariableRangeEvent(Etched.etchedPath(id)));
    }
}
