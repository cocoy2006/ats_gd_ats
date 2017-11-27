package android.testing.system.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import android.testing.system.testcase.TestcaseHandler;
import android.testing.system.util.history.Command;
import android.testing.system.util.history.Date;
import android.testing.system.util.history.History;
import android.testing.system.util.history.Sentence;

public class NewTestcaseHandler {

	private String username;
	private String project;
	private String testcase;
	private History history;
	
	public NewTestcaseHandler(String serialNumber, String username, String project, String testcase) {
		this.username = username;
		this.project = project;
		this.testcase = testcase;
		DeviceWithTimer device = Adb.getInstance().getLock().getLocks().get(serialNumber);
		device.setHistory(new History());
		history = device.getHistory();
	}
	
	public int handle() {
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
		
		File directory = new File(Adb.getInstance().getConf().getProperty("temp")
			.concat(username).concat(File.separator).concat(project).concat(File.separator));
		if(!directory.exists()) directory.mkdirs();
		File file = new File(directory.getAbsolutePath().concat(File.separator).concat(testcase));
		
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");			
			XMLWriter writer = new XMLWriter(new FileWriter(file), format);
			writer.write(docEle);
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		int result = new TestcaseHandler().newTestcase(username, project, file);
		return result;
	}
	
	public void addHistory(String exeCmd, String exeDate) {
		Command cmd = new Command();
		Date date = new Date();
		date.setText(exeDate);
		cmd.setDate(date);
		Sentence sentence = new Sentence();
		sentence.setText(exeCmd);
		cmd.setSentence(sentence);
		history.addCommand(cmd);
	}
}
