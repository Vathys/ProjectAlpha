package com.alpha.client.helper;

public class Data {
	
	private String docName;
	private int changeType;
	private int offset;
	private int length;
	private String msg;
	
	public Data(String docName, int changeType, int offset, int length, String msg)
	{
		this.docName = docName;
		this.changeType = changeType;
		this.offset = offset;
		this.length = length;
		this.msg = msg;
	}

	public String getDocName() {
		return docName;
	}

	public int getChangeType() {
		return changeType;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}

	public String getMsg() {
		return msg;
	}

	
}
