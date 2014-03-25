package com.davecoss.tomcat.feedcat;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Iterator;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.xml.stream.XMLStreamException;

public class List extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<p><a href=\"/feedcat/index.jsp\">Return to Main Page</a></p>");
		
		boolean countNew = (request.getParameter("new") != null);
		
		ListDB db = null;
		try {
			String dbpath = Config.getProperty("dbpath");
			db = new ListDB(dbpath);
			
			java.util.List<ListDB.IDURLPair> feeds = db.getFeedList();
			if(!feeds.isEmpty()) {
				out.println("<ul id=\"feedlist\">");
				populateList(feeds, db, countNew, out);
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
	
	private void populateList(java.util.List<ListDB.IDURLPair> feeds, ListDB db, boolean countNew, PrintWriter out) {
		Iterator<ListDB.IDURLPair> it = feeds.iterator();
		while(it.hasNext()) {
			ListDB.IDURLPair pair = it.next();
			FeedScraper feedScraper = new FeedScraper(pair.url, pair.id);
			Feed feed = null;
			try {
				feed = feedScraper.readFeed();
			} catch(XMLStreamException xmlse) {
				out.println("<li>Error reading feed " + pair.url + "</li>");
				continue;
			}
			String pubdate = feed.getPubDate();
			String newCountString = " ";
			if(pubdate.length() != 0)
				pubdate = String.format("(Last Updated %s)", pubdate);
			if(countNew) {
				try {
					int newEntries = db.countNew(feed);
					if(newEntries != 0) {
						newCountString = String.format("(%d New)", newEntries);
					}
				} catch(SQLException sqle) {
					//
				} catch(NoSuchAlgorithmException nsae) {
					//
				}
			}
			String readURL = String.format("/feedcat/feed/%d", pair.id);
			String feedURL = String.format("/feedcat/feedinfo/%d", pair.id);
			String msg = String.format("<li><a href=\"%s\">%s</a>%s(<a href=\"%s\">Info</a>): %s %s</li>",
					readURL, feed.getTitle(), newCountString, feedURL, feed.getDescription(), pubdate);
			out.println(msg);
		}
	}
}