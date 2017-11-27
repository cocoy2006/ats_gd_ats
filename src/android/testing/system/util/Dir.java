package android.testing.system.util;

import java.io.File;
/**
 * ά����Ŀ¼����
 * 
 * @author �Ÿ�������*/
public class Dir {
//	private final String parent = "screenCaptured";
	/**������Ŀ¼
	 * 
	 * @param parent ��Ŀ¼�����
	 * @param dirName ��Ŀ¼�����*/
	public void newDir(String parent, String dirName) {
		
		String dir = parent.concat(File.separator).concat(dirName);
		File directory = new File(dir);
		if(directory.isDirectory()) {
			//�׳��쳣�����߸����Ѿ����ڵ�Ŀ¼���Ľ���
			//System.out.println("Ŀ¼ '" + dirName + "' �Ѿ�����!");
		} else {
			directory.mkdir();
//			System.out.println("Ŀ¼ '" + dirName + "' �����ɹ�!");
		}
	}
	
	public static void main(String args[]) {
//		Dir f = new Dir();
//		f.newDir("temp");
	}
}
