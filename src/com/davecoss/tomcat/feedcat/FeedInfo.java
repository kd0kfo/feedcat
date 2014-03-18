package com.davecoss.tomcat.feedcat;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Stack;

import javax.servlet.http.*;
import javax.servlet.*;


public class FeedInfo extends HttpServlet {
	
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String path = request.getRequestURI();
		
		String[] tokens = path.split("/");
		if(tokens.length < 4) {
			out.println("Missing Feed ID");
			return;
		}
		
		int id = -1;
		String idString = tokens[tokens.length - 1];
		try {
			id = Integer.parseInt(idString);
		} catch(NumberFormatException nfe) {
			out.println("Invalid ID: " + idString);
			return;
		}
		
		
		ListDB db = null;
		try {
			db = new ListDB(Config.getProperty("dbpath"));
			FeedScraper feeder = db.getFeed(id);
			Feed feed = feeder.readFeed();
			out.println("<ul>");
			out.println("<li>Feed Title" + feed.getTitle() + "</li>");
			out.println("<li>Feed URL: " + feed.getUrl() + "</li>");
			out.println("<li>Pub Date: " + feed.getPubDate() + "</li>");
			out.println("<li>Description:<br/>" + feed.getDescription() + "</li>");
			out.println("</ul>");
		} catch(Exception e) {
			out.println("Error getting feed information");
			out.println(e.getMessage());
		} finally {
			if(db != null)
				try {
					db.close();
				} catch(SQLException sqle) {
					out.println("Error closing database");
					out.println(sqle.getMessage());
					sqle.printStackTrace(out);
					return;
				}
		}
	}
}