package com.tugalsan.api.file.xml.server.pom;

import com.tugalsan.api.pack.client.*;
import com.tugalsan.api.tree.client.*;
import java.util.*;

public class TS_FileXmlPomUtils {

    public String header() {
        return """
        <?xml version="1.0" encoding="UTF-8"?>
        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        """;
    }

    public static TGS_Pack2<TS_FileXmlPom, String> of(TGS_TreeAbstract<String, String> xmlObj) {
        if (!Objects.equals(xmlObj.id, "project")) {
            return new TGS_Pack2(null, "root.name not project, it is " + xmlObj.id);
        }
        if (xmlObj instanceof TGS_TreeLeaf<String, String>) {
            return new TGS_Pack2(null, "pom is empty");
        }
        if (!(xmlObj instanceof TGS_TreeBranch<String, String>)) {
            return new TGS_Pack2(null, "pom is not list");
        }

        TGS_Pack2<TS_FileXmlPom, String> pck = TGS_Pack2.of(new TS_FileXmlPom(), null);
        var xmlObjLst = (TGS_TreeBranch<String, String>) xmlObj;
        
        pck.value0.pomId = new TS_FileXmlPomId();
        xmlObjLst.childeren.forEach(child -> {
            if (Objects.equals(child.id, "modelVersion") && child instanceof TGS_TreeLeaf<String, String> childStr) {
                pck.value0.modelVersion = childStr.value;
                return;
            }
            if (Objects.equals(child.id, "groupId") && child instanceof TGS_TreeLeaf<String, String> childStr) {
                pck.value0.pomId.groupId = childStr.value;
                return;
            }
            if (Objects.equals(child.id, "groupId") && child instanceof TGS_TreeLeaf<String, String> childStr) {
                pck.value0.pomId.artifactId = childStr.value;
                return;
            }
            if (Objects.equals(child.id, "version") && child instanceof TGS_TreeLeaf<String, String> childStr) {
                pck.value0.pomId.version = childStr.value;
                return;
            }
            
            System.out.println("Unknown tag name: " + child.id);
        });

        return pck;
    }
}
