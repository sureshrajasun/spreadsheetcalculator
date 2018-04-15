package com.redmart.codechallenge.com.redmart.codechallenge.data;

import com.redmart.codechallenge.constant.rule.Operators;

public class OperatorToken extends Token {

	public OperatorToken(Operators operator) {
		setToken(operator.getOperator());
	}

	public Operators getParsedValue() {
		return Operators.get(getToken());
	}

}
