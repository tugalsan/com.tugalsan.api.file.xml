package com.tugalsan.api.file.xml.server.obj;

import com.tugalsan.api.list.client.*;
import java.util.*;

public class TS_FileXmlObjLst extends TS_FileXmlObj {

    public TS_FileXmlObjLst(String name) {
        this(name, TGS_ListUtils.of());
    }

    public TS_FileXmlObjLst(String name, List<TS_FileXmlObj> childeren) {
        super(name);
        this.childeren = childeren;
    }
    public List<TS_FileXmlObj> childeren = TGS_ListUtils.of();

    public static TS_FileXmlObjLst of(String name) {
        return new TS_FileXmlObjLst(name);
    }

    public static TS_FileXmlObjLst of(String name, List<TS_FileXmlObj> childeren) {
        return new TS_FileXmlObjLst(name, childeren);
    }

    public TS_FileXmlObjLst add(TS_FileXmlObj child) {
        childeren.add(child);
        return this;
    }
}
