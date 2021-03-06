/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.sdklib;

import com.android.prefs.AndroidLocation;
import com.android.prefs.AndroidLocation.AndroidLocationException;
import com.android.sdklib.AndroidVersion.AndroidVersionException;
import com.android.sdklib.internal.project.ProjectProperties;
import com.android.sdklib.io.FileWrapper;
import com.android.sdklib.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The SDK manager parses the SDK folder and gives access to the content.
 * @see PlatformTarget
 * @see AddOnTarget
 */
public final class SdkManager {

    public final static String PROP_VERSION_SDK = "ro.build.version.sdk";              //$NON-NLS-1$
    public final static String PROP_VERSION_CODENAME = "ro.build.version.codename";    //$NON-NLS-1$
    public final static String PROP_VERSION_RELEASE = "ro.build.version.release";      //$NON-NLS-1$

    public final static String ADDON_NAME = "name";                                    //$NON-NLS-1$
    public final static String ADDON_VENDOR = "vendor";                                //$NON-NLS-1$
    public final static String ADDON_API = "api";                                      //$NON-NLS-1$
    public final static String ADDON_DESCRIPTION = "description";                      //$NON-NLS-1$
    public final static String ADDON_LIBRARIES = "libraries";                          //$NON-NLS-1$
    public final static String ADDON_DEFAULT_SKIN = "skin";                            //$NON-NLS-1$
    public final static String ADDON_USB_VENDOR = "usb-vendor";                        //$NON-NLS-1$
    public final static String ADDON_REVISION = "revision";                            //$NON-NLS-1$
    public final static String ADDON_REVISION_OLD = "version";                         //$NON-NLS-1$


    private final static Pattern PATTERN_LIB_DATA = Pattern.compile(
            "^([a-zA-Z0-9._-]+\\.jar);(.*)$", Pattern.CASE_INSENSITIVE);               //$NON-NLS-1$

     // usb ids are 16-bit hexadecimal values.
    private final static Pattern PATTERN_USB_IDS = Pattern.compile(
            "^0x[a-f0-9]{4}$", Pattern.CASE_INSENSITIVE);                              //$NON-NLS-1$

    /** List of items in the platform to check when parsing it. These paths are relative to the
     * platform root folder. */
    private final static String[] sPlatformContentList = new String[] {
        SdkConstants.FN_FRAMEWORK_LIBRARY,
        SdkConstants.FN_FRAMEWORK_AIDL,
    };

    /** Preference file containing the usb ids for adb */
    private final static String ADB_INI_FILE = "adb_usb.ini";                          //$NON-NLS-1$
       //0--------90--------90--------90--------90--------90--------90--------90--------9
    private final static String ADB_INI_HEADER =
        "# ANDROID 3RD PARTY USB VENDOR ID LIST -- DO NOT EDIT.\n" +                   //$NON-NLS-1$
        "# USE 'android update adb' TO GENERATE.\n" +                                  //$NON-NLS-1$
        "# 1 USB VENDOR ID PER LINE.\n";                                               //$NON-NLS-1$

    /** The location of the SDK as an OS path */
    private final String mOsSdkPath;
    /** Valid targets that have been loaded. */
    private IAndroidTarget[] mTargets;

    /**
     * Create a new {@link SdkManager} instance.
     * External users should use {@link #createManager(String, ISdkLog)}.
     *
     * @param osSdkPath the location of the SDK.
     */
    private SdkManager(String osSdkPath) {
        mOsSdkPath = osSdkPath;
    }

    /**
     * Creates an {@link SdkManager} for a given sdk location.
     * @param osSdkPath the location of the SDK.
     * @param log the ISdkLog object receiving warning/error from the parsing. Cannot be null.
     * @return the created {@link SdkManager} or null if the location is not valid.
     */
    public static SdkManager createManager(String osSdkPath, ISdkLog log) {
        try {
            SdkManager manager = new SdkManager(osSdkPath);
            ArrayList<IAndroidTarget> list = new ArrayList<IAndroidTarget>();
            loadPlatforms(osSdkPath, list, log);
            loadAddOns(osSdkPath, list, log);

            // sort the targets/add-ons
            Collections.sort(list);

            manager.setTargets(list.toArray(new IAndroidTarget[list.size()]));

            // load the samples, after the targets have been set.
            manager.loadSamples(log);

            return manager;
        } catch (IllegalArgumentException e) {
            log.error(e, "Error parsing the sdk.");
        }

        return null;
    }

