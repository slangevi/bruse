package sc.bruse.parser;

/***********************************
 * Copyright 2008 Scott Langevin
 * 
 * All Rights Reserved.
 *
 * This file is part of BRUSE.
 *
 * BRUSE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BRUSE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BRUSE.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Scott Langevin (scott.langevin@gmail.com)
 *
 */

import java.io.BufferedReader;

import sc.bruse.network.*;

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
	
	public static BNParser create(BufferedReader reader) throws BruseAPIException {
		BNParser parser = null;
		
		// Quick Hack:  Assumes reader is a net file - should check somehow
		parser = new NETParser(reader);
		
		return parser;
	}
}
