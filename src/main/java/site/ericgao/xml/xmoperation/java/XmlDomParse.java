package site.ericgao.xml.xmoperation.java;

import jdk.management.resource.internal.inst.FileOutputStreamRMHooks;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.util.Formatter;
import java.util.Objects;

/**
 * @Project advanced
 * @Description //TODO
 * @Author EricGao
 * @Date 2021/2/9 11:03
 * @Version 1.0
 **/
public class XmlDomParse {

    /**
     * 格式化输出
     */
    private static final Formatter FORMATTER = new Formatter(System.out);

    public static void main(String[] args) {
        String filename = "table.pdm";
        String nodeName = "o:Table";
        XmlDomParse xmlDomParse = new XmlDomParse();
        Document document = xmlDomParse.domParse(filename);
        xmlDomParse.analyzeNode(document,nodeName);
        FORMATTER.close();
    }

    /**
     * 节点解析
     * @param document 接收Node类型的实例对象 多态！！！
     * @param nodeName 要解析的节点名
     */
    private void analyzeNode(Document document, String nodeName) {
        boolean visitContinueFlag = true;

        //获取 所有 标签名 为 nodeName 的 所有元素
        NodeList elements = document.getElementsByTagName(nodeName);
        //判断是否是元素节点，如果是元素节点就直接输出
        if (elements.getLength() == 0){
            System.err.format("不存在 标签名为{%s}的元素：", nodeName);
        }else{
            //遍历子节点集合
            for (int i = 0; i < elements.getLength() && visitContinueFlag; i++) {
                //获取其中的一个子节点
                Node child = elements.item(i);

                // 过滤掉 pdm文件 中不符合要求的node
                if(child.getAttributes() == null || child.getAttributes().getNamedItem("Id") == null) {
                    continue;
                }
                visitContinueFlag = false;
                //判断该子节点是否为元素节点，如果是元素节点就输出，不是元素节点就再获取到它的子节点集合-->递归
                list(child);
            }

        }
    }

    /**
     * 遍历 所有节点
     * 注意 若 node.getChildNodes().getLength() == 0
     * 表示 该node是文本内容（<a:Name>主键</a:Name>中的  “主键”），或为 单开闭节点（<a:TotalSavingCurrency/>），如以下为打印输出：
     * node.getNodeName() = #text
     * node.getNodeValue() = 主键
     * node.getNodeName() = a:TotalSavingCurrency
     * node.getNodeValue() = null
     *
     * 若 node.getChildNodes().getLength() == 1 表示 该node为非父节点，其只含值，嵌套子节点，
     * 通过 node.getFirstChild().getNodeValue() 或 node.getTextContent()获取其 值内容
     * @param node
     */
    private void list(Node node) {
        //判断是否是元素节点，如果是元素节点就直接输出
        if (node.getChildNodes().getLength() == 1){
            if ("a:ObjectID".equals(node.getNodeName())){
                System.out.println("------------------------ ****** -------------------------");
            }
            if (node.getParentNode().getParentNode().getParentNode() != null){
                FORMATTER.format("[%s --> %s --> %s]:[%s --> %s] \n",
                        node.getParentNode().getParentNode().getParentNode().getNodeName(),
                        node.getParentNode().getParentNode().getNodeName(), node.getParentNode().getNodeName(),
                        node.getNodeName(), node.getFirstChild().getNodeValue());
            } else if (node.getParentNode().getParentNode() != null){
                FORMATTER.format("[%s --> %s]:[%s --> %s] \n", node.getParentNode().getParentNode().getNodeName(),
                        node.getParentNode().getNodeName(), node.getNodeName(), node.getFirstChild().getNodeValue());
            }else if (node.getParentNode() != null){
                FORMATTER.format("[%s]:[%s --> %s] \n", node.getParentNode().getNodeName(), node.getNodeName(),
                        node.getFirstChild().getNodeValue());
            }
        }
        //如果没有进入if语句，下面就不是元素节点，故获取到子节点集合
        NodeList childNodes = node.getChildNodes();
        //遍历子节点集合
        for (int i = 0; i < childNodes.getLength(); i++) {
            //获取其中的一个子节点
            Node child = childNodes.item(i);
            //判断该子节点是否为元素节点，如果是元素节点就输出，不是元素节点就再获取到它的子节点集合-->递归
            list(child);
        }
    }

    /**
     * 解析 xml 结构文件
     * @param filename 文件全路径名
     * @return 解析后 生成对应的 Document 对象
     */
    private Document domParse(String filename) {
        Document document = null;
        try {
            //API规范：需要用一个工厂来造解析器对象，先造一个工厂
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            //获取解析器对象
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            //获取到解析XML文档的流对象
            InputStream inputStream = XmlDomParse.class.getClassLoader().getResourceAsStream(filename);
            //解析XML文档，得到了代表XML文档的Document对象！
            document = documentBuilder.parse(inputStream);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * 增加 新 元素
     * @param document xml 文档实例
     * @param parentTagName 父节点名
     * @param tagName 元素名
     * @param content 元素内容
     * @param filename document 对应的文件名
     */
    private void add(Document document, String parentTagName,String tagName, String content,String filename){
        //创建需要增加的节点
        try {
            Element element = document.createElement(tagName);
            //向节点添加文本内容
            element.setTextContent(content);
            //得到需要添加节点的父节点
            Node parentNode = document.getElementsByTagName(parentTagName).item(0);
            //把需要增加的节点挂在父节点下面去
            parentNode.appendChild(element);

            //将名为 tagName 的节点插入到 "o:Code" 节点之前！(不和 parentNode.appendChild(element); 同时生效)
            parentNode.insertBefore(element, document.getElementsByTagName("o:Code").item(0));

            //获取创建转换器的工厂
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            //获取转换器对象
            Transformer transformer = transformerFactory.newTransformer();
            //把内存中的Dom树更新到硬盘中
            transformer.transform(new DOMSource(document),new StreamResult(filename));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除节点
     * @param document
     * @param tagName 要删除的节点名称
     * @param filename document 对应的文件名
     */
    private void delete(Document document, String tagName, String filename){
        try {
            //获取到beijing这个节点
            Node node = document.getElementsByTagName(tagName).item(0);
            //获取到父节点，然后通过父节点把自己删除了
            node.getParentNode().removeChild(node);
            //把内存中的Dom树更新到硬盘文件中
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(filename));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改 元素 的值
     * @param document
     * @param tagName 元素名
     * @param filename document 对应的 xml 结构文档名
     */
    private void update(Document document, String tagName, String newContent, String filename){
        try {
            //获取到 tagName 节点
            Node node = document.getElementsByTagName(tagName).item(0);
            //修改内容
            node.setTextContent(newContent);
            //将内存中的Dom树更新到硬盘文件中
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(filename));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }


    private static void updateAttribute(Document document, String tagName,
                                        String[] attributes, String[] attributeValues, String filename){
        try {
            //获取到 tagName 节点
            Node node = document.getElementsByTagName(tagName).item(0);
            //现在node节点没有增加属性的方法，所以我就要找它的子类---Element
            Element element = (Element) node;
            //设置一个属性，如果存在则修改，不存在则创建！
            for (int i = 0; i < attributes.length; i++) {
                //如果要删除属性就用removeAttribute()方法
                element.setAttribute(attributes[i], attributeValues[i]);
            }
            //将内存中的Dom树更新到硬盘文件中
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(filename));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
