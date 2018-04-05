package me.carc.btown.common;

/**
 * Created by bamptonm on 20/03/2018.
 */

public class CacheTest {
    private static final CacheTest ourInstance = new CacheTest();

    public static CacheTest getInstance() {
        return ourInstance;
    }

    private CacheTest() {
    }
}
