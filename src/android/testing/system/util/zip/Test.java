package android.testing.system.util.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.android.sdklib.xml.AndroidManifestParser;
import com.android.sdklib.xml.ManifestData;
import com.android.sdklib.xml.ManifestData.Activity;

public class Test {
	private ZipInputStream  zipIn;      //瑙ｅ帇Zip 
    private ZipOutputStream zipOut;     //鍘嬬缉Zip 
    private ZipEntry        zipEntry; 
    private static int      bufSize;    //size of bytes 
    private byte[]          buf; 
    private int             readedBytes; 
     
    public Test(){ 
        this(512); 
    } 

    public Test(int bufSize){ 
        this.bufSize = bufSize; 
        this.buf = new byte[this.bufSize]; 
    } 
     
    //鍘嬬缉鏂囦欢澶瑰唴鐨勬枃浠�
    public void doZip(String zipDirectory){//zipDirectoryPath:闇�鍘嬬缉鐨勬枃浠跺す鍚�
        File file; 
        File zipDir; 

        zipDir = new File(zipDirectory); 
        String zipFileName = zipDir.getName() + ".zip";//鍘嬬缉鍚庣敓鎴愮殑zip鏂囦欢鍚�

        try{ 
            this.zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFileName))); 
            handleDir(zipDir , this.zipOut); 
            this.zipOut.close(); 
        }catch(IOException ioe){ 
            ioe.printStackTrace(); 
        } 
    } 

    //鐢眃oZip璋冪敤,閫掑綊瀹屾垚鐩綍鏂囦欢璇诲彇 
    private void handleDir(File dir , ZipOutputStream zipOut)throws IOException{ 
        FileInputStream fileIn; 
        File[] files; 

        files = dir.listFiles(); 
     
        if(files.length == 0){//濡傛灉鐩綍涓虹┖,鍒欏崟鐙垱寤轰箣. 
            //ZipEntry鐨刬sDirectory()鏂规硶涓�鐩綍浠�/"缁撳熬. 
            this.zipOut.putNextEntry(new ZipEntry(dir.toString() + "/")); 
            this.zipOut.closeEntry(); 
        } 
        else{//濡傛灉鐩綍涓嶄负绌�鍒欏垎鍒鐞嗙洰褰曞拰鏂囦欢. 
            for(File fileName : files){ 
                //System.out.println(fileName); 

                if(fileName.isDirectory()){ 
                    handleDir(fileName , this.zipOut); 
                } 
                else{ 
                    fileIn = new FileInputStream(fileName); 
                    this.zipOut.putNextEntry(new ZipEntry(fileName.toString())); 

                    while((this.readedBytes = fileIn.read(this.buf))>0){ 
                        this.zipOut.write(this.buf , 0 , this.readedBytes); 
                    } 

                    this.zipOut.closeEntry(); 
                } 
            } 
        } 
    } 

    //瑙ｅ帇鎸囧畾zip鏂囦欢 
    public void unZip(String unZipfileName){//unZipfileName闇�瑙ｅ帇鐨剒ip鏂囦欢鍚�
        FileOutputStream fileOut; 
        File file; 

        try{ 
            this.zipIn = new ZipInputStream (new BufferedInputStream(new FileInputStream(unZipfileName))); 

            while((this.zipEntry = this.zipIn.getNextEntry()) != null){ 
                file = new File(this.zipEntry.getName()); 
                System.out.println(file.getAbsolutePath());/// 

//                if(this.zipEntry.isDirectory()){ 
//                    file.mkdirs(); 
//                } 
//                else{ 
//                    //濡傛灉鎸囧畾鏂囦欢鐨勭洰褰曚笉瀛樺湪,鍒欏垱寤轰箣. 
//                    File parent = file.getParentFile(); 
//                    if(parent != null && !parent.exists()){ 
//                        parent.mkdirs(); 
//                    } 
//
//                    fileOut = new FileOutputStream(file); 
//                    while(( this.readedBytes = this.zipIn.read(this.buf) ) > 0){ 
//                        fileOut.write(this.buf , 0 , this.readedBytes ); 
//                        
//                    } 
//                    fileOut.close(); 
//                } 
                if("AndroidManifest.xml".equalsIgnoreCase(file.getName())) {                	
//                	try {
//                		ManifestData md = AndroidManifestParser.parse(new FileInputStream(file));
//                		System.out.println(md.getActivities());
//					} catch (SAXException e) {
//						System.out.println(e.getMessage());
//					} catch (StreamException e) {
//						System.out.println(e.getMessage());
//					} catch (ParserConfigurationException e) {
//						System.out.println(e.getMessage());
//					}
                }
                this.zipIn.closeEntry();     
            } 
        }catch(IOException ioe){ 
        	System.out.println(ioe.getMessage());
        } 
    } 

    //璁剧疆缂撳啿鍖哄ぇ灏�
    public void setBufSize(int bufSize){ 
        this.bufSize = bufSize; 
    } 

    //娴嬭瘯Zip绫�
    public static void main(String[] args)throws Exception{ 
//    	Adb adb = Adb.getInstance();
//    	AdbMonkeyDevice amd = (AdbMonkeyDevice) adb.getMonkeyDevice("emulator-5554");
//    	Set<String> categories = new HashSet<String>();
//    	Map<String, Object> extras = new HashMap<String, Object>();
////    	amd.startActivity("", "", "", "", categories, extras, "com.speedsoftware.rootexplorer/com.speedsoftware.rootexplorer.RootExplorer", 0);
//    	amd.startActivity("", "", "", "", categories, extras, "com.uc.browser/com.uc.browser.ActivityUpdate", 0);
//    	System.out.println("start activity complete.");
//    	System.exit(0);
    	
    	File file = ApkHelper.unZip("D:\\Developments\\ATS\\apk\\t.apk");
    	File amx = AXMLPrinter.parseToText(file.getAbsolutePath());
    	ManifestData md = AndroidManifestParser.parse(new FileInputStream(amx));
    	System.out.println(md.getPackage());
    	System.out.println(md.getLauncherActivity().getName());
//    	System.out.println(ApkHelper.getMainActivityName(file.getAbsolutePath()));
    } 

}