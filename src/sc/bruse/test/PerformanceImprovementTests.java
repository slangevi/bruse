package sc.bruse.test;

public class PerformanceImprovementTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
