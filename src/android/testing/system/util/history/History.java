package android.testing.system.util.history;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 	<device attributes>
 * 		<command>
 * 			<date>...</date>
 * 			<sentence>...</sentence>
 * 		</command>
 * 		...
 * 	</device>
 * */
public class History {

	private Map<String, String> attributes;
	
	private List<Command> commands;
	
	public History() {
		attributes = new HashMap<String, String>();
		commands = new ArrayList<Command>();
	}
	
	public void addAttribute(String name, String value) {
		attributes.put(name, value);
	}
	
	public String getAttribute(String name) {
		return attributes.get(name);
	}
	
	public Map<String, String> getAttributes() {
		return attributes;
	}
	
	public void addCommand(Command command) {
		commands.add(command);
	}
	
	public List<Command> getCommands() {
		return commands;
	}
}
