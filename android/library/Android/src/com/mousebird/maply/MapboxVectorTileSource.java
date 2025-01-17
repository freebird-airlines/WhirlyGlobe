package com.mousebird.maply;

import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * The MapboxVectorTiles class is used to load Mapbox format vector tiles
 * on demand over a certain area.  You'll need to use this in combination with
 * a QuadPagingLayer.
 */
public class MapboxVectorTileSource implements QuadPagingLayer.PagingInterface
{
    MaplyBaseController controller;
    OkHttpClient client;
    MapboxTileSource mbTiles = null;
    RemoteTileInfo tileInfo = null;
    public boolean debugOutput = false;

    /**
     * If set, we'll explicity dispose of the the vector objects after we're done.
     * This cuts down on memory use.
     */
    public boolean disposeAfterRemoval = false;

    MapboxVectorTileParser tileParser = null;
    VectorStyleInterface vecStyleFactory = null;

    /**
     * Construct with a initialized MBTilesSource.  This version reads from a local database.
     */
    public MapboxVectorTileSource(MapboxTileSource dataSource,VectorStyleInterface inVecStyleFactor)
    {
        mbTiles = dataSource;
        coordSys = mbTiles.getCoordSystem();
        tileParser = new MapboxVectorTileParser();
        vecStyleFactory = inVecStyleFactor;
    }

    public MapboxVectorTileSource(MaplyBaseController baseController,RemoteTileInfo inTileInfo,VectorStyleInterface inVecStyleFactor)
    {
        controller = baseController;
        client = baseController.getHttpClient();
        tileInfo = inTileInfo;
        tileParser = new MapboxVectorTileParser();
        vecStyleFactory = inVecStyleFactor;
        coordSys = new SphericalMercatorCoordSystem();
    }

    public CoordSystem coordSys = null;

    /**
     * Minimum zoom level supported.
     */
    public int minZoom()
    {
        if (mbTiles != null)
            return mbTiles.getMinZoom();
        else
            return tileInfo.minZoom;
    }

    /**
     * Maximum zoom level supported.
     */
    public int maxZoom()
    {
        if (mbTiles != null)
            return mbTiles.getMaxZoom();
        else
            return tileInfo.maxZoom;
    }

    static double MAX_EXTENT = 20037508.342789244;

    // Convert to spherical mercator directly
    Point2d toMerc(Point2d pt)
    {
        Point2d newPt = new Point2d();
        newPt.setValue(Math.toDegrees(pt.getX()) * MAX_EXTENT / 180.0,
                3189068.5 * Math.log((1.0 + Math.sin(pt.getY())) / (1.0 - Math.sin(pt.getY()))));

        return newPt;
    }

    // Process data returned from an MBTiles file or network request
    boolean processData(final QuadPagingLayer layer,final MaplyTileID tileID,byte[] tileData)
    {
        ArrayList<ComponentObject> tileCompObjs = new ArrayList<ComponentObject>();

        if (tileData != null) {
            try {
                // Unzip if it's compressed
                ByteArrayInputStream bin = new ByteArrayInputStream(tileData);
                GZIPInputStream in = new GZIPInputStream(bin);
                ByteArrayOutputStream bout = new ByteArrayOutputStream(tileData.length * 2);

                ZipEntry ze;
                byte[] buffer = new byte[1024];
                int count;
                while ((count = in.read(buffer)) != -1)
                    bout.write(buffer, 0, count);

                tileData = bout.toByteArray();
            } catch (Exception ex) {
                // We'll try the raw data if we can't decompress it
            }

            // Parse the data
            // Note: Eventually short circuit parsing with layer test and UUID test
            Mbr mbr = layer.geoBoundsForTile(tileID);
            mbr.ll = toMerc(mbr.ll);
            mbr.ur = toMerc(mbr.ur);

            MapboxVectorTileParser ourTileParser = tileParser;
            if (ourTileParser == null)
                return false;

            MapboxVectorTileParser.DataReturn dataObjs = ourTileParser.parseData(tileData, mbr);

            if (dataObjs == null)
                return false;

            VectorStyleInterface ourVecStyleFactory = vecStyleFactory;

            // Work through the vector objects
            if (ourVecStyleFactory != null) {
                HashMap<String, ArrayList<VectorObject>> vecObjsPerStyle = new HashMap<String, ArrayList<VectorObject>>();

                // Sort the vector objects into bins based on their styles
                if (dataObjs != null && dataObjs.vectorObjects != null)
                    for (VectorObject vecObj : dataObjs.vectorObjects) {
                        AttrDictionary attrs = vecObj.getAttributes();
                        VectorStyle[] styles = ourVecStyleFactory.stylesForFeature(attrs, tileID, attrs.getString("layer_name"), layer.maplyControl);
                        for (VectorStyle style : styles) {
                            ArrayList<VectorObject> vecObjsForStyle = vecObjsPerStyle.get(style.getUuid());
                            if (vecObjsForStyle == null) {
                                vecObjsForStyle = new ArrayList<VectorObject>();
                                vecObjsPerStyle.put(style.getUuid(), vecObjsForStyle);
                            }
                            vecObjsForStyle.add(vecObj);
                        }
                    }

                // Work through the various styles
                for (String uuid : vecObjsPerStyle.keySet()) {
                    ArrayList<VectorObject> vecObjs = vecObjsPerStyle.get(uuid);
                    VectorStyle style = ourVecStyleFactory.styleForUUID(uuid, layer.maplyControl);

                    // This makes the objects
                    if (style != null) {
                        ComponentObject[] compObjs = style.buildObjects(vecObjs, tileID, layer.maplyControl);
                        if (compObjs != null)
                            for (int ii = 0; ii < compObjs.length; ii++)
                                tileCompObjs.add(compObjs[ii]);
                    }
                }
            }

            // Add all the component objects we created
            if (tileCompObjs.size() > 0)
                layer.addData(tileCompObjs, tileID);

            //                    Log.d("Maply","Loaded vector tile: " + tileID.toString());

            layer.tileDidLoad(tileID);

            // Explicitly dispose of vector objects for efficiency
            if (disposeAfterRemoval)
            {
                for (VectorObject vecObj : dataObjs.vectorObjects)
                    vecObj.dispose();
            }
        } else
            // This just means the tile was empty
            layer.tileDidLoad(tileID);

        return true;
    }

