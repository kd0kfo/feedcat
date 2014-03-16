package com.davecoss.tomcat.feedcat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ListDB {
	
	private final String dbpath;
	private final Connection conn;
	
	
	public ListDB(String path) throws ClassNotFoundException, SQLException {
		this.dbpath = path;
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:" + dbpath);
        Statement stat = newStatement();
        try {
        	stat.execute("create table if not exists feeds (id INTEGER PRIMARY KEY, url STRING UNIQUE, title STRING, misc STRING);");
        } finally {
        	stat.close();
        }
	}
	
	public void close() throws SQLException {
		conn.close();
	}
	
	public void putFeed(Feed feed) throws SQLException {
		String sql = String.format("insert on conflict replace into feeds values (null, '%s', '%s', null);",
				feed.getLink(), feed.getTitle());
		Statement stat = newStatement();
		try {
			stat.execute(sql);
		} finally {
			stat.close();
		}
	}
	
	public FeedScraper getFeed(String url) throws SQLException {
		String sql = String.format("select * from feeds where url == '%s';", url);
		Statement stat = newStatement();
		ResultSet result = null;
		try {
			result = stat.executeQuery(sql);
			if(result.next())
				return new FeedScraper(result.getString("url"));
			else
				return null;
		} finally {
			stat.close();
			if(result != null)
				result.close();
		}
	}
	
	public FeedScraper getFeed(int id) throws SQLException {
		String sql = String.format("select * from feeds where id == '%d';", id);
		Statement stat = newStatement();
		ResultSet result = null;
		try {
			result = stat.executeQuery(sql);
			if(result.next())
				return new FeedScraper(result.getString("url"));
			else
				return null;
		} finally {
			stat.close();
			if(result != null)
				result.close();
		}
	}
	
	public List<IDURLPair> getFeedList() throws SQLException {
		String sql = "select * from feeds;";
		Statement stat = newStatement();
		ResultSet result = null;
		ArrayList<IDURLPair> urls = new ArrayList<IDURLPair>();
		try {
			result = stat.executeQuery(sql);
			while(result.next())
				urls.add(new IDURLPair(result.getInt("id"), result.getString("url")));
			return urls;
		} finally {
			stat.close();
			if(result != null)
				result.close();
		}
	}
	
	public boolean removeFeed(int id) throws SQLException {
		String sql = String.format("delete from feeds where id == %d;", id);
		Statement stat = newStatement();
		try {
			return stat.execute(sql);
		} finally {
			stat.close();
		}
	}
	
	public boolean removeFeed(String url) throws SQLException {
		String sql = String.format("delete from feeds where url == '%s';", url);
		Statement stat = newStatement();
		try {
			return stat.execute(sql);
		} finally {
			stat.close();
		}
	}
	
	private Statement newStatement() throws SQLException {
		return conn.createStatement(); 
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