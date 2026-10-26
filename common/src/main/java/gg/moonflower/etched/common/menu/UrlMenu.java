package gg.moonflower.etched.common.menu;

public interface UrlMenu {

    /**
     * Sets the URL for the resulting stack to the specified value.
     *
     * @param url The new URL
     */
    void setUrl(String url);

    /**
     * Gives the name of the station that was just picked, such as from a search. Screens that have no use for it ignore it.
     *
     * @param name The name of the station
     */
    default void setStationName(String name) {
    }
}
