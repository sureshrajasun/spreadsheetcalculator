package com.redmart.codechallenge.com.redmart.codechallenge.data;

import com.redmart.codechallenge.service.TokenFactory;

import java.util.LinkedList;
import java.util.regex.Pattern;

public class Cell {

	private final Pattern splitRegex = Pattern.compile("\\s+");

	private final int row;
	private final int col;
	private final LinkedList<ReferenceToken> references;
	private final LinkedList<Token> tokenList;
	private final String contents;
	private int unresolvedRefs;
	private boolean evaluated;
	private double evaluatedValue;

	public Cell(int row, int col, String contents) throws RuntimeException {
		this.row = row;
		this.col = col;
		this.contents = contents;
		this.unresolvedRefs = 0;
		this.references = new LinkedList<ReferenceToken>();
		this.tokenList = new LinkedList<Token>();
		this.parse();
	}

	public String getContents() {
		return contents;
	}

	public int getUnresolvedRefs() {
		return unresolvedRefs;
	}

	public void setUnresolvedRefs(int unresolvedRefs) {
		this.unresolvedRefs = unresolvedRefs;
	}

	public double getEvaluatedValue() {
		return evaluatedValue;
	}

	public void setEvaluatedValue(double evaluatedValue) {
		this.evaluatedValue = evaluatedValue;
	}

	public boolean isEvaluated() {
		return evaluated;
	}

	public void setEvaluated(boolean evaluated) {
		this.evaluated = evaluated;
	}

	public LinkedList<Token> getTokenList() {
		return tokenList;
	}

	public LinkedList<ReferenceToken> getReferences() {
		return references;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Cell cell = (Cell) o;

		if (col != cell.col) return false;
		if (row != cell.row) return false;
		if (!contents.equals(cell.contents)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return (String.valueOf(row) + String.valueOf(col)).hashCode();
	}

	private void parse() throws RuntimeException {
		String[] strArray = splitRegex.split(contents);
		TokenFactory tokenFactory = new TokenFactory();
		for (String s : strArray) {
			Token tok = tokenFactory.makeToken(s);
			if (tok.getClass().equals(ReferenceToken.class)) {
				references.add(((ReferenceToken) tok));
				unresolvedRefs++;
			}
			tokenList.add(tok);
		}
	}
}
