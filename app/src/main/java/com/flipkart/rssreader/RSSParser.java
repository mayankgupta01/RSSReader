package com.flipkart.rssreader;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created by mayank.gupta on 17/12/15.
 */
public class RSSParser extends DefaultHandler {
    private static final String TAG = "RSSParser";
    private boolean insideItem;
    private StringBuffer characterBuffer;
    public ArrayList<FeedItem> feedItems;
    private FeedItem feedItemObject;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        Log.i(TAG, "Started Parsing");
        feedItems = new ArrayList<FeedItem>();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        Log.i(TAG, "End Parsing");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if(localName.equalsIgnoreCase("item")) {
            insideItem = true;
            feedItemObject = new FeedItem();
        }

        if (insideItem && localName.equalsIgnoreCase("title")) {
            characterBuffer = new StringBuffer();
        }

        if (insideItem && localName.equalsIgnoreCase("link")) {
            characterBuffer = new StringBuffer();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if(localName.equalsIgnoreCase("item")) {
            insideItem = false;
            feedItems.add(feedItemObject);
        }

        if (insideItem && localName.equalsIgnoreCase("title")) {
            Log.i(TAG, "title " + characterBuffer.toString());
            feedItemObject.title = characterBuffer.toString();
            characterBuffer = null;
        }

        if (insideItem && localName.equalsIgnoreCase("link")) {
            Log.i(TAG, "link " + characterBuffer.toString());
            feedItemObject.link = characterBuffer.toString();
            characterBuffer = null;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (characterBuffer != null) {
            characterBuffer.append(ch,start,length);
        }
    }
}
