package com.redmart.codechallenge.service;

import com.redmart.codechallenge.Spreadsheet;
import com.redmart.codechallenge.com.redmart.codechallenge.data.Cell;
import com.redmart.codechallenge.com.redmart.codechallenge.data.OperatorToken;
import com.redmart.codechallenge.com.redmart.codechallenge.data.ReferenceToken;
import com.redmart.codechallenge.com.redmart.codechallenge.data.Token;
import com.redmart.codechallenge.com.redmart.codechallenge.data.ValueToken;
import com.redmart.codechallenge.exception.CircularDependencyException;
import com.redmart.codechallenge.util.PrettyPrinter;
import com.redmart.codechallenge.util.Utils;

import java.util.*;

public class WorkBook {

	private final LinkedList<Cell> topologicalList;
	private final HashMap<Integer, HashSet<Cell>> dependenciesMap;
	private int n; // number of columns (width)
	private int m; // number of rows (height)
	private Cell[][] cellMatrix;
	private int unsolvedCells;
	boolean circularDependent;

	public boolean isCircularDependent() {
		return circularDependent;
	}

	public void setCircularDependent(boolean circularDependent) {
		this.circularDependent = circularDependent;
	}

	public WorkBook() {
		topologicalList = new LinkedList<Cell>();
		dependenciesMap = new HashMap<Integer, HashSet<Cell>>();
		setCircularDependent(false);
	}

	public void readInput(Scanner inputScanner) throws RuntimeException {

		n = inputScanner.nextInt();
		m = inputScanner.nextInt();
		unsolvedCells = n * m;
		inputScanner.nextLine();    // omit the newline

		cellMatrix = new Cell[m][n];

		for (int row = 0; row < m; row++) {
			for (int col = 0; col < n; col++) {
				String data = inputScanner.nextLine().trim().toUpperCase();
				Cell curCell = cellMatrix[row][col] = new Cell(row, col, data);
				if (curCell.getReferences().size() > 0) {
					addToDependenciesMap(curCell);
				} else {
					topologicalList.add(curCell);
				}
			}
		}
	}

	private void addToDependenciesMap(Cell curCell) {
		LinkedList<ReferenceToken> curCellDeps = curCell.getReferences();
		for (ReferenceToken tok : curCellDeps) {
			HashSet<Cell> refBy;
			if (dependenciesMap.containsKey(tok.hashCode())) {
				refBy = dependenciesMap.get(tok.hashCode());
			} else {
				dependenciesMap.put(tok.hashCode(), refBy = new HashSet<Cell>());
			}
			refBy.add(curCell);
		}
	}

	/**
	 * Function to evaluate the workbook cell by cell in topological order
	 *
	 * @throws CircularDependencyException : if workbook has cell which are circularly dependent on each other
	 * @throws RuntimeException:            if parsing of token during evaluation fails
	 */
	public void evaluate() {
		while (topologicalList.size() > 0) {
			Cell curCell = topologicalList.removeFirst();
			evaluate(curCell);
			unsolvedCells--;
			resolveDependencies(curCell);
		}
		if (unsolvedCells != 0) {
			setCircularDependent(true);
			throw new CircularDependencyException("CircularDependencyFound: Unable to solve the workbook");
		}
	}

	private void resolveDependencies(Cell curCell) {
		// get all the cells dependent on this cell
		if (dependenciesMap.containsKey(curCell.hashCode())) {
			HashSet<Cell> curCellDeps = dependenciesMap.get(curCell.hashCode());
			if (curCellDeps.size() > 0) { // if there are cells dependent on this one
				for (Cell depCell : curCellDeps) {
					depCell.setUnresolvedRefs(depCell.getUnresolvedRefs() - 1);
					if (depCell.getUnresolvedRefs() == 0) // if all references resolved then add to topological list
						topologicalList.add(depCell);
				}
			}
		}
	}

	/**
	 * Function to evaluate a cell
	 *
	 * @param curCell the cell to be evaluated
	 * @return the evaluated value of the cell
	 * @throws RuntimeException:         if invalid token is found while evaluating
	 * @throws IllegalArgumentException: If the evaluation of operator is done on illegal argument like divide by 0
	 */
	private double evaluate(Cell curCell) throws RuntimeException {
		if (curCell.isEvaluated())
			return curCell.getEvaluatedValue();

		Stack<Double> RPNStack = new Stack<Double>();
		LinkedList<Token> curCellTokens = curCell.getTokenList();

		for (Token tok : curCellTokens) {
			if (tok.getClass().equals(ValueToken.class)) {
				RPNStack.push(((ValueToken) tok).getParsedValue());
			} else if (tok.getClass().equals(ReferenceToken.class)) {
				ReferenceToken refTok = (ReferenceToken) tok;
				Cell refCell = cellMatrix[refTok.getRefRow()][refTok.getRefCol()];
				RPNStack.push(evaluate(refCell));
			} else if (tok.getClass().equals(OperatorToken.class)) {
				OperatorToken opTok = (OperatorToken) tok;
				RPNStack = opTok.getParsedValue().apply(RPNStack);
			} else {
				throw new RuntimeException("Error: Invalid token: " + tok.toString());
			}
		}
		curCell.setEvaluatedValue(RPNStack.pop());
		curCell.setEvaluated(true);
		return curCell.getEvaluatedValue();
	}

	public String printWorkbook(boolean results) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (results) {
					if (cellMatrix[i][j].isEvaluated()) {
						String value = String.format("%.5f", cellMatrix[i][j].getEvaluatedValue());
						stringBuilder.append(value);
					} else {
						stringBuilder.append(Spreadsheet.NOT_EVALUATED);
						if (dependenciesMap.containsKey(cellMatrix[i][j].hashCode())) {
							stringBuilder.append(Spreadsheet.CYCLIC_DEPENDENCY);
						}
					}
				} else {
					stringBuilder.append(cellMatrix[i][j].getContents());
				}
				if (!(i == m - 1 && j == n - 1))
					stringBuilder.append(System.lineSeparator());
			}
		}
		return stringBuilder.toString();
	}

	public void prettyPrintWorkbook(boolean results) {
		String[][] matrix = new String[m + 1][n + 1];
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				if (i == 0 || j == 0) {
					if (i == 0 && j == 0) {
						matrix[i][j] = " ";
					} else if (i == 0) {
						matrix[i][j] = String.valueOf(j);
					} else {
						matrix[i][j] = Utils.getRowName(i - 1);
					}
				} else {
					if (results) {
						if (cellMatrix[i - 1][j - 1].isEvaluated()) {
							matrix[i][j] = String.format("%.5f", cellMatrix[i - 1][j - 1].getEvaluatedValue());
						} else {
							matrix[i][j] = Spreadsheet.NOT_EVALUATED;
							if (dependenciesMap.containsKey(cellMatrix[i - 1][j - 1].hashCode())) {
								matrix[i][j] = matrix[i][j] + Spreadsheet.CYCLIC_DEPENDENCY;
							}
						}
					} else {
						matrix[i][j] = cellMatrix[i - 1][j - 1].getContents();
					}
				}
			}
		}
		if (results) {
			System.out.println("Results:");
		} else {
			System.out.println("Inputs:");
		}
		final PrettyPrinter printer = new PrettyPrinter(System.out);
		printer.print(matrix);
	}
}
