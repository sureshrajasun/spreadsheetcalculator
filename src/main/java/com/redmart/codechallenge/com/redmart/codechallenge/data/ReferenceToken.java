package com.redmart.codechallenge.com.redmart.codechallenge.data;

import com.redmart.codechallenge.util.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReferenceToken extends Token {

	public static final String refPatternRegex = "([a-zA-Z]+)(\\d+)";

	private static final Pattern refPattern = Pattern.compile(refPatternRegex);

	private int refRow;
	private int refCol;

	public ReferenceToken(String str) {
		setToken(str);
		Matcher matcher = refPattern.matcher(str);

		if (matcher.matches()) {
			refRow = Utils.getRowIndex(matcher.group(1));
			refCol = Utils.getColIndex(matcher.group(2));
		} else {
			throw new RuntimeException("Error: Unable to parse reference: " + str);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ReferenceToken that = (ReferenceToken) o;

		if (refCol != that.refCol) return false;
		if (refRow != that.refRow) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return (String.valueOf(refRow) + String.valueOf(refCol)).hashCode();
	}

	public int getRefRow() {
		return refRow;
	}

	public int getRefCol() {
		return refCol;
	}
}
