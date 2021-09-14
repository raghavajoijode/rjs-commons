package org.subra.commons.dtos;

import java.io.Serializable;

import org.subra.commons.restapi.utils.ResponseMessage;
import org.subra.commons.restapi.utils.ResponseStatus;

public class Response implements Serializable {
	private static final long serialVersionUID = 1L;
	private ResponseStatus status;
	private ResponseMessage message;
	private Object body;

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public ResponseMessage getMessage() {
		return message;
	}

	public void setMessage(ResponseMessage message) {
		this.message = message;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Response(ResponseStatus status, ResponseMessage message, Object body) {
		super();
		this.status = status;
		this.message = message;
		this.body = body;
	}

}
