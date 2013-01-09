package org.jboss.android.assetcache;

import android.content.res.AssetManager;

import java.io.*;

/**
 * Created By: Gytis Trikleris (gytis@redhat.com)
 * Date: 29/12/12
 * Time: 18:47
 */
public class AssetCache {

    private static final String TAG = "AssetCache";

    private File cacheDir;

    private AssetManager assetManager;

    public AssetCache(File cacheDir, AssetManager assetManager) {
        this.cacheDir = cacheDir;
        this.assetManager = assetManager;
    }

    /**
     * Extracts asset and creates its copy in application cache.
     * Returns absolute path to the cached asset.
     *
     * @param assetName
     * @return
     */
    public String cacheAsset(String assetName) {
        String path = getPathInCache(assetName);

        if (path == null) {
            moveAssetToCache(assetName);
            path = getPathInCache(assetName);
        }

        return path;
    }

    /**
     * Removes asset from cache.
     *
     * @param assetName
     */
    public void removeAssetFromCache(String assetName) {
        String path = getPathInCache(assetName);

        if (path != null) {
            File f = new File(path);
            f.delete();
        }
    }

    /**
     * Returns absolute path of cached asset. Asset has to be cached before.
     * Returns null if asset does not exist in cache.
     *
     * @param assetName
     * @return
     */
    public String getPathInCache(String assetName) {
        File f = new File(cacheDir + "/" + assetName);
        String path = null;

        if (f.exists()) {
            path = f.getAbsolutePath();
        }

        return path;
    }

    private void moveAssetToCache(String assetName) {
        InputStream is = getAssetInputStream(assetName);
        byte[] buffer = getBufferFromInputStream(is);
        OutputStream os = getAssetOutputStream(assetName);
        writeBufferToOutputStream(os, buffer);
    }

    private InputStream getAssetInputStream(String assetName) {
        InputStream is;

        try {
            is = assetManager.open(assetName);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return is;
    }

    private OutputStream getAssetOutputStream(String assetName) {
        OutputStream os;

        try {
            File f = new File(cacheDir + "/" + assetName);
            os = new FileOutputStream(f);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return os;
    }

    private byte[] getBufferFromInputStream(InputStream is) {
        byte[] buffer;

        try {
            int size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return buffer;
    }

    private void writeBufferToOutputStream(OutputStream os, byte[] buffer) {
        try {
            os.write(buffer);
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
