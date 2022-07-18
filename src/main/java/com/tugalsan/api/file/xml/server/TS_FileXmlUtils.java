package com.tugalsan.api.file.xml.server;

import com.tugalsan.api.unsafe.client.*;
import java.nio.file.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class TS_FileXmlUtils {

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

    public static boolean isValue(Node node) {
        return node.getNodeType() == Node.ELEMENT_NODE;
    }
}
