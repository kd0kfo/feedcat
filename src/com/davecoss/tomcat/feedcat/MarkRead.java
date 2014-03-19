package com.davecoss.tomcat.feedcat;

import java.io.*;
import java.sql.SQLException;

import javax.servlet.http.*;
import javax.servlet.*;


public class MarkRead extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<p><a href=\"/feedcat/list\">Return to List of Feeds</a></p>");
	
		String path = request.getRequestURI();
		
		String[] tokens = path.split("/");
		if(tokens.length < 5) {
			out.println("Missing Feed ID and/or Feed Entry ID");
			return;
		}
		
		int id = -1;
		String idString = tokens[tokens.length - 2];
		String guid = tokens[tokens.length - 1];
		try {
			id = Integer.parseInt(idString);
		} catch(NumberFormatException nfe) {
			out.println("Invalid ID: " + idString);
			return;
		}
		
		ListDB db = null;
		try {
			String dbpath = Config.getProperty("dbpath");
			db = new ListDB(dbpath);
			
			boolean markedRead = db.markRead(id, guid);
		} catch(Exception e) {
			out.println("Error: " + e.getMessage());
			e.printStackTrace(out);
		} finally {
			if(db != null)
				try {
					db.close();
				} catch(SQLException sqle) {
					throw new ServletException("Error closing database", sqle);
				}
		}
	}
}