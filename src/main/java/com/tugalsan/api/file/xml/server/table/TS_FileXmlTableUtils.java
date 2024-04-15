package com.tugalsan.api.file.xml.server.table;

import com.tugalsan.api.file.xml.server.TS_FileXmlUtils;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import org.w3c.dom.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;

public class TS_FileXmlTableUtils {

    public static TGS_UnionExcuseVoid toFile(TGS_ListTable source, Path dest, String root, String item) {
        List<String> headers = source.getRow(0);
        var size = source.getRowSize();
        var u_doc = TS_FileXmlUtils.of();
        if (u_doc.isExcuse()){
            return u_doc.toExcuseVoid();
        }
        var doc = u_doc.value();
        var rootElement = doc.createElement(root);
        for (var ri = 0; ri < size; ri++) {
            var recordI = doc.createElement(item);
            for (var ci = 0; ci < headers.size(); ci++) {
                var columnIHeader = doc.createElement(headers.get(ci));
                var u = source.getValueAsString(ri, ci);
                if (u.isExcuse()) {
                    return u.toExcuseVoid();
                }
                var columnIValue = doc.createTextNode(u.value());
                columnIHeader.appendChild(columnIValue);
                recordI.appendChild(columnIHeader);
            }
            rootElement.appendChild(recordI);
        }
        doc.appendChild(rootElement);
        TS_FileXmlUtils.save(doc, dest);
        return TGS_UnionExcuseVoid.ofVoid();
    }

    public static TGS_UnionExcuse<TGS_ListTable> toTable(Path source, List<String> headers, String item) {
        var dest = TGS_ListTable.ofStr();
        dest.setRow(0, headers);

        var u_doc = TS_FileXmlUtils.of(source);
        if (u_doc.isExcuse()){
            return u_doc.toExcuse();
        }
        var doc = u_doc.value();
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
        return TGS_UnionExcuse.of(dest);
    }
}
