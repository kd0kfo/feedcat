package com.davecoss.tomcat.feedcat;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.http.*;
import javax.servlet.*;

public class List extends HttpServlet {
	
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<p><a href=\"/feedcat/index.jsp\">Return to Main Page</a></p>");
		ListDB db = null;
		try {
			String dbpath = Config.getProperty("dbpath");
			db = new ListDB(dbpath);
			
			java.util.List<ListDB.IDURLPair> feeds = db.getFeedList();
			if(!feeds.isEmpty()) {
				out.println("<ul id=\"feedlist\">");
				populateList(feeds, out);
				out.println("</ul>");
			}
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
	
	private void populateList(java.util.List<ListDB.IDURLPair> feeds, PrintWriter out) {
		Iterator<ListDB.IDURLPair> it = feeds.iterator();
		while(it.hasNext()) {
			ListDB.IDURLPair pair = it.next();
			FeedScraper feedScraper = new FeedScraper(pair.url, pair.id);
			Feed feed = feedScraper.readFeed();
			String pubdate = feed.getPubDate();
			if(pubdate.length() != 0)
				pubdate = String.format("(Last Updated %s)", pubdate);
			String readURL = String.format("/feedcat/feed/%d", pair.id);
			String feedURL = String.format("/feedcat/feedinfo/%d", pair.id);
			String msg = String.format("<li><a href=\"%s\">%s</a> (<a href=\"%s\">Info</a>): %s %s</li>",
					readURL, feed.getTitle(), feedURL, feed.getDescription(), pubdate);
			out.println(msg);
		}
	}
}