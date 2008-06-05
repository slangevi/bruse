package sc.bruse.test;

import org.junit.*;
import static org.junit.Assert.*;

import sc.bruse.engine.*;
import sc.bruse.network.*;

import java.util.*;

public class TestDseparation {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testDseparation() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("/Users/scott/studfarm.net");
			DSeparationAnalyzer dsep = new DSeparationAnalyzer(network);
			
			ArrayList<String> query = new ArrayList<String>();
			query.add("Irene");
			
			ArrayList<String> evidence = new ArrayList<String>();
			evidence.add("Gwenn");
			
			ArrayList<BruseNode> nodes = dsep.getDseparation(query, evidence);
			
			assertTrue(nodes != null);
			
			assertTrue(nodes.size() == 4);
			
			assertTrue(nodes.get(0).getName().equalsIgnoreCase(("K")));
			assertTrue(nodes.get(1).getName().equalsIgnoreCase(("Ann")));
			assertTrue(nodes.get(2).getName().equalsIgnoreCase(("Fred")));
			assertTrue(nodes.get(3).getName().equalsIgnoreCase(("L")));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testDseparation2() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("/Users/scott/studfarm.net");
			DSeparationAnalyzer dsep = new DSeparationAnalyzer(network);
			
			ArrayList<String> query = new ArrayList<String>();
			query.add("K");
			
			ArrayList<String> evidence = new ArrayList<String>();
			evidence.add("Gwenn");
			evidence.add("John");
			
			ArrayList<BruseNode> nodes = dsep.getDseparation(query, evidence);
			
			assertTrue(nodes != null);
			
			assertTrue(nodes.size() == 0);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testDseparation3() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("/Users/scott/studfarm.net");
			DSeparationAnalyzer dsep = new DSeparationAnalyzer(network);
			
			ArrayList<String> query = new ArrayList<String>();
			query.add("K");
			
			ArrayList<String> evidence = new ArrayList<String>();
			evidence.add("Gwenn");
			evidence.add("Dorothy");
			
			ArrayList<BruseNode> nodes = dsep.getDseparation(query, evidence);
			
			assertTrue(nodes != null);
			
			assertTrue(nodes.size() == 2);
			
			assertTrue(nodes.get(0).getName().equalsIgnoreCase(("Cecily")));
			assertTrue(nodes.get(1).getName().equalsIgnoreCase(("L")));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testDseparation4() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("/Users/scott/studfarm.net");
			DSeparationAnalyzer dsep = new DSeparationAnalyzer(network);
			
			ArrayList<String> query = new ArrayList<String>();
			query.add("John");
			
			ArrayList<String> evidence = new ArrayList<String>();
			evidence.add("Henry");
			evidence.add("Irene");
			
			ArrayList<BruseNode> nodes = dsep.getDseparation(query, evidence);
			
			assertTrue(nodes != null);
			
			assertTrue(nodes.size() == 9);
			
			assertTrue(nodes.get(0).getName().equalsIgnoreCase(("K")));
			assertTrue(nodes.get(1).getName().equalsIgnoreCase(("Gwenn")));
			assertTrue(nodes.get(2).getName().equalsIgnoreCase(("Dorothy")));
			assertTrue(nodes.get(3).getName().equalsIgnoreCase(("Brian")));
			assertTrue(nodes.get(4).getName().equalsIgnoreCase(("Eric")));
			assertTrue(nodes.get(5).getName().equalsIgnoreCase(("Ann")));
			assertTrue(nodes.get(6).getName().equalsIgnoreCase(("Fred")));
			assertTrue(nodes.get(7).getName().equalsIgnoreCase(("Cecily")));
			assertTrue(nodes.get(8).getName().equalsIgnoreCase(("L")));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testDseparation5() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("/Users/scott/studfarm.net");
			DSeparationAnalyzer dsep = new DSeparationAnalyzer(network);
			
			ArrayList<String> query = new ArrayList<String>();
			query.add("John");
			
			ArrayList<String> evidence = new ArrayList<String>();
			evidence.add("John");
			
			ArrayList<BruseNode> nodes = dsep.getDseparation(query, evidence);
			
			assertTrue(nodes != null);
			
			assertTrue(nodes.size() == 11);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
}
