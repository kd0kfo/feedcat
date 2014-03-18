package com.davecoss.tomcat.feedcat;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;

public class Config {
	
	public static String getEnvValue(String key) throws NamingException {
		Context env = (Context)(new InitialContext()).lookup("java:comp/env");
		String propValue = (String) env.lookup(key);
		int envLoc = propValue.indexOf("ENV{");
        int endTag = propValue.indexOf("}");
        if(envLoc != -1 && endTag != -1) {
        	String envName = propValue.substring(envLoc + 4, endTag);
        	propValue = propValue.replace(propValue.substring(envLoc, endTag + 1), System.getenv(envName));
        }
        return propValue;
	}
	
	public static File getPropertiesFile() throws ServletException, NamingException{
		String path = getEnvValue("properties-path");
		File propertiesFile = new File(path);
        if(!propertiesFile.exists()) 
                throw new ServletException("Missing properties file: " + propertiesFile.getAbsolutePath());
        return propertiesFile;
	}
	
	public static String getProperty(String key, String def) throws ServletException, NamingException, IOException {
		File propertiesFile = getPropertiesFile();
		Properties props = new Properties();
		InputStream propsInput = null;
		try {
	        propsInput = new FileInputStream(propertiesFile);
	        props.load(propsInput);
	        return props.getProperty(key, def);
	    } finally {
			if(propsInput != null)
				propsInput.close();
		}
	}
	
	public static String getProperty(String key) throws ServletException, NamingException, IOException {
		return getProperty(key, null);
	}
	
                
}