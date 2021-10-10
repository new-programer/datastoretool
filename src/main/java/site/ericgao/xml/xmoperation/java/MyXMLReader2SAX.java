package site.ericgao.xml.xmoperation.java;

import jdk.internal.org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MyXMLReader2SAX extends DefaultHandler {

    java.util.Stack tags = new java.util.Stack();

    public MyXMLReader2SAX() {
        super();
    }

    public static void main(String[] args) {

        long lasting = System.currentTimeMillis();

        try {
            SAXParserFactory sf = SAXParserFactory.newInstance();
            SAXParser sp = sf.newSAXParser();
            MyXMLReader2SAX reader = new MyXMLReader2SAX();
            sp.parse(String.valueOf(new InputSource("data_10k.xml")), reader);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("运行时间：" + (System.currentTimeMillis() - lasting)
                + "毫秒");
    }

    @Override
    public void characters(char ch[], int start, int length)
            throws SAXException {
        String tag = (String) tags.peek();
    }
}