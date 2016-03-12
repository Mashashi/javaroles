package pt.mashashi.javaroles;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

public class LoggerTarget extends WriterAppender{
	
	private static ByteArrayOutputStream lastBaus = null;
	
	private ByteArrayOutputStream baos;
	private static PrintStream ps;
	
	public LoggerTarget() {
		//Layout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p - %m%n");
		Layout layout = new PatternLayout("%m%n");
	    setLayout(layout);
	    lastBaus = baos = new ByteArrayOutputStream();
		ps = new PrintStream(baos);
		ps.println("Start");
		setWriter(createWriter(new SystemOutStream()));
	}
	
	public static String string(){
		try {
			lastBaus.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lastBaus.toString();
	}
	
	public static List<String> string(String regex){
		return grep(string(), regex);
	}
	
	private static List<String> grep(String log, String start){
		List<String> l = new LinkedList<>();
		String[] ss = log.split("\r?\n");
		for(String s : ss){
			if(s.startsWith(start)){ 
				l.add(s.substring(start.length()));
			}
		}
		return l;
	}
	
	private static class SystemOutStream extends OutputStream {
        public SystemOutStream() {
        }

        public void close() {
        	ps.close();
        }

        public void flush() {
            ps.flush();
        }

        public void write(final byte[] b) throws IOException {
        	lastBaus.write(b);
        }

        public void write(final byte[] b, final int off, final int len){
        	lastBaus.write(b, off, len);
        }

        public void write(final int b) {
        	lastBaus.write(b);
        }
        
    }
	
}
