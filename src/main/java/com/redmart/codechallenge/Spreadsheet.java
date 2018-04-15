package com.redmart.codechallenge;

import com.redmart.codechallenge.exception.CircularDependencyException;
import com.redmart.codechallenge.service.WorkBook;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Logger;


public class Spreadsheet {

	private static final Logger LOGGER = Logger.getLogger(Spreadsheet.class.getName());
	private static final int STATUS_CODE_FAILURE = 1;
	private static final String PRETTY_PRINT_ARG1 = "--pretty";
	private static final String PRETTY_PRINT_ARG2 = "-p";

	public static final String NOT_EVALUATED = "Not Evaluated";
	public static final String CYCLIC_DEPENDENCY = " (Circular Dependency)";

	private final WorkBook workBook;
	private boolean prettyPrint = false;
	private Scanner inputScanner;

	public Spreadsheet() {
		workBook = new WorkBook();
		inputScanner = new Scanner(System.in);
	}

	public static void main(String[] args) {
		Spreadsheet spreadsheet = new Spreadsheet();
		if (args.length > 0) {
			try {
				if (!spreadsheet.simpleCommandLineParser(args)) {
					LOGGER.severe("Error: Invalid number of arguments");
					printUsage();
					System.exit(STATUS_CODE_FAILURE);
				}
			} catch (FileNotFoundException e) {
				LOGGER.severe("Caught FileNotFoundException: Unable to find the specified input file");
				System.exit(STATUS_CODE_FAILURE);
			}
		}
		spreadsheet.processWorkbook();
		spreadsheet.evaluate();

		if (spreadsheet.isPrettyPrint())
			spreadsheet.prettyPrintResults();
		else
			System.out.print(spreadsheet.getResults());

		// if the workbook is cyclic return with non-zero exit code
		if (spreadsheet.isWorkBookCircular()) {
			System.exit(STATUS_CODE_FAILURE);
		}
	}

	public boolean isWorkBookCircular() {
		return workBook.isCircularDependent();
	}

	private static void printUsage() {
		System.out.println("Usage: SpreadsheetSolver [--prettyPrint, -p] [filepath]");
		System.out.println("SpreadsheetSolver can take 2 command line argument");
		System.out.println("[--prettyPrint, -p]: Print the output in a pretty format");
		System.out.println("[filepath]: path of file from where the input has to be read. If no filepath is provided" +
				" SpreadsheetSolver expects to read inputs from stdin");
	}

	public void setInputScanner(Scanner inputScanner) {
		this.inputScanner = inputScanner;
	}

	public boolean isPrettyPrint() {
		return prettyPrint;
	}

	public void processWorkbook() {
		try {
			workBook.readInput(inputScanner);
		} catch (RuntimeException re) {
			LOGGER.severe(re.getMessage());
			System.exit(STATUS_CODE_FAILURE);
		} finally {
			inputScanner.close();
		}
	}

	private boolean simpleCommandLineParser(String[] args) throws FileNotFoundException {
		int argsCount = args.length;
		for (String arg : args) {
			if (arg.trim().equalsIgnoreCase(PRETTY_PRINT_ARG1) || arg.trim().equalsIgnoreCase
					(PRETTY_PRINT_ARG2)) {
				prettyPrint = true;
				argsCount--;
			} else if (new File(arg).exists()) {
				setInputScanner(new Scanner(new File(arg)));
				argsCount--;
			}
		}
		return argsCount == 0;
	}

	public void evaluate() {
		try {
			workBook.evaluate();
		} catch (IllegalArgumentException iae) {
			LOGGER.severe(iae.getMessage());
			System.exit(STATUS_CODE_FAILURE);
		} catch (CircularDependencyException cde) {
			LOGGER.severe(cde.getMessage());
		} catch (RuntimeException re) {
			LOGGER.severe(re.getMessage());
			System.exit(STATUS_CODE_FAILURE);
		}
	}

	public String getResults() {
		return workBook.printWorkbook(true);
	}

	private void prettyPrintResults() {
		workBook.prettyPrintWorkbook(false);
		workBook.prettyPrintWorkbook(true);
	}
}
