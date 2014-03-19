package com.davecoss.tomcat.feedcat;

/**
 * 
 * Source code adapted from the excellent tutorials of Lars Vogel, whos code is available under the Eclipse Public License
 * version 1.0 http://www.vogella.com/tutorials/RSSFeed/article.html
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class FeedScraper {
  static final String TITLE = "title";
  static final String DESCRIPTION = "description";
  static final String CHANNEL = "channel";
  static final String LANGUAGE = "language";
  static final String COPYRIGHT = "copyright";
  static final String LINK = "link";
  static final String AUTHOR = "author";
  static final String ITEM = "item";
  static final String ENTRY = "entry"; // added for atom.
  static final String PUB_DATE = "pubDate";
  static final String GUID = "guid";
  static final String ENTRY_ID = "id";

  final URL url;
  private Integer dbid = null;

  public FeedScraper(String feedUrl) {
    try {
      this.url = new URL(feedUrl);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
  
  public FeedScraper(String feedUrl, Integer dbid) {
	    try {
	      this.url = new URL(feedUrl);
	      this.dbid = dbid;
	    } catch (MalformedURLException e) {
	      throw new RuntimeException(e);
	    }
	  }

  public Feed readFeed() {
    Feed feed = null;
    try {
      boolean isFeedHeader = true;
      // Set header values intial to the empty string
      String description = "";
      String title = "";
      String link = "";
      String language = "";
      String copyright = "";
      String author = "";
      String pubdate = "";
      String guid = "";

      // First create a new XMLInputFactory
      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      // Setup a new eventReader
      InputStream in = read();
      XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
      // read the XML document
      while (eventReader.hasNext()) {
        XMLEvent event = eventReader.nextEvent();
        if (event.isStartElement()) {
        	StartElement startElement = event.asStartElement();
          String localPart = startElement.getName()
              .getLocalPart();
          switch (localPart) {
          case ITEM: case ENTRY:
            if (isFeedHeader) {
              isFeedHeader = false;
              feed = new Feed(url, title, link, description, language,
                  copyright, pubdate);
            }
            event = eventReader.nextEvent();
            break;
          case TITLE:
            title = getCharacterData(event, eventReader);
            break;
          case DESCRIPTION:
            description = getCharacterData(event, eventReader);
            break;
          case LINK:
        	  Attribute relAttrib = startElement.getAttributeByName(new QName("rel"));
        	  Attribute hrefAttrib = startElement.getAttributeByName(new QName("href"));
        	  if(relAttrib != null && hrefAttrib != null) {
        		  if(relAttrib.getValue().equals("alternate")) {
        			  link = hrefAttrib.getValue();
        		  }
        	  } else {
        		  link = getCharacterData(event, eventReader);
        	  }
            break;
          case GUID: case ENTRY_ID:
            guid = getCharacterData(event, eventReader);
            break;
          case LANGUAGE:
            language = getCharacterData(event, eventReader);
            break;
          case AUTHOR:
            author = getCharacterData(event, eventReader);
            break;
          case PUB_DATE:
            pubdate = getCharacterData(event, eventReader);
            break;
          case COPYRIGHT:
            copyright = getCharacterData(event, eventReader);
            break;
          }
        } else if (event.isEndElement()) {
        	String tagtype = event.asEndElement().getName().getLocalPart();
          if (tagtype.equals(ITEM) || tagtype.equals(ENTRY)) {
            FeedMessage message = new FeedMessage();
            message.setAuthor(author);
            message.setDescription(description);
            message.setGuid(guid);
            message.setLink(link);
            message.setTitle(title);
            feed.getMessages().add(message);
            event = eventReader.nextEvent();
            continue;
          }
        }
      }
    } catch (XMLStreamException e) {
      throw new RuntimeException(e);
    }
    return feed;
  }

  private String getCharacterData(XMLEvent event, XMLEventReader eventReader)
      throws XMLStreamException {
    String result = "";
    event = eventReader.nextEvent();
    if (event instanceof Characters) {
      result = event.asCharacters().getData();
    }
    return result;
  }

  private InputStream read() {
    try {
      return url.openStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public Integer getDBId() {
	  return dbid;
  }
  
  public void setDBId(int dbid) {
	  this.dbid = new Integer(dbid);
  }
  
  public void setDBId(Integer dbid) {
	  this.dbid = dbid;
  }
} 