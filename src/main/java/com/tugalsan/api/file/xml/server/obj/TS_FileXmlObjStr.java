package com.tugalsan.api.file.xml.server.obj;

public class TS_FileXmlObjStr extends TS_FileXmlObj {

    public TS_FileXmlObjStr(String name, String value) {
        super(name);
        this.value = value;
    }
    public String value;

    public static TS_FileXmlObjStr of(String name, String value) {
        return new TS_FileXmlObjStr(name, value);
    }
}
