package com.tugalsan.api.file.xml.server.table;

import com.tugalsan.api.file.xml.server.TS_FileXmlUtils;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import org.w3c.dom.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.unsafe.client.*;

public class TS_FileXmlTableUtils {

    public static Path toFile(TGS_ListTable source, Path dest, String root, String item) {
        return TGS_UnSafe.call(() -> {
            List<String> headers = source.getRow(0);
            var size = source.getRowSize();

            var doc = TS_FileXmlUtils.of();

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

            TS_FileXmlUtils.save(doc, dest);
            return dest;
        });
    }

    public static TGS_ListTable toTable(Path source, List<String> headers, String item) {
        var dest = TGS_ListTable.ofStr();
        dest.setRow(0, headers);

        var doc = TS_FileXmlUtils.of(source);
        var nList = doc.getElementsByTagName(item);
        var size = nList.getLength();
        IntStream.range(0, size).forEachOrdered(ri -> {
            var recordNode = nList.item(ri);
            if (TS_FileXmlUtils.isNode(recordNode)) {
                IntStream.range(0, headers.size()).forEachOrdered(ci -> {
                    var cellValue = ((Element) recordNode).getElementsByTagName(headers.get(ci)).item(0).getTextContent();
                    dest.setValue(ri + 1, ci, cellValue);
                });
            }
        });
        return dest;
    }
}
