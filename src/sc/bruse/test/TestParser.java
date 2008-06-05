package sc.bruse.test;


import org.junit.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import sc.bruse.parser.*;
import java.util.*;

public class TestParser {

	@Before
	public void setUp() throws Exception {
		int MAX = 65000;
		ArrayList<Integer> alist = new ArrayList();
		LinkedList<Integer> list = new LinkedList();
		Integer array[] = new Integer[MAX];
		Hashtable<Integer, Integer> hash = new Hashtable<Integer, Integer>();
		
		long StartTime, EndTime;
		Integer o;
		
		// TIMING TEST
		StartTime = System.currentTimeMillis();
		
		for (int i=0; i < MAX; i++) {
			alist.add(new Integer(i));
		}

		for (int i=0; i < MAX; i++) {
			o = alist.get(i);
		}
		
		EndTime = System.currentTimeMillis();
		System.out.println("ArrayList time: " + (EndTime - StartTime));
		
		System.gc();
		java.lang.Thread.sleep(10);
		
		StartTime = System.currentTimeMillis();
		
		for (int i=0; i < MAX; i++) {
			list.add(new Integer(i));
		}
		
		ListIterator<Integer> it = list.listIterator();
		while (it.hasNext()) {
			o = it.next();
		}
		
		EndTime = System.currentTimeMillis();
		System.out.println("LinkedList time: " + (EndTime - StartTime));
		
		System.gc();
		java.lang.Thread.sleep(10);
		
		StartTime = System.currentTimeMillis();
		
		for (int i=0; i < MAX; i++) {
			array[i] = new Integer(i);
		}
		
		for (int i=0; i < MAX; i++) {
			o = array[i];
		}
		
		EndTime = System.currentTimeMillis();
		System.out.println("Array time: " + (EndTime - StartTime));
		
		System.gc();
		java.lang.Thread.sleep(10);
		
		StartTime = System.currentTimeMillis();
		
		for (int i=0; i < MAX; i++) {
			o = new Integer(i);
			hash.put(o, o);
		}
		
		for (int i=0; i < MAX; i++) {
			o = hash.get(i);
		}
		
		EndTime = System.currentTimeMillis();
		System.out.println("Hash Access time: " + (EndTime - StartTime));
		hash.clear();
		
		System.gc();
		java.lang.Thread.sleep(10);
		
		StartTime = System.currentTimeMillis();
		
		for (int i=0; i < MAX; i++) {
			o = new Integer(i);
			hash.put(o, o);
		}
		
		Iterator<Integer> it2 = hash.values().iterator();
		while (it2.hasNext()) {
			o = it2.next();
		}
		
		EndTime = System.currentTimeMillis();
		System.out.println("Hash Iterator time: " + (EndTime - StartTime));
	}

	@Test
	public void testNETparser() {
		try {						
			BNParser parser = BNParserFactory.create("tests/diabetes.net");
			parser.parse();
			
			Object vars[] = parser.getVariables().toArray();
			assertTrue("Wrong number of variables", vars.length == 12);
			
			/*Variable var = (Variable)vars[0];
			assertTrue(var.getName(), var.getName() =="Fred");
			
			var = (Variable)vars[1];
			assertTrue(var.getName() =="John");*/
			
			for (int i=0; i < vars.length; i++) {
				Variable var = (Variable)vars[i];
				System.out.print(var.getName() + ": ");
				for (int j=0; j < var.getStates().size(); j++) {
					System.out.print(" " + var.getStates().get(j));
				}
				System.out.println();
				for (int k=0; k < var.getParents().size(); k++) {
					System.out.print(var.getParents().get(k).getName() + ", ");
				}
				System.out.println();
				for (int l=0; l < var.getTable().length; l++) {
					System.out.print(var.getTable()[l] + ",");
				}
				System.out.println("\n");
			}
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
}
