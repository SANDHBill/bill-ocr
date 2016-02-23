package com.sandh.billanalyzer.utility;

/**
 * Created by hamed on 23/02/2016.
 */
public class TraceableProccessedObject{
    private AuditItem auditITem;


    private Object proccessedObject=null;
    private final TraceableProccessedObject parent;
    private TraceableProccessedObject child;

    public TraceableProccessedObject(Object pproccessedObject){
        this(null,pproccessedObject,null);
    }

    public TraceableProccessedObject(Object pproccessedObject,AuditItem audit){
        this(null,pproccessedObject,audit);
    }

    public TraceableProccessedObject(TraceableProccessedObject pparent,Object pproccessedObject,AuditItem audit){
        parent=pparent;
        if(pparent!=null){
            pparent.child=this;
        }
        proccessedObject=pproccessedObject;
        auditITem=audit;
    }

    public Object getProccessedObject() {
        return proccessedObject;
    }

}
