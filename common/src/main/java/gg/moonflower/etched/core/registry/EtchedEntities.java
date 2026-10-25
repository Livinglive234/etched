package gg.moonflower.etched.core.registry;

import gg.moonflower.etched.common.entity.MinecartJukebox;
import gg.moonflower.etched.platform.EtchedPlatform;
import gg.moonflower.etched.platform.Registrar;
import gg.moonflower.etched.platform.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EtchedEntities {

    public static final Registrar<EntityType<?>> REGISTRY = EtchedPlatform.get().registrar(BuiltInRegistries.ENTITY_TYPE);

    public static final RegistryObject<EntityType<MinecartJukebox>> JUKEBOX_MINECART = REGISTRY.register("jukebox_minecart", () -> EntityType.Builder.<MinecartJukebox>of(MinecartJukebox::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8).build("minecart_jukebox"));
}
