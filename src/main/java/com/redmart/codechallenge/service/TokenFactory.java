package com.redmart.codechallenge.service;

import com.redmart.codechallenge.com.redmart.codechallenge.data.OperatorToken;
import com.redmart.codechallenge.com.redmart.codechallenge.data.ReferenceToken;
import com.redmart.codechallenge.com.redmart.codechallenge.data.Token;
import com.redmart.codechallenge.com.redmart.codechallenge.data.ValueToken;
import com.redmart.codechallenge.constant.rule.Operators;


public class TokenFactory {

	public Token makeToken(String str){
		if (Operators.isValidOperator(str))
			return new OperatorToken(Operators.get(str));
		else if (str.matches(ReferenceToken.refPatternRegex))
			return new ReferenceToken(str);
		else if (str.matches(ValueToken.valuePatternRegex))
			return new ValueToken(str);
		else
			throw new RuntimeException("Error: Invalid token: " + str);
	}
}
