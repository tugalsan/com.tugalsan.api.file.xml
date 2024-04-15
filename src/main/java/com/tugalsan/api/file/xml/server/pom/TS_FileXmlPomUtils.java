package com.tugalsan.api.file.xml.server.pom;

import com.tugalsan.api.tree.client.*;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import java.util.*;

public class TS_FileXmlPomUtils {

    public String header() {
        return """
        <?xml version="1.0" encoding="UTF-8"?>
        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        """;
    }

    public static TGS_UnionExcuse<TS_FileXmlPom> of(TGS_TreeAbstract<String, String> xmlObj) {
        if (!Objects.equals(xmlObj.id, "project")) {
            return TGS_UnionExcuse.ofExcuse(TS_FileXmlPomUtils.class.getSimpleName(), "of", "root.name not project, it is " + xmlObj.id);
        }
        if (xmlObj instanceof TGS_TreeLeaf<String, String>) {
            return TGS_UnionExcuse.ofExcuse(TS_FileXmlPomUtils.class.getSimpleName(), "of", "pom is empty");
        }
        if (!(xmlObj instanceof TGS_TreeBranch<String, String>)) {
            return TGS_UnionExcuse.ofExcuse(TS_FileXmlPomUtils.class.getSimpleName(), "of", "pom is not list");
        }

        var pom = new TS_FileXmlPom();
        var xmlObjLst = (TGS_TreeBranch<String, String>) xmlObj;
        
        pom.pomId = new TS_FileXmlPomId();
        xmlObjLst.childeren.forEach(child -> {
            if (Objects.equals(child.id, "modelVersion") && child instanceof TGS_TreeLeaf<String, String> childStr) {
                pom.modelVersion = childStr.value;
                return;
            }
            if (Objects.equals(child.id, "groupId") && child instanceof TGS_TreeLeaf<String, String> childStr) {
                pom.pomId.groupId = childStr.value;
                return;
            }
            if (Objects.equals(child.id, "groupId") && child instanceof TGS_TreeLeaf<String, String> childStr) {
               pom.pomId.artifactId = childStr.value;
                return;
            }
            if (Objects.equals(child.id, "version") && child instanceof TGS_TreeLeaf<String, String> childStr) {
                pom.pomId.version = childStr.value;
                return;
            }
            
            System.out.println("Unknown tag name: " + child.id);
        });

        return TGS_UnionExcuse.of(pom);
    }
}
