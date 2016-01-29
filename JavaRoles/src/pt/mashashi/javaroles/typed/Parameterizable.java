package pt.mashashi.javaroles.typed;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

/**
 * 
 * @author Rafael
 *
 */
public class Parameterizable{
	@SuppressWarnings("serial")
	public class NotParameterizableException extends Exception{}
	MethodDeclaration m;
	ConstructorDeclaration c;
	public Parameterizable(Node n) throws NotParameterizableException {
		boolean processed = false;
		if((processed = n.getClass().equals(MethodDeclaration.class))){
			this.m = (MethodDeclaration)n;
		}else if((processed = n.getClass().equals(ConstructorDeclaration.class))){
			this.c = (ConstructorDeclaration)n;
		}
		if(!processed){
			throw new NotParameterizableException();
		}
	}
	public List<Parameter> getParameters(){
		return m == null ? c.getParameters(): m.getParameters();
	}
}