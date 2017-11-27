package android.testing.system.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import com.android.monkeyrunner.MonkeyImage;

public class AutotestHandler {
	private final String SCHEMA_DIR = Adb.getInstance().getConf().getProperty("BaseSchemaRealpath");
	private final String SCHEMA = SCHEMA_DIR.concat("history.xsd");
	private final String PIC_FORMAT = "gif";
	private Adb adb;
	private int counter = 1000 * 1000;
	
	public static void main(String args[]) {
		AutotestHandler test  = new AutotestHandler();
		test.Start(new File("D:\\history.xml"), null, null);
	}
	
	public void Start(File xml, String serialNumber, String sessionId) {
		adb = Adb.getInstance();
		String parent = createDir(sessionId);
		
		Document doc = validation(xml);
		Element root = doc.getRootElement();
		List<Element> commandList = root.elements("command");
		long time = -1; 
		long interval = 0; //����������ʱ����
		long cur;
		long allTime = 0; //��ĵ���ʱ��
		String sentence;
		for(Element command : commandList) {
			cur = Long.parseLong(command.element("date").getTextTrim());
			if(time == -1) time = cur;
			interval = cur - time;
			allTime += interval;
			time = cur;
			if(interval != 0) {
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			sentence = command.element("sentence").getTextTrim();
			if(sentence.startsWith("UPLOAD")) break;
			//take snapshot
			takeSnapshot(serialNumber, parent, "before", sentence);
			//execute command
			new Commands(serialNumber).executeCommand(sentence);
			//take snapshot again
			takeSnapshot(serialNumber, parent, "after", sentence);
			
		}
	}
	
	// D:/temp/runTestcaseResult/username/serialNumber/Fri 2011 11 13../*.gif
	public void start(File file, String serialNumber, String username) {
		adb = Adb.getInstance();
		String parent = adb.getConf().getProperty("runTestcaseResult").concat(username).concat(File.separator).concat(serialNumber)
			.concat(File.separator).concat(new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())).concat(File.separator);
		File directory = new File(parent);
		if(!directory.exists()) directory.mkdirs();
		
		Document doc = validation(file);
		Element root = doc.getRootElement();
		List<Element> commandList = root.elements("command");
		long time = -1; 
		long interval = 0; //����������ʱ����
		long cur;
		long allTime = 0; //��ĵ���ʱ��
		String sentence;
		for(Element command : commandList) {
			cur = Long.parseLong(command.element("date").getTextTrim());
			if(time == -1) time = cur;
			interval = cur - time;
			allTime += interval;
			time = cur;
			if(interval != 0) {
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			sentence = command.element("sentence").getTextTrim();
			if(sentence.startsWith("UPLOAD")) break;
			//take snapshot
			takeSnapshot(serialNumber, parent, "before", sentence);
			//execute command
			new Commands(serialNumber).executeCommand(sentence);
			//take snapshot again
			takeSnapshot(serialNumber, parent, "after", sentence);
		}
	}
	
	private String createDir(String sessionId) {
		String parent = adb.getConf().getProperty("BaseAutotestResultRealpath");
		new Dir().newDir(parent, sessionId);
		return parent.concat("/").concat(sessionId);
	}
	
	private Document validation(File xml) {
		FileInputStream fis = null;
		Document doc = null;
		try {
			fis = new FileInputStream(xml);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		SAXReader reader = new SAXReader();
		reader.setStripWhitespaceText(true);
		reader.setMergeAdjacentText(true);
		try {
			doc = reader.read(fis);
		} catch (DocumentException e) {
			System.out.println(e.getMessage());
		}
		return doc;
	}
	
	private Document validation(String xml) {
		Document doc = null;
		try {
			SAXReader reader = new SAXReader(true);
			reader.setStripWhitespaceText(true);
			reader.setMergeAdjacentText(true);
			
			reader.setFeature("http://xml.org/sax/features/validation", true);  
	        reader.setFeature("http://apache.org/xml/features/validation/schema", true);  
	        reader.setFeature("http://apache.org/xml/features/validation/schema-full-checking",true);
	        reader.setProperty(
	        	"http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", SCHEMA);
	        doc = reader.read(this.getClass().getResourceAsStream(xml));
		} catch(DocumentException e) {
			System.out.println(e.getMessage());
		} catch(SAXException e) {
			System.out.println(e.getMessage());
		}
		return doc;
	}
	
	private void takeSnapshot(String serialNumber, String parent, String when, String command) {
		MonkeyImage image = adb.getMonkeyDevice(serialNumber).takeSnapshot();
		String imageName = takeImageName(parent, when, command);
		image.writeToFile(imageName, PIC_FORMAT);
	}
	
	private String takeImageName(String parent, String when, String command) {
		StringBuffer sb = new StringBuffer(parent);
		sb.append(String.valueOf(++counter)).append("_").append(when)
			.append("_").append(command).append(".").append(PIC_FORMAT);
		return sb.toString();
	}
}
