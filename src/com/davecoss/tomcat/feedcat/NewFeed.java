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


public class NewFeed extends HttpServlet {
	
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String url = request.getParameter("url");
		if(url == null || url.length() == 0) {
			out.println("Missing URL");
			return;
		}
		
		ListDB db = null;
		
		try {
			String dbpath = Config.getProperty("dbpath");
			db = new ListDB(dbpath);
			
			// Get Feed
			FeedScraper feeder = new FeedScraper(url);
			Feed feed = feeder.readFeed();
			
			db.putFeed(feed);
			out.println("Added " + feed.getTitle());
		} catch (Exception e) {
			out.println("Error adding feed:");
			out.println(e.getMessage());
			e.printStackTrace(out);
			return;
		} finally {
			if(db != null) {
				try {
					db.close();
				} catch (SQLException sqle) {
					out.println("Error closing database");
					out.println(sqle.getMessage());
				}
			}
			out.println("<p><a href=\"/feedcat/newfeed.jsp\">Add another feed</a></p>");
			out.println("<p><a href=\"/feedcat/index.jsp\">Return to Main Page</a></p>");
		}
	}
}