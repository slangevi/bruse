package sc.bruse.test;

import sc.bruse.network.*;
import sc.bruse.engine.*;

public class PerformanceImprovementTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test2();
		//test3();
		//test4();
	}
	
	private static void test2() {
		String file = "tests/test61.net";
		
		try {
			BruseNetwork network = BruseNetworkFactory.create(file);
			
			/*BruseTable a = network.getNode("John").getTable();
			BruseTable b = network.getNode("Irene").getTable();
			
			StateIterator it = new StateIterator(b.getVariables());
			StateIterator.dumpStates(it);
			
			BruseTable c = a.multiplyBy(b);
			*/
			BruseTable t1 = network.getNode("n1").getTable();
			
			/*BruseTable t2 = network.getNode("n1").getTable();
			
			for (int i=1; i < 11; i++) {
				t2 = t2.multiplyBy(network.getNode("n"+i).getTable());
			}
			
			long StartTime = System.currentTimeMillis();
			
			for (int i=1; i < 11; i++) {
				t2 = t2.absorb(network.getNode("n"+i).getTable());
			}
			
			long EndTime = System.currentTimeMillis();
			System.out.println((EndTime-StartTime));
			System.out.println("Table size: " + t2.getTableValues().length);
			*/
			
			long StartTime = System.currentTimeMillis();
			
			for (int i=1; i < 11; i++) {
				t1 = t1.multiplyBy(network.getNode("n"+i).getTable());
			}
			
			long EndTime = System.currentTimeMillis();
			System.out.println((EndTime-StartTime));
			
			StartTime = System.currentTimeMillis();
			
			BruseTable t2 = t1.getMarginal("n1");
			
			EndTime = System.currentTimeMillis();
			System.out.println((EndTime-StartTime));
			
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void test4() {
		java.util.HashMap<String, Integer> hash = new java.util.HashMap<String, Integer>();
		hash.put("test", 1);
		
		int sum = 1;
		double p = 1;
		
		long StartTime = System.currentTimeMillis();
		for (int i=0; i < 2097152; i++) {
			
			for (int j=0; j < 10; j++) {
				sum += sum*j;
				hash.get("test");
			}
			
			for (int j=0; j < 10; j++) {
				sum += sum*j;
				hash.get("test");
			}
			p = p * i;
			
			for (int j=0; j < 10; j++) {
				p = p  + 1;
				hash.get("test");
			}
		}
		long EndTime = System.currentTimeMillis();
		System.out.println((EndTime-StartTime));
	}
	
	private static void test3() {
		double a[] = new double[2097152];
		double b[] = new double[10];
		double c[] = new double[2097152];
		
		java.util.Random rnd = new java.util.Random();
		
		for (int i=0; i < a.length; i++) {
			a[i] = rnd.nextDouble();
		}
		
		for (int i=0; i < b.length; i++) {
			b[i] = rnd.nextDouble();
		}
		
		java.util.HashMap<String, Integer> hash = new java.util.HashMap<String, Integer>();
		String str = new String("test");
		hash.put(str, 1);
		int idx = 0;
		long StartTime = System.currentTimeMillis();
		
		for (int i=0; i < a.length; i++) {
			//for (int k=0; k < 10; k++) {
				for (int j=0; j < b.length; j++) {
					for (int l=0; l < 10; l++) {
						idx += hash.get(str) * l;
					}
					c[i] = a[i]*b[j];
				}
			//}
		}
		
		long EndTime = System.currentTimeMillis();
		System.out.println((EndTime-StartTime));
	}
	
	private static void test1() {
		double a[] = new double[256];
		double b[] = new double[256];
		double c[] = new double[256*256];
		
		for (int i=0; i < 256; i++) {
			a[i] = 1;
			b[i] = 1;
		}
		
		long StartTime = System.currentTimeMillis();
		
		for (int k=0; k < 1000; k++) {
			for (int i=0; i < 256; i++) {
				for (int j=0; j < 256; j++) {
					c[i*256+j] = a[i]*a[j];
				}
			}
		}
		
		long EndTime = System.currentTimeMillis();
		System.out.println((EndTime-StartTime));
	}

}
