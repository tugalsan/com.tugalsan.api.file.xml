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

    public static Path toFile(TGS_ListTable source, Path dest) {
        return TGS_UnSafe.compile(() -> {
            List<String> headers = source.getRow(0);
            var size = source.getRowSize();

            var dbFactory = DocumentBuilderFactory.newInstance();
            var dBuilder = dbFactory.newDocumentBuilder();
            var doc = dBuilder.newDocument();

            var rootElement = doc.createElement("root");
            IntStream.range(1, size).forEachOrdered(ri -> {
                var recordI = doc.createElement("record");
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

    public static TGS_ListTable toTable(Path source, List<String> headers) {
        return TGS_UnSafe.compile(() -> {
            var dest = new TGS_ListTable();
            dest.setRow(0, headers);

            var dbFactory = DocumentBuilderFactory.newInstance();
            var dBuilder = dbFactory.newDocumentBuilder();
            var doc = dBuilder.parse(source.toFile());
            doc.getDocumentElement().normalize();

//            String tagRoot = doc.getDocumentElement().getNodeName();
            var nList = doc.getElementsByTagName("record");
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
