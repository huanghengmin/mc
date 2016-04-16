package com.hzih.mc.utils;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.jar.Manifest;

/**
 * Created by IntelliJ IDEA.
 * User: Ç®ÏþÅÎ
 * Date: 12-6-13
 * Time: ÏÂÎç3:57
 * To change this template use File | Settings | File Templates.
 */
public class ManifestUtil {
    /**
     *
     * @param servletContextClass    ServletContext.class
     * @return
     * @throws IOException
     */
    public static Manifest getManifest(Class<ServletContext> servletContextClass) throws IOException {
        InputStream inputStream = servletContextClass.getResourceAsStream("/META-INF/MANIFEST.MF");
        Manifest manifest = new Manifest(inputStream);
        return manifest;
    }

    public static String getValue(Manifest manifest, String key) {
        Map map = manifest.getEntries();
        return manifest.getMainAttributes().getValue(key);
    }

}
