package sc.bruse.network;

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

/***
 * The API exception class
 * 
 * @author langevin
 */
public class BruseAPIException extends Exception {
	
	/***
	 * BruseAPIException construction
	 *
	 */
	public BruseAPIException() {
		super();
	}
	
	/***
	 * BruseAPIException constructor
	 * @param msg is the error message associated with the exception
	 */
	public BruseAPIException(String msg) {
		super(msg);
	}
}