    /**
     * Used internally to start fetching data.
     * @param layer The quad paging layer asking you to start fetching.
     * @param tileID The tile to start fetching
     */
    public void startFetchForTile(final QuadPagingLayer layer,final MaplyTileID tileID)
    {
        // It's a local MBTiles file
        if (mbTiles != null) {
            LayerThread thread = layer.maplyControl.getWorkingThread();
            thread.addTask(new Runnable() {
                @Override
                public void run() {
                    // Load the data, if it's there
                    MapboxTileSource thisMbTiles = mbTiles;
                    if (thisMbTiles != null) {
                        byte[] tileData = thisMbTiles.getDataTile(tileID);

                        processData(layer, tileID, tileData);
                    }
                }
            });
        } else {
            if (debugOutput)
                Log.d("Maply","Starting fetch for tile " + tileID.level + ": (" + tileID.x + "," + tileID.y + ")");

            // Form the tile URL
            int maxY = 1<<tileID.level;
            int remoteY = maxY - tileID.y - 1;
            final URL tileURL = tileInfo.buildURL(tileID.x,remoteY,tileID.level);

            String cacheFile = null;
            if (cacheDir != null)
                cacheFile = cacheDir.getAbsolutePath() + tileInfo.buildCacheName(tileID.x, tileID.y, tileID.level);
            ConnectionTask task = new ConnectionTask(layer,this,tileID,tileURL,cacheFile);
            task.fetchTile();
        }
    }

    HashMap<MaplyTileID,ConnectionTask> tasks = new HashMap<MaplyTileID,ConnectionTask>();

    ConnectionTask getTask(MaplyTileID tileID)
    {
        synchronized (tasks)
        {
            return tasks.get(tileID);
        }
    }

    void addTask(MaplyTileID tileID,ConnectionTask task)
    {
        synchronized (tasks)
        {
            tasks.put(tileID,task);
        }
    }

    void removeTask(MaplyTileID tileID)
    {
        synchronized (tasks)
        {
            tasks.remove(tileID);
        }
    }

    /**
     * Notification that a tile unloaded.
     */
    public void tileDidUnload(MaplyTileID tileID)
    {
        ConnectionTask task = getTask(tileID);

        if (task != null) {
            task.cancel();
            task.clear();
        }
    }

    // Called when the layer shuts down
    public void clear()
    {
        synchronized (this)
        {
            if (client != null)
                cancel(client, NET_TAG);

            synchronized (tasks)
            {
                HashMap<MaplyTileID,ConnectionTask> theTasks = (HashMap<MaplyTileID,ConnectionTask>)tasks.clone();
                for (ConnectionTask task : theTasks.values())
                    task.clear();
            }

            controller = null;
            client = null;
            mbTiles = null;
            tileInfo = null;
            tileParser = null;
            vecStyleFactory = null;
        }
    }

