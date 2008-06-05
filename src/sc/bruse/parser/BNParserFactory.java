package sc.bruse.parser;

import sc.bruse.api.*;

public class BNParserFactory {

	public static BNParser create(String filename) throws BruseAPIException {
		BNParser parser = null;
		
		if (filename.endsWith("net")) {
			parser = new NETParser(filename);
		}
		else {
			System.err.print("Bayesian Network file format not supported.");
			throw new BruseAPIException("Invalid file format");
		}
		
		return parser;
	}
}
