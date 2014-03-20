package com.davecoss.tomcat.feedcat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * Represents one RSS message
 */
public class FeedMessage {

  String title;
  String description;
  String link;
  String author;
  String guid;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getGuid() {
    return guid;
  }

  public void setGuid(String guid) {
    this.guid = guid;
  }

  public String digestGuid() throws NoSuchAlgorithmException {
	MessageDigest md = MessageDigest.getInstance("MD5");
	md.update(guid.getBytes());
	byte[] md5bytes = md.digest();
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < md5bytes.length; i++)
		sb.append(Integer.toString((md5bytes[i] & 0xff) + 0x100, 16).substring(1));
	return sb.toString();
  }
  
  @Override
  public String toString() {
    return "FeedMessage [title=" + title + ", description=" + description
        + ", link=" + link + ", author=" + author + ", guid=" + guid
        + "]";
  }

} 