    private void cancel(OkHttpClient client, Object net_tag) {
    }


    File cacheDir = null;
    Object NET_TAG = new Object();

    /**
     * Set the cache directory for fetched vector tiles.  We'll look there first.
     * There is no limiting or pruning going on, that directory will just get
     * bigger and bigger.
     * <p>
     * By default that directory is null.
     *
     * @param inCacheDir Cache directory for image tiles.
     */
    public void setCacheDir(File inCacheDir)
    {
        cacheDir = inCacheDir;
    }

    // Connection task fetches the image
    private class ConnectionTask implements Callback
    {
        MapboxVectorTileSource tileSource = null;
        QuadPagingLayer layer = null;
        MaplyTileID tileID = null;
        URL url = null;
        String locFile = null;
        public Call call;
        byte[] tileData = null;
        File cacheFile = null;
        boolean isCanceled = false;

        ConnectionTask(QuadPagingLayer inLayer,MapboxVectorTileSource inTileSource, MaplyTileID inTileID,URL inURL,String inFile)
        {
            layer = inLayer;
            tileSource = inTileSource;
            tileID = inTileID;
            locFile = inFile;
            url = inURL;
        }

        // Either fetch the tile from the local cache or fetch it remotely
        protected void fetchTile() {
            try {
                synchronized (this) {
                    // See if it's here locally
                    if (locFile != null) {
                        cacheFile = new File(locFile);
                        if (cacheFile.exists()) {
                            tileData = FileUtils.readFileToByteArray(cacheFile);
                            if (debugOutput) {
                                if (tileData != null)
                                    Log.d("Maply", "Read cached file for tile " + tileID.level + ": (" + tileID.x + "," + tileID.y + ")");
                                else
                                    Log.d("Maply", "Read cached file for tile " + tileID.level + ": (" + tileID.x + "," + tileID.y + ")");
                            }
                        }
                    }
                }

                if (tileData != null) {
                    reportTile();
                    return;
                }

                // Load the data from that URL
                Request request = new Request.Builder().url(url).tag(NET_TAG).build();

                synchronized (this) {
                    call = client.newCall(request);
                    addTask(tileID, this);
                    call.enqueue(this);
                }
            } catch (Exception e) {
                if (debugOutput)
                    Log.e("Maply","Exception while trying to fetch the tile: " + e.toString());
            }
        }

        // Callback from OK HTTP on tile loading failure
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            // Ignore cancels
            if (e.getLocalizedMessage().contains("Canceled"))
                return;

            if (!isCanceled)
                Log.e("Maply", "Failed to fetch remote tile " + tileID.level + ": (" + tileID.x + "," + tileID.y + ")");

            clear();
        }

        // Callback from OK HTTP on success
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            if (isCanceled)
                return;

            try {
                tileData = response.body().bytes();

                if (processData(layer,tileID,tileData)) {
                    // Save to cache
                    if (cacheFile != null && tileData != null) {
                        OutputStream fOut;
                        fOut = new FileOutputStream(cacheFile);
                        fOut.write(tileData);
                        fOut.close();
                    }
                }

                if (debugOutput) {
                    if (tileData != null)
                        Log.d("Maply", "Fetched remote file for tile " + tileID.level + ": (" + tileID.x + "," + tileID.y + ")");
                    else {
                        Log.d("Maply", "Fetched remote tile " + +tileID.level + ": (" + tileID.x + "," + tileID.y + ")" + " but did not decode.");
                        Log.e("Maply", "Response for failed image decode: " + response.toString());
                    }
                }
            }
            catch (Exception e)
            {
                Log.e("Maply", "Fetched remote file for tile " + tileID.level + ": (" + tileID.x + "," + tileID.y + ")" + " because: " + e.toString());
            }

            reportTile();
        }

        // Let the system know we've got a tile
        protected void reportTile() {
            synchronized (this) {
                if (layer != null) {
                    // Let the layer and delegate know what happened with it
                    if (tileData == null) {
                        layer.tileFailedToLoad(tileID);
                    } else {
                        layer.tileDidLoad(tileID);
                    }
                }
            }

            clear();
        }

        // Cancel an outstanding request
        protected void cancel() {
            synchronized (this) {
                isCanceled = true;
                if (call != null)
                    call.cancel();
            }

            clear();
        }

        void clear()
        {
            removeTask(tileID);

            synchronized (this)
            {
                tileSource = null;
                layer = null;
                url = null;
                locFile = null;
                call = null;
                cacheFile = null;
            }
        }
    }
}
