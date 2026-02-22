package net.quepierts.endfieldpanorama.earlywindow.render.shader;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Shaders {

    @UtilityClass
    public static class Vertex {

        public static final String BLIT             = "blit_screen";
        public static final String CHARACTER        = "character";

        public static final String PANORAMA         = "panorama";
    }

    @UtilityClass
    public static class Fragment {

        public static final String CHARACTER        = "character";
        public static final String FANCY_BACKGROUND = "fancy_background";

        public static final String PANORAMA         = "panorama";

    }

}
