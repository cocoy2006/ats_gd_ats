package android.testing.system.servlet;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import android.testing.system.util.Adb;
import android.testing.system.util.AutotestHandler;
import android.testing.system.util.Dir;

public class Upfiles extends HttpServlet {

	/**
	 * 使用方法:
	 * <form action="/servlet/Upfiles" enctype="multipart/form-data" method="post">
	 * 		<input type="file" name="file1"><br>
	 * 		<input type="file" name="file2"><br>
	 * 		<input type="file" name="file3"><br>
	 * 		<input type="submit">
	 * </form>
	 */
	private final long SIZE_MAX = 1024 * 1024 * 1000;
	private final long FILE_SIZE_MAX = 1024 * 1024 * 1000;
	
	private final String UPLOAD_DIR = "/sdcard/";
	private final String INSTALL_DIR = "/data/local/tmp/";
	private final String CHARSET = "UTF-8";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		
		if(request.getSession().getAttribute("percentage") != null) 
			request.getSession().removeAttribute("percentage");
		request.setCharacterEncoding(CHARSET);
		File file = upfile(request);
		
		String serialNumber = request.getSession().getAttribute("serialNumber").toString();
		
		String js = null;
		String way = request.getParameter("way");
		if(way.equalsIgnoreCase("upload")) {
			js = "<script type='text/javascript'>parent.uploadResult('"+file.getName()+"')</script>";
		} else if(way.equalsIgnoreCase("autotest")) {
			js = "<script type='text/javascript'>parent.autotestResult('"+serialNumber+"','"+file.getName()+"')</script>";
		}		
		response.setContentType("text/html;charset=" + CHARSET);
		response.getWriter().println(js);
	}

	private File upfile(final HttpServletRequest request) {
		// 设置目录
		String uploadDir = Adb.getInstance().getConf().getProperty("BaseUploadRealpath");
		String sessionId = request.getSession().getId();
		new Dir().newDir(uploadDir, sessionId);
		uploadDir = uploadDir.concat("/").concat(sessionId);
		// 设置参数
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 设置内存区块大小4KB
		factory.setSizeThreshold(4 * 1024);
		// 设置暂存容器，当上传文件大于设置的内存块大小时，用暂存容器做中转
		factory.setRepository(new File(uploadDir));
		ServletFileUpload fileUpload = new ServletFileUpload(factory);
		fileUpload.setHeaderEncoding(CHARSET);
		// 设置进度监听
		ProgressListener progressListener = new ProgressListener(){
		   private long megaBytes = -1;
		   public void update(long pBytesRead, long pContentLength, int pItems) {
		       long mBytes = pBytesRead / 10000;
		       if (megaBytes == mBytes && pBytesRead != pContentLength) {
		           return;
		       }
		       megaBytes = mBytes;
		       if (pContentLength == -1) {

		       } else {
		           double percentage = ((double) pBytesRead / (double) pContentLength);
		           request.getSession().setAttribute("percentage", percentage);
		       }
		   }
		};
		fileUpload.setProgressListener(progressListener);
		
//		fileUpload.setSizeMax(SIZE_MAX);
//		fileUpload.setFileSizeMax(FILE_SIZE_MAX);
		List<FileItem> fileItemList = null;
		try {
			fileItemList = fileUpload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		FileItem fileItem = null;
		File writeToFile = null;
		
		Iterator<FileItem> fileItemIterator = fileItemList.iterator();
		while (fileItemIterator.hasNext()) {
			fileItem = fileItemIterator.next();
			// 普通文件框上传
			if (fileItem.isFormField()) {

			} else {
				// 获取文件上传的文件名
				String OriginalFileName = takeOutFileName(fileItem.getName());
				if (!"".equals(OriginalFileName)) {					
					writeToFile = new File(uploadDir + File.separator + OriginalFileName);
					try {
						fileItem.write(writeToFile);
						break;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return writeToFile;
	}
	
	private String takeOutFileName(String filePath) {
//		String fileName = null;
//		try {
//			fileName = new String(filePath.getBytes("ISO-8859-1"), "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		String fileName = filePath;
		if (null != filePath && !"".equals(filePath)) {
			int port = filePath.lastIndexOf("\\" + 1);
			if(port != -1){
				fileName = filePath.substring(port);
			}
		}
//		fileName.trim().replaceAll("\\s", "_");
		return replace(fileName);
	}
	
	private String replace(String ori) {
		ori = ori.trim();
		char[] oriChar = ori.toCharArray();
		int len = oriChar.length;
		char[] fileChar = new char[len];
		for(int i = 0; i < len; i++) {
			if(oriChar[i] == ' ') {
				fileChar[i] = '_';
			} else {
				fileChar[i] = oriChar[i];
			}			
		}
		return String.copyValueOf(fileChar);
	}
}
