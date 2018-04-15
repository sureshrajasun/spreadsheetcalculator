package com.redmart.codechallenge.exception;

public class CircularDependencyException extends RuntimeException {
	public CircularDependencyException() {
		super();
	}

	public CircularDependencyException(String message) {
		super(message);
	}
}
