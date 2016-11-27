package pt.mashashi.javaroles.test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.mashashi.javaroles.CircularLifoBuffer;

public class CircularLifoBufferTest {
	
	private static PrintStream ps;
	private static ByteArrayOutputStream baos;
	
	@BeforeClass
	public static void setup(){
		baos = new ByteArrayOutputStream();
	    ps = new PrintStream(baos);
	    System.setOut(ps);
	}
	
	@Test
	public void testPrint() {
		CircularLifoBuffer clb = new CircularLifoBuffer(2);
		clb.add("1");
		clb.add("2");
		clb.add("3");
		clb.printAll();
		String sep = System.getProperty("line.separator");
		assertEquals("b[0]=3"+sep+"b[1]=2"+sep, baos.toString());
	}
	
	@Test
	public void testBasic() {
		CircularLifoBuffer clb = new CircularLifoBuffer();
		clb.add("1");
		clb.add("2");
		clb.add("3");
		clb.add("4");
		clb.add("5");
		assertEquals(5, clb.maxSize());
		assertEquals(false, clb.isFull());
		assertEquals("5", clb.remove());
	}
	
	@Test
	public void testIterator() {
		CircularLifoBuffer clb = new CircularLifoBuffer();
		clb.add("1");
		clb.add("2");
		clb.add("3");
		clb.add("4");
		clb.add("5");
		Iterator<?> ite = clb.iterator();
		int i = 5;
		while(ite.hasNext()){
			assertEquals(Integer.toString(i), ite.next());
			i--;
			ite.remove();
		}
		assertEquals(0, clb.size());
	}
	
}
