package pt.mashashi.javaroles.typed.rolemethod;

import java.util.HashMap;

import pt.mashashi.javaroles.MissProcessingException;

/**
 * 
 * @author Rafael
 *
 */
public class MaleBonobo extends Bonobo{
	
	public MaleBonobo() {}
	
	@Override
	public String hello() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("msg", "Not processing because monkey doesn't say hello");
		throw new MissProcessingException(map);
	}
	
}
