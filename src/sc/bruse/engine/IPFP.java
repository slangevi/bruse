package sc.bruse.engine;

import sc.bruse.api.*;

import java.util.*;

public class IPFP {
	private static double IPFP_DELTA = 0.00001;	// convergence stopping condition for IPFP
	
	public static BruseTable absorb(BruseTable table, ArrayList<BruseTable> evidence) throws BruseAPIException {
		String varName = null;
		double curEntropy = 0, prevEntropy = 0, diff = 0;
		int i = 0, count = 0, k = evidence.size();
		BruseTable curTable = null, prevTable = table, softFinding = null;
		BookKeepingMgr.IPFPTableSize = prevTable.getTableValues().length;

		// if there is no soft evidence then return original table
		if (k == 0) return table;
		
		// Repeat until converged
		// TODO needs to check for non-convergence and raise error
		while (true) {
			BookKeepingMgr.NumIPFPIterations++;
			softFinding = evidence.get(i);
			varName = softFinding.getVariables()[0].getName();
			
			// TODO should split this up for better readability
			BruseTable marg = prevTable.getMarginal(varName);
			curTable = prevTable.multiplyBy(softFinding).div(marg);
			
			// check if IPFP has converged by checking if the prev table 
			// and the cur table are within IPFP_DELTA of each other
		
			if (converged(curTable, evidence)) return curTable;
			
			/* TEMP REMOVED
			
			curEntropy = getCrossEntropy(curTable, prevTable);
			
			// If entropy measure has not changed from previous iteration then we have converged
			if (curEntropy == prevEntropy) return curTable;
			
			//TODO instead of comparing to prevEntropy, just compare against IPFP_DELTA
			//TODO when curEntropy <= IPFP_DELTA then stop
			
			diff = Math.abs(curEntropy - prevEntropy);
			
			// If entropy measure is within IPFP_DELTA threshold then we have converged
			if (diff <= IPFP_DELTA) return curTable;
			
			*/
			
			// update previous table
			prevTable = curTable;
			
			// update previous entropy measurement
			prevEntropy = curEntropy;
			
			// Move to next evidence finding
			i = ++count % k;
		}
	}
	
	private static boolean converged(BruseTable table, ArrayList<BruseTable> constraints) {
		BruseTable seTable, curTable;
		double curEntropy;
		
		try {
		
			for (int i=0; i < constraints.size(); i++) {
				seTable = constraints.get(i);
				curTable = table.getMarginal(seTable.getVariables()[0].getName());
			
				curEntropy = IPFP.getCrossEntropy(curTable, seTable);
				
				if (curEntropy > 0.0001) return false;
			}
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public static double getCrossEntropy(BruseTable p, BruseTable q) throws BruseAPIException {
		double result = 0;
		double pvals[] = p.getTableValues(), qvals[] = q.getTableValues();
		
		// Cross entropy (I-divergence) is the measure of the difference between 
		// two probability distributions
		
		if ((p.getVariables().length != p.getVariables().length) || 
			(p.containsVariables(q.getVariableNames()) == false))
			throw new BruseAPIException("P and Q must represent the same random variable.");
		
		for (int i=0; i < pvals.length; i++) {
			if (pvals[i] != 0) {
				result += pvals[i] * Math.log10(pvals[i]/qvals[i]);
			}
		}
		
		return result;
	}
}
