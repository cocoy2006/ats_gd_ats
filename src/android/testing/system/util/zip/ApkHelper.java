package android.testing.system.util.zip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.android.sdklib.xml.AndroidManifestParser;
import com.android.sdklib.xml.ManifestData;

public class ApkHelper {
	private static ZipInputStream zipIn;
	private static ZipEntry zipEntry;
	private static byte[] buf = new byte[512];
	private static int readedBytes;
	private static final float[] RADIX_MULTS = { 0.0039063F, 3.051758E-005F,
			1.192093E-007F, 4.656613E-010F };

//	private static final String[] DIMENSION_UNITS = { "px", "dip", "sp", "pt",
//			"in", "mm", "", "" };
//
//	private static final String[] FRACTION_UNITS = { "%", "%p", "", "", "", "",
//			"", "" };

	/**
	 * unzip .apk file, and return 'AndroidManifest.xml' file
	 * 
	 * @param .apk file's absolute path
	 * @return AndroidManifest.xml file or null
	 * */
	public static File unZip(String apkFilePath) {
		try {
			File apkFile = new File(apkFilePath);
			String apkFileParent = apkFile.getParent();
			String apkFileName = apkFile.getName();
			zipIn = new ZipInputStream(new BufferedInputStream(
					new FileInputStream(apkFile)));

			while ((zipEntry = zipIn.getNextEntry()) != null) {
				if ("AndroidManifest.xml".equalsIgnoreCase(zipEntry.getName())) {
					File file = new File(apkFileParent + File.separatorChar + apkFileName + ".xml");
					FileOutputStream fileOut = new FileOutputStream(file);
					while ((readedBytes = zipIn.read(buf)) > 0) {
						fileOut.write(buf, 0, readedBytes);
					}
					fileOut.close();
					return file;
				}
				zipIn.closeEntry();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	public static ManifestData getManifestData(String apkFilePath) {
		return getManifestData(unZip(apkFilePath));
	}
	
	public static ManifestData getManifestData(File androidManifestXml) {
		File amx = AXMLPrinter.parseToText(androidManifestXml.getAbsolutePath());
		try {
			return AndroidManifestParser.parse(new FileInputStream(amx));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
//	public static String getComponent(String apkFilePath) {
//		return getComponent(getManifestData(unZip(apkFilePath)));
//	}
	
	public static String getComponent(ManifestData md) {
		if(md != null) {
			return md.getPackage().concat("/").concat(md.getLauncherActivity().getName()); 
		}
		return null;
	}
	
	public static String getPackage(ManifestData md) {
		if(md != null) {
			return md.getPackage();
		}
		return null;
	}

//	public static String getMainActivityName(String androidManifestXml) {
//		AXmlResourceParser parser = new AXmlResourceParser();
//		try {
//			parser.open(new FileInputStream(androidManifestXml));
//			while (true) {
//				int type = parser.next();
//				if (type == 2 && "activity".equals(parser.getName())) {
//					for (int i = 0; i != parser.getAttributeCount(); i++) {
//						if ("name".equals(parser.getAttributeName(i))) {
//							return getAttributeValue(parser, i);
//						}
//					}
//				}
//			}
//		} catch (FileNotFoundException e) {
//			System.out.println(e.getMessage());
//		} catch (XmlPullParserException e) {
//			System.out.println(e.getMessage());
//		} catch (IOException e) {
//			System.out.println(e.getMessage());
//		}
//		return null;
//	}

//	private static String getAttributeValue(AXmlResourceParser parser, int index) {
//		int type = parser.getAttributeValueType(index);
//		int data = parser.getAttributeValueData(index);
//		if (type == 3) {
//			return parser.getAttributeValue(index);
//		}
//		if (type == 2) {
//			return String.format("?%s%08X", new Object[] { getPackage(data),
//					Integer.valueOf(data) });
//		}
//		if (type == 1) {
//			return String.format("@%s%08X", new Object[] { getPackage(data),
//					Integer.valueOf(data) });
//		}
//		if (type == 4) {
//			return String.valueOf(Float.intBitsToFloat(data));
//		}
//		if (type == 17) {
//			return String.format("0x%08X",
//					new Object[] { Integer.valueOf(data) });
//		}
//		if (type == 18) {
//			return data != 0 ? "true" : "false";
//		}
//		if (type == 5) {
//			return Float.toString(complexToFloat(data))
//					+ DIMENSION_UNITS[(data & 0xF)];
//		}
//		if (type == 6) {
//			return Float.toString(complexToFloat(data))
//					+ FRACTION_UNITS[(data & 0xF)];
//		}
//		if ((type >= 28) && (type <= 31)) {
//			return String.format("#%08X",
//					new Object[] { Integer.valueOf(data) });
//		}
//		if ((type >= 16) && (type <= 31)) {
//			return String.valueOf(data);
//		}
//		return String.format("<0x%X, type 0x%02X>", new Object[] {
//				Integer.valueOf(data), Integer.valueOf(type) });
//	}

//	private static String getPackage(int id) {
//		if (id >>> 24 == 1) {
//			return "android:";
//		}
//		return "";
//	}

	public static float complexToFloat(int complex) {
		return (complex & 0xFFFFFF00) * RADIX_MULTS[(complex >> 4 & 0x3)];
	}
}
