package sc.bruse.engine;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import sc.bruse.network.BruseNode;
import sc.bruse.network.BruseTable;

public class VariableElimination {

	public static ArrayList<BruseTable> query(ArrayList<BruseTable> potentials, ArrayList<BruseNode> target) {
		BruseNode var = null;
		BruseTable pot = null, marg = null;
		ArrayList<BruseTable> result = new ArrayList<BruseTable>(), workingset = null;;
	
		//DEBUG:
		/*System.out.print("Target: ");
		for (int i=0; i < target.size(); i++) {
			System.out.print(target.get(i).getName() + ",");
		}
		System.out.println();
		System.out.print("Potentials: ");
		for (int i=0; i < potentials.size(); i++) {
			System.out.print(potentials.get(i).getVariableNames());
		}
		System.out.println();
		*/
		// initially consider all potentials
		result.addAll(potentials);
		
		// create an elimination order for the variables
		HashSet<BruseNode> elimVars = createEliminationOrder(potentials, target);
			
		Iterator<BruseNode> it = elimVars.iterator();
		while (it.hasNext()) {
			var = it.next();
			//DEBUG:
			//System.out.println("Eliminating: " + var.getName());
			
			marg = null;
			workingset = new ArrayList<BruseTable>();
			for (int i=0; i < result.size(); i++) {
				pot = result.get(i);
				if (pot.containsVariable(var.getName())) {
					//DEBUG:
					//System.out.println("Needs Margalizing: " + pot.getVariableNames());
					if (marg == null) {
						marg = pot;
					}
					else {
//						long StartTime = System.currentTimeMillis();
						marg = marg.multiplyBy(pot);
//						long EndTime = System.currentTimeMillis();
//						BookKeepingMgr.TMP += (EndTime - StartTime);
					}
				}
				else {
					//DEBUG:
					//System.out.println("No marg needed for: " + pot.getVariableNames());
					workingset.add(pot);
				}
			}
			if (marg != null) {
				//DEBUG:
				//System.out.println("Margalizing: " + marg.getVariableNames());
				ArrayList domain = marg.getVariableNames();
				domain.remove(var.getName());
				workingset.add(marg.getMarginal(domain));
			}
			result = workingset;
			//DEBUG:
			/*System.out.print("Potentials: ");
			for (int i=0; i < result.size(); i++) {
				System.out.print(result.get(i).getVariableNames());
			}
			System.out.println("\n-----------");*/
		}
		
		return result;
	}

	private static void minDeficiencySearch(ArrayList<BruseNode> nodes) {
		// TODO
	}
	
	private static HashSet<BruseNode> createEliminationOrder(ArrayList<BruseTable> potentials, ArrayList<BruseNode> target) {
		BruseTable pot = null;
		HashSet<BruseNode> elimVars = new HashSet<BruseNode>();
		
		for (int i=0; i < potentials.size(); i++) {
			pot = potentials.get(i);
			for (int j=0; j < pot.getVariables().length; j++) {
				if (target.contains(pot.getVariables()[j]) == false) {
					elimVars.add(pot.getVariables()[j]);
				}
			}
		}
		
		return elimVars;
	}
}
