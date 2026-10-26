package gg.moonflower.etched.core.compat.vs;

import gg.moonflower.etched.api.sound.SoundPositions;
import gg.moonflower.etched.core.Etched;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4dc;
import org.joml.Vector3d;

import java.lang.reflect.Method;

/**
 * Makes radios and jukeboxes on Valkyrien Skies ships, such as Eureka ships, heard from where the ship is.
 * <p>The blocks of a ship are stored far away from where the ship is drawn, and Valkyrien Skies moves the sounds of the game back to
 * the ship itself. The sounds of this mod are not the kind it moves, so this does it for them. Valkyrien Skies is reached by name,
 * since its classes are not needed to build this mod.
 */
public final class ValkyrienSkiesCompat {

    private ValkyrienSkiesCompat() {
    }

    public static void load() {
        try {
            Class<?> utils = Class.forName("org.valkyrienskies.mod.common.VSGameUtilsKt");
            Method manager = null;
            for (Method method : utils.getMethods()) {
                if (method.getName().equals("getLoadedShipManagingPos") && method.getParameterCount() == 4 && method.getParameterTypes()[1] == double.class) {
                    manager = method;
                    break;
                }
            }
            if (manager == null) {
                throw new NoSuchMethodException("getLoadedShipManagingPos");
            }

            Class<?> clientShip = Class.forName("org.valkyrienskies.core.api.ships.ClientShip");
            Method renderTransform = clientShip.getMethod("getRenderTransform");
            Method shipToWorld = Class.forName("org.valkyrienskies.core.api.ships.properties.ShipTransform").getMethod("getShipToWorld");
            Method managerMethod = manager;

            SoundPositions.setTransformer((x, y, z) -> {
                Level level = Minecraft.getInstance().level;
                if (level == null) {
                    return null;
                }
                try {
                    Object ship = managerMethod.invoke(null, level, x, y, z);
                    if (ship == null) {
                        return null;
                    }
                    Matrix4dc matrix = (Matrix4dc) shipToWorld.invoke(renderTransform.invoke(ship));
                    Vector3d world = matrix.transformPosition(new Vector3d(x, y, z));
                    return new Vec3(world.x, world.y, world.z);
                } catch (Exception e) {
                    return null;
                }
            });
            Etched.LOGGER.info("Radios and jukeboxes on Valkyrien Skies ships will be heard from the ship");
        } catch (Throwable t) {
            Etched.LOGGER.warn("Could not connect to Valkyrien Skies, radios and jukeboxes on ships may be heard from the wrong place", t);
        }
    }
}
