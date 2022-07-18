package com.tugalsan.api.file.xml.server.obj;

import com.tugalsan.api.file.xml.server.*;
import com.tugalsan.api.stream.client.*;
import java.nio.file.*;
import java.util.stream.*;
import org.w3c.dom.*;

public class TS_FileXmlObjUtils {

    public static TS_FileXmlObj toXmlObj(Path source) {
        var doc = TS_FileXmlUtils.parse(source);
        return toXmlObj(doc);
    }

    private static TS_FileXmlObj toXmlObj(Document doc) {
        return toXmlObj(doc.getDocumentElement());
    }

    private static TS_FileXmlObj toXmlObj(Node node) {
        return toXmlObj((Element) node);
    }

    private static TS_FileXmlObj toXmlObj(Element element) {
        if (!element.hasChildNodes()) {
            var rootName = element.getNodeName();
            var rootValue = element.getTextContent();
            return TS_FileXmlObjStr.of(rootName, rootValue);
        }
        var rootXmlLst = new TS_FileXmlObjLst(element.getNodeName(), TGS_StreamUtils.toList(
                IntStream.range(0, element.getChildNodes().getLength())
                        .mapToObj(i -> toXmlObj(element.getChildNodes().item(i)))
        ));
        return rootXmlLst;
    }
}
