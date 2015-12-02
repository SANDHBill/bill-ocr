package com.sandh.billanalyzer.utility;


public abstract class AbstractTraceableOperator implements TraceableOperator {

	private volatile boolean debugMode = false;

	private String parameters="";
	private String originName="";
	protected String lastOperation="";

	@Override
	public String getOriginName() {
		return originName;
	}

	@Override
	public void setOriginName(String originName) {
		this.originName = originName;
	}

	@Override
	public String getParameters() {
		return parameters;
	}

	@Override
	public void setParameters(String parameters) {
		this.parameters = parameters;
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
	public String getOperation(){
		return lastOperation;
	}




}
