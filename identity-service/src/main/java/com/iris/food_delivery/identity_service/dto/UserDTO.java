package com.iris.food_delivery.identity_service.dto;

public class UserDTO {

    private String username;
        
    private String role;
    
    private String active;
    
    private String token;
    
    private String password;
    
    
    public UserDTO() {
    	
    }
    
	public UserDTO(String username, String role, String active) {
		super();
		this.username = username;
		this.role = role;
		this.active = active;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    
}
