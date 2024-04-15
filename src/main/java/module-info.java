module com.tugalsan.api.file.xml {
    requires java.xml;
    requires com.tugalsan.api.list;
    requires com.tugalsan.api.union;
    requires com.tugalsan.api.bytes;
    requires com.tugalsan.api.stream;
    requires com.tugalsan.api.tree;
    requires com.tugalsan.api.string;
    exports com.tugalsan.api.file.xml.server.obj;
    exports com.tugalsan.api.file.xml.server.table;
    exports com.tugalsan.api.file.xml.server;
}
