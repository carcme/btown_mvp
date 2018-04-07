package me.carc.btown.tours.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import me.carc.btown.common.C;

public class ImageSize {

    @SuppressFBWarnings("MS_MUTABLE_ARRAY")
    public static final int[] SCREEN  = new int[] {C.SCREEN_WIDTH, C.SCREEN_HEIGHT};
    public static final int[] IMAGE   = new int[] {C.IMAGE_WIDTH, C.IMAGE_HEIGHT};
    public static final int[] THUMB   = new int[] {100, 100};
    public static final int[] HEADER  = new int[] {650, 400};
}
