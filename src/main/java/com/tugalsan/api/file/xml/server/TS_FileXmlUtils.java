package com.tugalsan.api.file.xml.server;

import com.tugalsan.api.bytes.client.*;
import com.tugalsan.api.stream.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.unsafe.client.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

public class TS_FileXmlUtils {

    public static DocumentBuilder createBuilder() {
        return TGS_UnSafe.compile(() -> {
            var factory = DocumentBuilderFactory.newInstance();
            return factory.newDocumentBuilder();
        });
    }

    public static Document newDocument() {
        return createBuilder().newDocument();
    }

    public static Node getNodeRoot(Document doc) {
        return doc.getDocumentElement();
    }

    public static NamedNodeMap getAttributes(Node node) {
        return node.getAttributes();
    }

    public static Document parse(String content) {
        return TGS_UnSafe.compile(() -> {
            var input = TGS_ByteArrayUtils.toByteArray(content);
            try ( var bis = new ByteArrayInputStream(input)) {
                var doc = createBuilder().parse(bis);
                doc.getDocumentElement().normalize();
                return doc;
            }
        });
    }

    public static Document parse(Path source) {
        return TGS_UnSafe.compile(() -> {
            var input = source.toFile();
            var doc = createBuilder().parse(input);
            doc.getDocumentElement().normalize();
            return doc;
        });
    }

    public static boolean isNode(Node node) {
        return node.getNodeType() == Node.ELEMENT_NODE;
    }

    public static int childCount(Node node) {
        return node.getChildNodes().getLength();
    }

    public static Node childGet(Node node, int i) {
        return node.getChildNodes().item(i);
    }

    public static Stream<Node> getChilderenStreamExceptText(Node node) {
        return getChilderenStream(node)
                .filter(child -> !isText(child));
    }

    public static Stream<Node> getChilderenStream(Node node) {
        return IntStream.range(0, childCount(node))
                .mapToObj(i -> childGet(node, i));
    }

    public static List<Node> getChilderenLst(Node node) {
        return TGS_StreamUtils.toList(getChilderenStream(node));
    }

    public static List<Node> getChilderenLstExceptText(Node node) {
        return TGS_StreamUtils.toList(getChilderenStreamExceptText(node));
    }

    public static boolean isBranch(Node node) {
        return getChilderenStream(node)
                .map(child -> node.getNodeName())
                .filter(id -> !Objects.equals(id, "#text"))
                .findAny().isPresent();
    }

    public static boolean isLeaf(Node node) {
        return isBranch(node) ? false : !isText(node);
    }

    public static boolean isText(Node node) {
        var isText = node instanceof Text;
        if (isText) {
            System.out.println("node is text: " + node.getNodeName());
        }
        return isText;
//        return isText(node.getNodeName());
    }

    public static boolean isText(CharSequence nodeName) {
        return Objects.equals(nodeName, "#text");
    }

    public static String getText(Node node) {
        if (node.hasChildNodes()) {
            return TS_FileXmlUtils.getChilderenStream(node)
                    .map(child -> /*isText(node) ? node.getTextContent() :*/ getText(child))
                    .collect(Collectors.joining(""));
        }
        return TGS_StringUtils.toNullIfEmpty(node.getTextContent());
    }

    public static Node newNodeBranch(Document doc, String nodeName) {
        return doc.createElement(nodeName);
    }

    public static Node newNodeLeaf(Document doc, String nodeName) {
        return doc.createTextNode(nodeName);
    }

    public static Node newNodeComment(Document doc, String nodeName) {
        return doc.createComment(nodeName);
    }

    public static void addNode(Node parent, Node child) {
        parent.appendChild(child);
    }

    public static void addNode(Document doc, Node child) {
        doc.appendChild(child);
    }

    public static void toFile(Document doc, Path dest) {
        TGS_UnSafe.execute(() -> {
            var transformerFactory = TransformerFactory.newInstance();
            var transformer = transformerFactory.newTransformer();
            var docSource = new DOMSource(doc);
            var result = new StreamResult(dest.toFile());
            transformer.transform(docSource, result);
        });
    }
}
