package com.redmart.codechallenge.constant.rule;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public enum Operators {

	ADD("+"), SUB("-"), MUL("*"), DIV("/"), INC("++"), DEC("--");

	// Reverse-lookup map for getting a Operator from an operator string
	private static final Map<String, Operators> lookup = new HashMap<String, Operators>();

	static {
		for (Operators op : Operators.values())
			lookup.put(op.getOperator(), op);
	}

	private final String operator;

	Operators(String op) {
		operator = op;
	}

	public static Operators get(String op) {
		return lookup.get(op);
	}

	public static boolean isValidOperator(String op) {
		return get(op) != null;
	}

	public String getOperator() {
		return operator;
	}

	public Stack<Double> apply(Stack<Double> RPNStack) throws IllegalArgumentException {
		double op1, op2;
		switch (this) {
			case ADD:
				op1 = RPNStack.pop();
				op2 = RPNStack.pop();
				RPNStack.push(op2 + op1);
				break;
			case SUB:
				op1 = RPNStack.pop();
				op2 = RPNStack.pop();
				RPNStack.push(op2 - op1);
				break;
			case MUL:
				op1 = RPNStack.pop();
				op2 = RPNStack.pop();
				RPNStack.push(op2 * op1);
				break;
			case DIV:
				op1 = RPNStack.pop();
				op2 = RPNStack.pop();
				if (op1 == 0) {
					throw new IllegalArgumentException("Error: Cannot divide by 0");
				}
				RPNStack.push(op2 / op1);
				break;
			case INC:
				op1 = RPNStack.pop();
				RPNStack.push(++op1);
				break;
			case DEC:
				op1 = RPNStack.pop();
				RPNStack.push(--op1);
				break;
		}
		return RPNStack;
	}
}