package com.davecoss.tomcat.feedcat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.sqlite.*;

public class ListDB {
	
	private final String dbpath;
	private final Connection conn;
	
	
	public ListDB(String path) throws ClassNotFoundException, SQLException {
		this.dbpath = path;
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:" + dbpath);
		PreparedStatement stat = null;
        try {
        	stat = conn.prepareStatement("create table if not exists feeds (id INTEGER PRIMARY KEY, url STRING UNIQUE, title STRING, misc STRING);");
        	stat.execute();
        } finally {
        	if(stat != null)
        		stat.close();
        }
	}
	
	public void close() throws SQLException {
		conn.close();
	}
	
	public boolean putFeed(Feed feed) throws SQLException {
		PreparedStatement stat = conn.prepareStatement("insert or replace into feeds values (null, ? , ? , null);");
		stat.setString(1, feed.getUrl().toString());
		stat.setString(2, feed.getTitle());
		try {
			return stat.execute();
		} finally {
			stat.close();
		}
	}
	
	public FeedScraper getFeed(String url) throws SQLException {
		PreparedStatement stat = conn.prepareStatement("select * from feeds where url == ?;");
		stat.setString(1, url);
		ResultSet result = null;
		try {
			result = stat.executeQuery();
			if(result.next())
				return new FeedScraper(result.getString("url"), result.getInt("id"));
			else
				return null;
		} finally {
			stat.close();
			if(result != null)
				result.close();
		}
	}
	
	public FeedScraper getFeed(int id) throws SQLException {
		PreparedStatement stat = conn.prepareStatement("select * from feeds where id == ?;");
		stat.setInt(1, id);
		ResultSet result = null;
		try {
			result = stat.executeQuery();
			if(result.next())
				return new FeedScraper(result.getString("url"), result.getInt("id"));
			else
				return null;
		} finally {
			stat.close();
			if(result != null)
				result.close();
		}
	}
	
	public List<IDURLPair> getFeedList() throws SQLException {
		PreparedStatement stat = conn.prepareStatement("select * from feeds;");
		ResultSet result = null;
		ArrayList<IDURLPair> urls = new ArrayList<IDURLPair>();
		try {
			result = stat.executeQuery();
			while(result.next())
				urls.add(new IDURLPair(result.getInt("id"), result.getString("url")));
			return urls;
		} finally {
			stat.close();
			if(result != null)
				result.close();
		}
	}	
	
	public class IDURLPair {
		public final int id;
		public final String url;
		
		public IDURLPair(int id, String url) {
			this.id = id;
			this.url = url;
		}
	}
}