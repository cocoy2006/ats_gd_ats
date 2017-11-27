package android.testing.system.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import android.testing.system.util.Adb;
import android.testing.system.util.history.Command;
import android.testing.system.util.history.History;

public class Export extends HttpServlet {

	private final String FILE_NAME = "history.xml";
	private final String CHARSET = "UTF-8";
	private History history;
	private String serialNumber;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		serialNumber = request.getParameter("serialNumber");
		history = Adb.getInstance().getLock().getLocks().get(serialNumber).getHistory();
		
		Document docEle = DocumentHelper.createDocument();
		Element hisEle = docEle.addElement("history");
		Map<String, String> attributes = history.getAttributes();
		for(String name : attributes.keySet()) {
			hisEle.addAttribute(name, attributes.get(name));
		}
		List<Command> commands = history.getCommands();
		for(int i = 0; i < commands.size(); i++) {
			Element command = hisEle.addElement("command");
			command.addElement("date").setText(commands.get(i).getDate().getText());
			command.addElement("sentence").setText(commands.get(i).getSentence().getText());
		}
		
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding(CHARSET);			
			XMLWriter writer = new XMLWriter(new FileWriter(new File(FILE_NAME)), format);
			writer.write(docEle);
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(FILE_NAME));
		int available = in.available();
		ByteArrayOutputStream out = new ByteArrayOutputStream(available);
		byte[] temp = new byte[available];
		int size = 0;
		while ((size = in.read(temp)) != -1) {
			out.write(temp, 0, size);
		}
		in.close();
		
		
		response.reset();
		response.setContentType("application/xml;charset=".concat(CHARSET));
		response.setHeader("Content-Disposition", "attachment;filename=".concat(FILE_NAME)); 
		
		
		
		response.getOutputStream().write(temp);
		
	}


}
