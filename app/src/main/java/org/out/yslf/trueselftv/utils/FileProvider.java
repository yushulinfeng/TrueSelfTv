package org.out.yslf.trueselftv.utils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Copy From android.support.v4.content.FileProvider.
 * Then we can without v4 lib.
 *
 * @author SunYuLin
 * @since 2019/2/15
 */

@SuppressWarnings("NullableProblems")
public class FileProvider extends ContentProvider {
    private static final String[] COLUMNS = new String[]{"_display_name", "_size"};
    private static final File DEVICE_ROOT = new File("/");
    private static final HashMap<String, FileProvider.PathStrategy> sCache = new HashMap<>();
    private FileProvider.PathStrategy mStrategy;

    public FileProvider() {
    }

    public boolean onCreate() {
        return true;
    }

    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);
        if (info.exported) {
            throw new SecurityException("Provider must not be exported");
        } else if (!info.grantUriPermissions) {
            throw new SecurityException("Provider must grant uri permissions");
        } else {
            this.mStrategy = getPathStrategy(context, info.authority);
        }
    }

    public static Uri getUriForFile(Context context, String authority, File file) {
        FileProvider.PathStrategy strategy = getPathStrategy(context, authority);
        return strategy.getUriForFile(file);
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        File file = this.mStrategy.getFileForUri(uri);
        if (projection == null) {
            projection = COLUMNS;
        }

        String[] cols = new String[projection.length];
        Object[] values = new Object[projection.length];
        int i = 0;
        String[] var10 = projection;
        int var11 = projection.length;

        for (int var12 = 0; var12 < var11; ++var12) {
            String col = var10[var12];
            if ("_display_name".equals(col)) {
                cols[i] = "_display_name";
                values[i++] = file.getName();
            } else if ("_size".equals(col)) {
                cols[i] = "_size";
                values[i++] = file.length();
            }
        }

        cols = copyOf(cols, i);
        values = copyOf(values, i);
        MatrixCursor cursor = new MatrixCursor(cols, 1);
        cursor.addRow(values);
        return cursor;
    }

    public String getType(Uri uri) {
        File file = this.mStrategy.getFileForUri(uri);
        int lastDot = file.getName().lastIndexOf(46);
        if (lastDot >= 0) {
            String extension = file.getName().substring(lastDot + 1);
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            if (mime != null) {
                return mime;
            }
        }

        return "application/octet-stream";
    }

    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("No external inserts");
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("No external updates");
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        File file = this.mStrategy.getFileForUri(uri);
        return file.delete() ? 1 : 0;
    }

    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        File file = this.mStrategy.getFileForUri(uri);
        int fileMode = modeToMode(mode);
        return ParcelFileDescriptor.open(file, fileMode);
    }

    private static FileProvider.PathStrategy getPathStrategy(Context context, String authority) {
        synchronized (sCache) {
            FileProvider.PathStrategy strat = sCache.get(authority);
            if (strat == null) {
                try {
                    strat = parsePathStrategy(context, authority);
                } catch (IOException var6) {
                    throw new IllegalArgumentException("Failed to parse android.support.FILE_PROVIDER_PATHS meta-data", var6);
                } catch (XmlPullParserException var7) {
                    throw new IllegalArgumentException("Failed to parse android.support.FILE_PROVIDER_PATHS meta-data", var7);
                }

                sCache.put(authority, strat);
            }

            return strat;
        }
    }

    private static FileProvider.PathStrategy parsePathStrategy(Context context, String authority) throws IOException, XmlPullParserException {
        FileProvider.SimplePathStrategy strat = new FileProvider.SimplePathStrategy(authority);
        ProviderInfo info = context.getPackageManager().resolveContentProvider(authority, PackageManager.GET_META_DATA);
        XmlResourceParser in = info.loadXmlMetaData(context.getPackageManager(), "android.support.FILE_PROVIDER_PATHS");
        if (in == null) {
            throw new IllegalArgumentException("Missing android.support.FILE_PROVIDER_PATHS meta-data");
        } else {
            int type;
            while ((type = in.next()) != 1) {
                if (type == 2) {
                    String tag = in.getName();
                    String name = in.getAttributeValue(null, "name");
                    String path = in.getAttributeValue(null, "path");
                    File target = null;
                    if ("root-path".equals(tag)) {
                        target = DEVICE_ROOT;
                    } else if ("files-path".equals(tag)) {
                        target = context.getFilesDir();
                    } else if ("cache-path".equals(tag)) {
                        target = context.getCacheDir();
                    } else if ("external-path".equals(tag)) {
                        target = Environment.getExternalStorageDirectory();
                    } else {
                        File[] externalMediaDirs;
                        if ("external-files-path".equals(tag)) {
                            externalMediaDirs = getExternalFilesDirs(context, null);
                            if (externalMediaDirs.length > 0) {
                                target = externalMediaDirs[0];
                            }
                        } else if ("external-cache-path".equals(tag)) {
                            externalMediaDirs = getExternalCacheDirs(context);
                            if (externalMediaDirs.length > 0) {
                                target = externalMediaDirs[0];
                            }
                        } else if (Build.VERSION.SDK_INT >= 21 && "external-media-path".equals(tag)) {
                            externalMediaDirs = context.getExternalMediaDirs();
                            if (externalMediaDirs.length > 0) {
                                target = externalMediaDirs[0];
                            }
                        }
                    }

                    if (target != null) {
                        strat.addRoot(name, buildPath(target, path));
                    }
                }
            }

            return strat;
        }
    }

    public static File[] getExternalFilesDirs(Context context, String type) {
        return Build.VERSION.SDK_INT >= 19 ? context.getExternalFilesDirs(type) : new File[]{context.getExternalFilesDir(type)};
    }

    public static File[] getExternalCacheDirs(Context context) {
        return Build.VERSION.SDK_INT >= 19 ? context.getExternalCacheDirs() : new File[]{context.getExternalCacheDir()};
    }

    private static int modeToMode(String mode) {
        int modeBits;
        if ("r".equals(mode)) {
            modeBits = 268435456;
        } else if (!"w".equals(mode) && !"wt".equals(mode)) {
            if ("wa".equals(mode)) {
                modeBits = 704643072;
            } else if ("rw".equals(mode)) {
                modeBits = 939524096;
            } else {
                if (!"rwt".equals(mode)) {
                    throw new IllegalArgumentException("Invalid mode: " + mode);
                }

                modeBits = 1006632960;
            }
        } else {
            modeBits = 738197504;
        }

        return modeBits;
    }

    private static File buildPath(File base, String... segments) {
        File cur = base;
        for (String segment : segments) {
            if (segment != null) {
                cur = new File(cur, segment);
            }
        }
        return cur;
    }

    private static String[] copyOf(String[] original, int newLength) {
        String[] result = new String[newLength];
        System.arraycopy(original, 0, result, 0, newLength);
        return result;
    }

    private static Object[] copyOf(Object[] original, int newLength) {
        Object[] result = new Object[newLength];
        System.arraycopy(original, 0, result, 0, newLength);
        return result;
    }

    static class SimplePathStrategy implements FileProvider.PathStrategy {
        private final String mAuthority;
        private final HashMap<String, File> mRoots = new HashMap<>();

        SimplePathStrategy(String authority) {
            this.mAuthority = authority;
        }

        void addRoot(String name, File root) {
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Name must not be empty");
            } else {
                try {
                    root = root.getCanonicalFile();
                } catch (IOException var4) {
                    throw new IllegalArgumentException("Failed to resolve canonical path for " + root, var4);
                }

                this.mRoots.put(name, root);
            }
        }

        public Uri getUriForFile(File file) {
            String path;
            try {
                path = file.getCanonicalPath();
            } catch (IOException var7) {
                throw new IllegalArgumentException("Failed to resolve canonical path for " + file);
            }

            Map.Entry<String, File> mostSpecific = null;
            Iterator<Map.Entry<String, File>> var4 = this.mRoots.entrySet().iterator();

            while (true) {
                Map.Entry<String, File> root;
                String rootPath;
                do {
                    do {
                        if (!var4.hasNext()) {
                            if (mostSpecific == null) {
                                throw new IllegalArgumentException("Failed to find configured root that contains " + path);
                            }

                            rootPath = (mostSpecific.getValue()).getPath();
                            if (rootPath.endsWith("/")) {
                                path = path.substring(rootPath.length());
                            } else {
                                path = path.substring(rootPath.length() + 1);
                            }

                            path = Uri.encode(mostSpecific.getKey()) + '/' + Uri.encode(path, "/");
                            return (new Uri.Builder()).scheme("content").authority(this.mAuthority).encodedPath(path).build();
                        }

                        root = var4.next();
                        rootPath = (root.getValue()).getPath();
                    } while (!path.startsWith(rootPath));
                }
                while (mostSpecific != null && rootPath.length() <= (mostSpecific.getValue()).getPath().length());

                mostSpecific = root;
            }
        }

        public File getFileForUri(Uri uri) {
            String path = uri.getEncodedPath();
            if (path == null) {
                throw new IllegalArgumentException("Failed to encode path for " + uri);
            }
            int splitIndex = path.indexOf(47, 1);
            String tag = Uri.decode(path.substring(1, splitIndex));
            path = Uri.decode(path.substring(splitIndex + 1));
            File root = this.mRoots.get(tag);
            if (root == null) {
                throw new IllegalArgumentException("Unable to find configured root for " + uri);
            } else {
                File file = new File(root, path);

                try {
                    file = file.getCanonicalFile();
                } catch (IOException var8) {
                    throw new IllegalArgumentException("Failed to resolve canonical path for " + file);
                }

                if (!file.getPath().startsWith(root.getPath())) {
                    throw new SecurityException("Resolved path jumped beyond configured root");
                } else {
                    return file;
                }
            }
        }
    }

    interface PathStrategy {
        Uri getUriForFile(File var1);

        File getFileForUri(Uri var1);
    }
}
