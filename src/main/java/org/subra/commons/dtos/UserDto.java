package org.subra.commons.dtos;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UserDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3685049766587045457L;
	private String email;
	private String name;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

}
