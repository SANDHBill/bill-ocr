package com.sandh.billanalyzer.utility;


import java.util.LinkedList;
import java.util.List;

public abstract class AbstractTraceableOperator implements TraceableOperator {

	private volatile boolean debugMode = false;

	private String originName="";

    public String getFilterName() {
        return filterName;
    }

    String filterName="Not set";
	List<TraceableOperator> operationChain;

	AbstractTraceableOperator(){
		operationChain = new LinkedList<TraceableOperator>();
        operationChain.add(this);
	}
	AbstractTraceableOperator(TraceableOperator parent){
		operationChain = parent.getChain();
        operationChain.add(this);
		setDebugMode(parent.isDebugMode());
		setOriginName(parent.getOriginName());
	}

    ProcessMaterial output;
    public ProcessMaterial getOutput() {
        return output;
    }
	@Override
	public String getOriginName() {
		return originName;
	}

	@Override
	public void setOriginName(String originName) {
		this.originName = originName;
	}

	@Override
	public boolean isDebugMode() {
		return debugMode;
	}
	@Override
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}
	@Override
	public TraceableOperator getLastOperation(){
		return operationChain.get(operationChain.size()-1);
	}

	public List<TraceableOperator> getChain(){
		return operationChain;
	}




}
