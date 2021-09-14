package org.subra.commons.dtos;

import java.io.Serializable;

public class EmailPayLoad implements Serializable {

	private static final long serialVersionUID = 1615385120899678724L;
	private String toAddress;
	private String subject;
	private String body;

	public EmailPayLoad() {
		super();
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