    /**
     * Returns the location of the SDK.
     */
    public String getLocation() {
        return mOsSdkPath;
    }

    /**
     * Returns the targets that are available in the SDK.
     * <p/>
     * The array can be empty but not null.
     */
    public IAndroidTarget[] getTargets() {
        return mTargets;
    }

    /**
     * Sets the targets that are available in the SDK.
     * <p/>
     * The array can be empty but not null.
     */
    private void setTargets(IAndroidTarget[] targets) {
        assert targets != null;
        mTargets = targets;
    }

    /**
     * Returns a target from a hash that was generated by {@link IAndroidTarget#hashString()}.
     *
     * @param hash the {@link IAndroidTarget} hash string.
     * @return The matching {@link IAndroidTarget} or null.
     */
    public IAndroidTarget getTargetFromHashString(String hash) {
        if (hash != null) {
            for (IAndroidTarget target : mTargets) {
                if (hash.equals(target.hashString())) {
                    return target;
                }
            }
        }

        return null;
    }

    /**
     * Updates adb with the USB devices declared in the SDK add-ons.
     * @throws AndroidLocationException
     * @throws IOException
     */
    public void updateAdb() throws AndroidLocationException, IOException {
        FileWriter writer = null;
        try {
            // get the android prefs location to know where to write the file.
            File adbIni = new File(AndroidLocation.getFolder(), ADB_INI_FILE);
            writer = new FileWriter(adbIni);

            // first, put all the vendor id in an HashSet to remove duplicate.
            HashSet<Integer> set = new HashSet<Integer>();
            IAndroidTarget[] targets = getTargets();
            for (IAndroidTarget target : targets) {
                if (target.getUsbVendorId() != IAndroidTarget.NO_USB_ID) {
                    set.add(target.getUsbVendorId());
                }
            }

            // write file header.
            writer.write(ADB_INI_HEADER);

            // now write the Id in a text file, one per line.
            for (Integer i : set) {
                writer.write(String.format("0x%04x\n", i));                            //$NON-NLS-1$
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * Reloads the content of the SDK.
     *
     * @param log the ISdkLog object receiving warning/error from the parsing. Cannot be null.
     */
    public void reloadSdk(ISdkLog log) {
        // get the current target list.
        ArrayList<IAndroidTarget> list = new ArrayList<IAndroidTarget>();
        loadPlatforms(mOsSdkPath, list, log);
        loadAddOns(mOsSdkPath, list, log);

        // For now replace the old list with the new one.
        // In the future we may want to keep the current objects, so that ADT doesn't have to deal
        // with new IAndroidTarget objects when a target didn't actually change.

        // sort the targets/add-ons
        Collections.sort(list);
        setTargets(list.toArray(new IAndroidTarget[list.size()]));

        // load the samples, after the targets have been set.
        loadSamples(log);
    }

    /**
     * Loads the Platforms from the SDK.
     * @param sdkOsPath Location of the SDK
     * @param list the list to fill with the platforms.
     * @param log the ISdkLog object receiving warning/error from the parsing. Cannot be null.
     */
    private static void loadPlatforms(String sdkOsPath, ArrayList<IAndroidTarget> list,
            ISdkLog log) {
        File platformFolder = new File(sdkOsPath, SdkConstants.FD_PLATFORMS);
        if (platformFolder.isDirectory()) {
            File[] platforms  = platformFolder.listFiles();

            for (File platform : platforms) {
                if (platform.isDirectory()) {
                    PlatformTarget target = loadPlatform(sdkOsPath, platform, log);
                    if (target != null) {
                        list.add(target);
                    }
                } else {
                    log.warning("Ignoring platform '%1$s', not a folder.", platform.getName());
                }
            }

            return;
        }

        String message = null;
        if (platformFolder.exists() == false) {
            message = "%s is missing.";
        } else {
            message = "%s is not a folder.";
        }

        throw new IllegalArgumentException(String.format(message,
                platformFolder.getAbsolutePath()));
    }

    /**
     * Loads a specific Platform at a given location.
     * @param sdkOsPath Location of the SDK
     * @param platformFolder the root folder of the platform.
     * @param log the ISdkLog object receiving warning/error from the parsing. Cannot be null.
     */
    private static PlatformTarget loadPlatform(String sdkOsPath, File platformFolder,
            ISdkLog log) {
        FileWrapper buildProp = new FileWrapper(platformFolder, SdkConstants.FN_BUILD_PROP);

        if (buildProp.isFile()) {
            Map<String, String> map = ProjectProperties.parsePropertyFile(buildProp, log);

            if (map != null) {
                // look for some specific values in the map.

                // version string
                String apiName = map.get(PROP_VERSION_RELEASE);
                if (apiName == null) {
                    log.warning(
                            "Ignoring platform '%1$s': %2$s is missing from '%3$s'",
                            platformFolder.getName(), PROP_VERSION_RELEASE,
                            SdkConstants.FN_BUILD_PROP);
                    return null;
                }

                // api level
                int apiNumber;
                String stringValue = map.get(PROP_VERSION_SDK);
                if (stringValue == null) {
                    log.warning(
                            "Ignoring platform '%1$s': %2$s is missing from '%3$s'",
                            platformFolder.getName(), PROP_VERSION_SDK,
                            SdkConstants.FN_BUILD_PROP);
                    return null;
                } else {
                    try {
                         apiNumber = Integer.parseInt(stringValue);
                    } catch (NumberFormatException e) {
                        // looks like apiNumber does not parse to a number.
                        // Ignore this platform.
                        log.warning(
                                "Ignoring platform '%1$s': %2$s is not a valid number in %3$s.",
                                platformFolder.getName(), PROP_VERSION_SDK,
                                SdkConstants.FN_BUILD_PROP);
                        return null;
                    }
                }

                // codename (optional)
                String apiCodename = map.get(PROP_VERSION_CODENAME);
                if (apiCodename != null && apiCodename.equals("REL")) {
                    apiCodename = null; // REL means it's a release version and therefore the
                                        // codename is irrelevant at this point.
                }

                // platform rev number
                int revision = 1;
                FileWrapper sourcePropFile = new FileWrapper(platformFolder,
                        SdkConstants.FN_SOURCE_PROP);
                Map<String, String> sourceProp = ProjectProperties.parsePropertyFile(
                        sourcePropFile, log);
                if (sourceProp != null) {
                    try {
                        revision = Integer.parseInt(sourceProp.get("Pkg.Revision"));   //$NON-NLS-1$
                    } catch (NumberFormatException e) {
                        // do nothing, we'll keep the default value of 1.
                    }
                    map.putAll(sourceProp);
                }

                // Ant properties
                FileWrapper sdkPropFile = new FileWrapper(platformFolder, SdkConstants.FN_SDK_PROP);
                Map<String, String> antProp = null;
                if (sdkPropFile.isFile()) { // obsolete platforms don't have this.
                    antProp = ProjectProperties.parsePropertyFile(sdkPropFile, log);
                }

                if (antProp != null) {
                    map.putAll(antProp);
                }

                // api number and name look valid, perform a few more checks
                if (checkPlatformContent(platformFolder, log) == false) {
                    return null;
                }

                // create the target.
                PlatformTarget target = new PlatformTarget(
                        sdkOsPath,
                        platformFolder.getAbsolutePath(),
                        map,
                        apiNumber,
                        apiCodename,
                        apiName,
                        revision);

                // need to parse the skins.
                String[] skins = parseSkinFolder(target.getPath(IAndroidTarget.SKINS));
                target.setSkins(skins);

                return target;
            }
        } else {
            log.warning("Ignoring platform '%1$s': %2$s is missing.",   //$NON-NLS-1$
                    platformFolder.getName(),
                    SdkConstants.FN_BUILD_PROP);
        }

        return null;
    }


    /**
     * Loads the Add-on from the SDK.
     * @param osSdkPath Location of the SDK
     * @param list the list to fill with the add-ons.
     * @param log the ISdkLog object receiving warning/error from the parsing. Cannot be null.
     */
    private static void loadAddOns(String osSdkPath, ArrayList<IAndroidTarget> list, ISdkLog log) {
        File addonFolder = new File(osSdkPath, SdkConstants.FD_ADDONS);
        if (addonFolder.isDirectory()) {
            File[] addons  = addonFolder.listFiles();

            IAndroidTarget[] targetList = list.toArray(new IAndroidTarget[list.size()]);

            for (File addon : addons) {
                // Add-ons have to be folders. Ignore files and no need to warn about them.
                if (addon.isDirectory()) {
                    AddOnTarget target = loadAddon(addon, targetList, log);
                    if (target != null) {
                        list.add(target);
                    }
                }
            }

            return;
        }

        String message = null;
        if (addonFolder.exists() == false) {
            message = "%s is missing.";
        } else {
            message = "%s is not a folder.";
        }

        throw new IllegalArgumentException(String.format(message,
                addonFolder.getAbsolutePath()));
    }

    /**
     * Loads a specific Add-on at a given location.
     * @param addonDir the location of the add-on directory.
     * @param targetList The list of Android target that were already loaded from the SDK.
     * @param log the ISdkLog object receiving warning/error from the parsing. Cannot be null.
     */
    private static AddOnTarget loadAddon(File addonDir,
            IAndroidTarget[] targetList,
            ISdkLog log) {

        // Parse the addon properties to ensure we can load it.
        Pair<Map<String, String>, String> infos = parseAddonProperties(addonDir, targetList, log);

        Map<String, String> propertyMap = infos.getFirst();
        String error = infos.getSecond();

        if (error != null) {
            log.warning("Ignoring add-on '%1$s': %2$s", addonDir.getName(), error);
            return null;
        }

        // Since error==null we're not supposed to encounter any issues loading this add-on.
        try {
            assert propertyMap != null;

            String api = propertyMap.get(ADDON_API);
            String name = propertyMap.get(ADDON_NAME);
            String vendor = propertyMap.get(ADDON_VENDOR);

            assert api != null;
            assert name != null;
            assert vendor != null;

            PlatformTarget baseTarget = null;

            // Look for a platform that has a matching api level or codename.
            for (IAndroidTarget target : targetList) {
                if (target.isPlatform() && target.getVersion().equals(api)) {
                    baseTarget = (PlatformTarget)target;
                    break;
                }
            }

            assert baseTarget != null;

            // get the optional description
            String description = propertyMap.get(ADDON_DESCRIPTION);

            // get the add-on revision
            int revisionValue = 1;
            String revision = propertyMap.get(ADDON_REVISION);
            if (revision == null) {
                revision = propertyMap.get(ADDON_REVISION_OLD);
            }
            if (revision != null) {
                revisionValue = Integer.parseInt(revision);
            }

            // get the optional libraries
            String librariesValue = propertyMap.get(ADDON_LIBRARIES);
            Map<String, String[]> libMap = null;

            if (librariesValue != null) {
                librariesValue = librariesValue.trim();
                if (librariesValue.length() > 0) {
                    // split in the string into the libraries name
                    String[] libraries = librariesValue.split(";");                    //$NON-NLS-1$
                    if (libraries.length > 0) {
                        libMap = new HashMap<String, String[]>();
                        for (String libName : libraries) {
                            libName = libName.trim();

                            // get the library data from the properties
                            String libData = propertyMap.get(libName);

                            if (libData != null) {
                                // split the jar file from the description
                                Matcher m = PATTERN_LIB_DATA.matcher(libData);
                                if (m.matches()) {
                                    libMap.put(libName, new String[] {
                                            m.group(1), m.group(2) });
                                } else {
                                    log.warning(
                                            "Ignoring library '%1$s', property value has wrong format\n\t%2$s",
                                            libName, libData);
                                }
                            } else {
                                log.warning(
                                        "Ignoring library '%1$s', missing property value",
                                        libName, libData);
                            }
                        }
                    }
                }
            }

            AddOnTarget target = new AddOnTarget(addonDir.getAbsolutePath(), name, vendor,
                    revisionValue, description, libMap, baseTarget);

            // need to parse the skins.
            String[] skins = parseSkinFolder(target.getPath(IAndroidTarget.SKINS));

            // get the default skin, or take it from the base platform if needed.
            String defaultSkin = propertyMap.get(ADDON_DEFAULT_SKIN);
            if (defaultSkin == null) {
                if (skins.length == 1) {
                    defaultSkin = skins[0];
                } else {
                    defaultSkin = baseTarget.getDefaultSkin();
                }
            }

            // get the USB ID (if available)
            int usbVendorId = convertId(propertyMap.get(ADDON_USB_VENDOR));
            if (usbVendorId != IAndroidTarget.NO_USB_ID) {
                target.setUsbVendorId(usbVendorId);
            }

            target.setSkins(skins, defaultSkin);

            return target;
        }
        catch (Exception e) {
            log.warning("Ignoring add-on '%1$s': error %2$s.",
                    addonDir.getName(), e.toString());
        }

        return null;
    }

    /**
     * Parses the add-on properties and decodes any error that occurs when loading an addon.
     *
     * @param addonDir the location of the addon directory.
     * @param targetList The list of Android target that were already loaded from the SDK.
     * @param log the ISdkLog object receiving warning/error from the parsing. Cannot be null.
     * @return A pair with the property map and an error string. Both can be null but not at the
     *  same time. If a non-null error is present then the property map must be ignored. The error
     *  should be translatable as it might show up in the SdkManager UI.
     */
    public static Pair<Map<String, String>, String> parseAddonProperties(
            File addonDir,
            IAndroidTarget[] targetList,
            ISdkLog log) {
        Map<String, String> propertyMap = null;
        String error = null;

        FileWrapper addOnManifest = new FileWrapper(addonDir, SdkConstants.FN_MANIFEST_INI);

        do {
            if (!addOnManifest.isFile()) {
                error = String.format("File not found: %1$s", SdkConstants.FN_MANIFEST_INI);
                break;
            }

            propertyMap = ProjectProperties.parsePropertyFile(addOnManifest, log);
            if (propertyMap == null) {
                error = String.format("Failed to parse properties from %1$s",
                        SdkConstants.FN_MANIFEST_INI);
                break;
            }

            // look for some specific values in the map.
            // we require name, vendor, and api
            String name = propertyMap.get(ADDON_NAME);
            if (name == null) {
                error = addonManifestWarning(ADDON_NAME);
                break;
            }

            String vendor = propertyMap.get(ADDON_VENDOR);
            if (vendor == null) {
                error = addonManifestWarning(ADDON_VENDOR);
                break;
            }

            String api = propertyMap.get(ADDON_API);
            PlatformTarget baseTarget = null;
            if (api == null) {
                error = addonManifestWarning(ADDON_API);
                break;
            }

            // Look for a platform that has a matching api level or codename.
            for (IAndroidTarget target : targetList) {
                if (target.isPlatform() && target.getVersion().equals(api)) {
                    baseTarget = (PlatformTarget)target;
                    break;
                }
            }

            if (baseTarget == null) {
                error = String.format("Unable to find base platform with API level '%1$s'", api);
                break;
            }

            // get the add-on revision
            String revision = propertyMap.get(ADDON_REVISION);
            if (revision == null) {
                revision = propertyMap.get(ADDON_REVISION_OLD);
            }
            if (revision != null) {
                try {
                    Integer.parseInt(revision);
                } catch (NumberFormatException e) {
                    // looks like revision does not parse to a number.
                    error = String.format("%1$s is not a valid number in %2$s.",
                                ADDON_REVISION, SdkConstants.FN_BUILD_PROP);
                    break;
                }
            }

        } while(false);

        return Pair.of(propertyMap, error);
    }

    /**
     * Converts a string representation of an hexadecimal ID into an int.
     * @param value the string to convert.
     * @return the int value, or {@link IAndroidTarget#NO_USB_ID} if the convertion failed.
     */
    private static int convertId(String value) {
        if (value != null && value.length() > 0) {
            if (PATTERN_USB_IDS.matcher(value).matches()) {
                String v = value.substring(2);
                try {
                    return Integer.parseInt(v, 16);
                } catch (NumberFormatException e) {
                    // this shouldn't happen since we check the pattern above, but this is safer.
                    // the method will return 0 below.
                }
            }
        }

        return IAndroidTarget.NO_USB_ID;
    }

    /**
     * Prepares a warning about the addon being ignored due to a missing manifest value.
     * This string will show up in the SdkManager UI.
     *
     * @param valueName The missing manifest value, for display.
     */
    private static String addonManifestWarning(String valueName) {
        return String.format("'%1$s' is missing from %2$s.",
                valueName, SdkConstants.FN_MANIFEST_INI);
    }

    /**
     * Checks the given platform has all the required files, and returns true if they are all
     * present.
     * <p/>This checks the presence of the following files: android.jar, framework.aidl, aapt(.exe),
     * aidl(.exe), dx(.bat), and dx.jar
     *
     * @param platform The folder containing the platform.
     * @param log Logger. Cannot be null.
     */
    private static boolean checkPlatformContent(File platform, ISdkLog log) {
        for (String relativePath : sPlatformContentList) {
            File f = new File(platform, relativePath);
            if (!f.exists()) {
                log.warning(
                        "Ignoring platform '%1$s': %2$s is missing.",                  //$NON-NLS-1$
                        platform.getName(), relativePath);
                return false;
            }
        }
        return true;
    }



    /**
     * Parses the skin folder and builds the skin list.
     * @param osPath The path of the skin root folder.
     */
    private static String[] parseSkinFolder(String osPath) {
        File skinRootFolder = new File(osPath);

        if (skinRootFolder.isDirectory()) {
            ArrayList<String> skinList = new ArrayList<String>();

            File[] files = skinRootFolder.listFiles();

            for (File skinFolder : files) {
                if (skinFolder.isDirectory()) {
                    // check for layout file
                    File layout = new File(skinFolder, SdkConstants.FN_SKIN_LAYOUT);

                    if (layout.isFile()) {
                        // for now we don't parse the content of the layout and
                        // simply add the directory to the list.
                        skinList.add(skinFolder.getName());
                    }
                }
            }

            return skinList.toArray(new String[skinList.size()]);
        }

        return new String[0];
    }

    /**
     * Loads all samples from the {@link SdkConstants#FD_SAMPLES} directory.
     *
     * @param log Logger. Cannot be null.
     */
    private void loadSamples(ISdkLog log) {
        File sampleFolder = new File(mOsSdkPath, SdkConstants.FD_SAMPLES);
        if (sampleFolder.isDirectory()) {
            File[] platforms  = sampleFolder.listFiles();

            for (File platform : platforms) {
                if (platform.isDirectory()) {
                    // load the source.properties file and get an AndroidVersion object from it.
                    AndroidVersion version = getSamplesVersion(platform, log);

                    if (version != null) {
                        // locate the platform matching this version
                        for (IAndroidTarget target : mTargets) {
                            if (target.isPlatform() && target.getVersion().equals(version)) {
                                ((PlatformTarget)target).setSamplesPath(platform.getAbsolutePath());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the {@link AndroidVersion} of the sample in the given folder.
     *
     * @param folder The sample's folder.
     * @param log Logger for errors. Cannot be null.
     * @return An {@link AndroidVersion} or null on error.
     */
    private AndroidVersion getSamplesVersion(File folder, ISdkLog log) {
        File sourceProp = new File(folder, SdkConstants.FN_SOURCE_PROP);
        try {
            Properties p = new Properties();
            p.load(new FileInputStream(sourceProp));

            return new AndroidVersion(p);
        } catch (FileNotFoundException e) {
            log.warning("Ignoring sample '%1$s': does not contain %2$s.",              //$NON-NLS-1$
                    folder.getName(), SdkConstants.FN_SOURCE_PROP);
        } catch (IOException e) {
            log.warning("Ignoring sample '%1$s': failed reading %2$s.",                //$NON-NLS-1$
                    folder.getName(), SdkConstants.FN_SOURCE_PROP);
        } catch (AndroidVersionException e) {
            log.warning("Ignoring sample '%1$s': no android version found in %2$s.",   //$NON-NLS-1$
                    folder.getName(), SdkConstants.FN_SOURCE_PROP);
        }

        return null;
    }

}
