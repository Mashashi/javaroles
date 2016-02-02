package pt.mashashi.javaroles;

import java.io.File;

public class FileUtils {
	
	private FileUtils(){}
	
	public static String separatorAtEnt(String path){
		if(!path.endsWith(File.separator)){
			path += File.separator;
		}
		return path;
	}
	
}
