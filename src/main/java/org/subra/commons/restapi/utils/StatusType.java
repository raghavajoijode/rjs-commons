package org.subra.commons.restapi.utils;

public enum StatusType {

	OPEN("Open"), IN_PROGRESS("In Progress"), CLOSED("Closed");

	private StatusType(String value) {
		this.value = value;
	}

	private String value;

	public String value() {
		return value;
	}

}
