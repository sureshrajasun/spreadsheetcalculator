package com.redmart.codechallenge.com.redmart.codechallenge.data;

public abstract class Token {

	private String token = null;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "Token{" +
				"token='" + token + '\'' +
				'}';
	}
}