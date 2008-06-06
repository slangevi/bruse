package sc.bruse.test;

import sc.bruse.network.*;
import sc.bruse.engine.*;

public class PerformanceImprovementTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test2();
	}
	
	private static void test2() {
		String file = "tests/test61.net";
		
		try {
			BruseNetwork network = BruseNetworkFactory.create(file);
			
			BruseTable t1 = network.getNode("n1").getTable();
			
			BruseTable t2 = network.getNode("n1").getTable();
			
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
			
			
			StartTime = System.currentTimeMillis();
			
			for (int i=1; i < 11; i++) {
				t1 = t1.multiplyBy(network.getNode("n"+i).getTable());
			}
			
			EndTime = System.currentTimeMillis();
			System.out.println((EndTime-StartTime));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
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
