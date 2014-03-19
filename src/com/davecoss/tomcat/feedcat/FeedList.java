package com.davecoss.tomcat.feedcat;

import java.io.*;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Stack;

import javax.servlet.http.*;
import javax.servlet.*;


public class FeedList extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	static final String URL_ENCODE_TYPE = "UTF-8";

	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<p><a href=\"/feedcat/list\">Return to List of Feeds</a></p>");
	
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
			String dbpath = Config.getProperty("dbpath");
			db = new ListDB(dbpath);
			
			FeedScraper feeder = db.getFeed(id);
			if(feeder != null) {
				Feed feed = feeder.readFeed();
				out.println("Feed: " + feed.getTitle());
				out.println("<br/>");
				out.println("<ul id=\"entries\">");
				Stack<FeedMessage> messages = feed.getMessageStack();
				while(!messages.empty()) {
					printEntry(messages.pop(), id, out);
				}
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
	
	private void printEntry(FeedMessage message, int feedid, PrintWriter out) {
		String description = message.getDescription();
		String extra = null;
		if(description.length() > 100)
			extra = description.substring(0, 100) + "...";
		else
			extra = description;
		if(extra.length() != 0)
			extra = "<p>" + extra + "</p>";
		String readURL = null;
		try {
			readURL = String.format("/feedcat/markread/%d/%s", feedid, URLEncoder.encode(message.getGuid(), URL_ENCODE_TYPE)); // TODO: Make this better... This is just for prototyping.
		} catch(UnsupportedEncodingException uee) {
			readURL = "";
		}
		String msg = String.format("<li>(<a href=\"%s\">Read</a>) <a href=\"%s\">%s</a>%s</li>", readURL, message.getLink(), message.getTitle(), extra);
		out.println(msg);
	}
	
}