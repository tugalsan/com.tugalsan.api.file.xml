package com.tugalsan.api.file.xml.server.obj;

import com.tugalsan.api.file.xml.server.*;
import com.tugalsan.api.stream.client.*;
import com.tugalsan.api.tree.client.*;
import com.tugalsan.api.unsafe.client.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import org.w3c.dom.*;

public class TS_FileXmlTreeUtils {

    public static TGS_TreeAbstract<String, String> toTree(Path source) {
        var doc = TS_FileXmlUtils.parse(source);
        return toTree(doc);
    }

    private static TGS_TreeAbstract<String, String> toTree(Document doc) {
        return toTree(doc.getDocumentElement());
    }

    public static TGS_TreeAbstract<String, String> toTree(Node node) {
        return TS_FileXmlUtils.isBranch(node) ? toBranch(node) : toLeaf(node);
    }

    public static TGS_TreeBranch<String, String> toBranch(Node node) {
        List<TGS_TreeAbstract<String, String>> childeren = TGS_StreamUtils.toLst(
                TS_FileXmlUtils.getChilderenStreamExceptText(node)
                        .map(child -> toTree(child))
        );
        return TGS_TreeBranch.of(node.getNodeName(), childeren);
    }

    public static TGS_TreeLeaf<String, String> toLeaf(Node node) {
        var nm = node.getNodeName();
        var vl = TS_FileXmlUtils.getChilderenStream(node)
                .map(child -> TS_FileXmlUtils.getText(child))
                .collect(Collectors.joining(""));
        System.out.printf("nm-vl: [%s-%s]\n", nm, vl);
        return TGS_TreeLeaf.of(nm, vl);
    }

    public static Document toDocument(TGS_TreeAbstract<String, String> treeRoot) {
        return TGS_UnSafe.compile(() -> {
            var doc = TS_FileXmlUtils.newDocument();
            if (treeRoot instanceof TGS_TreeBranch<String, String> treeBranch) {
                System.out.println("branch detected...");
                var nodeBranch = TS_FileXmlUtils.newNodeBranch(doc, treeBranch.id);
                loadNodeBranchContent(doc, nodeBranch, treeBranch);
                TS_FileXmlUtils.addNode(doc, nodeBranch);
                return doc;
            }
            if (treeRoot instanceof TGS_TreeLeaf<String, String> treeLeaf) {
                System.out.println("leaf detected...");
                var nodeLeaf = TS_FileXmlUtils.newNodeLeaf(doc, treeLeaf.id);
                loadNodeLeafContent(nodeLeaf, treeLeaf);
                TS_FileXmlUtils.addNode(doc, nodeLeaf);
                return doc;
            }
            System.out.println("comment detected...");
            var nodeComment = TS_FileXmlUtils.newNodeComment(doc, treeRoot.id);
            TS_FileXmlUtils.addNode(doc, nodeComment);
            return doc;
        });
    }

    private static void loadNodeLeafContent(Node nodeLeaf, TGS_TreeLeaf<String, String> treeLeaf) {
        nodeLeaf.setTextContent(treeLeaf.value);
    }

    private static void loadNodeBranchContent(Document doc, Node nodeParentBranch, TGS_TreeBranch<String, String> treeParentBranch) {
        treeParentBranch.childeren.forEach(treeChild -> {
            if (treeChild instanceof TGS_TreeBranch<String, String> treeBranch) {
                var nodeBranch = TS_FileXmlUtils.newNodeBranch(doc, treeBranch.id);
                loadNodeBranchContent(doc, nodeBranch, treeBranch);
                TS_FileXmlUtils.addNode(nodeParentBranch, nodeBranch);
                return;
            }
            if (treeChild instanceof TGS_TreeLeaf<String, String> treeLeaf) {
                var nodeLeaf = TS_FileXmlUtils.newNodeLeaf(doc, treeLeaf.id);
                loadNodeLeafContent(nodeLeaf, treeLeaf);
                TS_FileXmlUtils.addNode(nodeParentBranch, nodeLeaf);
                return;
            }
            var nodeComment = TS_FileXmlUtils.newNodeComment(doc, treeChild.id);
            TS_FileXmlUtils.addNode(nodeParentBranch, nodeComment);
        });
    }
}
