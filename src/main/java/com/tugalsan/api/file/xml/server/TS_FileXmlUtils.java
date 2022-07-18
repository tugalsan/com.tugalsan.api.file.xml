package com.tugalsan.api.file.xml.server;

import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.unsafe.client.*;

public class TS_FileXmlUtils {

    public static Path toFile(TGS_ListTable source, Path dest, String root, String item) {
        return TGS_UnSafe.compile(() -> {
            List<String> headers = source.getRow(0);
            var size = source.getRowSize();

            var doc = create(dest);

            var rootElement = doc.createElement(root);
            IntStream.range(1, size).forEachOrdered(ri -> {
                var recordI = doc.createElement(item);
                IntStream.range(0, headers.size()).forEachOrdered(ci -> {
                    var columnIHeader = doc.createElement(headers.get(ci));
                    var columnIValue = doc.createTextNode(source.getValueAsString(ri, ci));
                    columnIHeader.appendChild(columnIValue);
                    recordI.appendChild(columnIHeader);
                });
                rootElement.appendChild(recordI);
            });
            doc.appendChild(rootElement);

            var transformerFactory = TransformerFactory.newInstance();
            var transformer = transformerFactory.newTransformer();
            var docSource = new DOMSource(doc);
            var result = new StreamResult(dest.toFile());
            transformer.transform(docSource, result);
            return dest;
        });
    }

    public static Document create(Path dest) {
        return TGS_UnSafe.compile(() -> {
            var dbFactory = DocumentBuilderFactory.newInstance();
            var dBuilder = dbFactory.newDocumentBuilder();
            return dBuilder.newDocument();
        });
    }

    public static Document parse(Path source) {
        return TGS_UnSafe.compile(() -> {
            var dbFactory = DocumentBuilderFactory.newInstance();
            var dBuilder = dbFactory.newDocumentBuilder();
            var doc = dBuilder.parse(source.toFile());
            doc.getDocumentElement().normalize();
            return doc;
        });
    }

    public static TGS_ListTable toTable(Path source, List<String> headers, String item) {
        return TGS_UnSafe.compile(() -> {
            var dest = new TGS_ListTable();
            dest.setRow(0, headers);

            var doc = parse(source);
//            String tagRoot = doc.getDocumentElement().getNodeName();
            var nList = doc.getElementsByTagName(item);
            var size = nList.getLength();
            IntStream.range(0, size).forEachOrdered(ri -> {
                var recordNode = nList.item(ri);
//                String tagRecord = nNode.getNodeName();
                if (recordNode.getNodeType() == Node.ELEMENT_NODE) {
                    IntStream.range(0, headers.size()).forEachOrdered(ci -> {
                        var cellValue = ((Element) recordNode).getElementsByTagName(headers.get(ci)).item(0).getTextContent();
                        dest.setValue(ri + 1, ci, cellValue);
                    });
                }
            });
            return dest;
        });
    }
}
