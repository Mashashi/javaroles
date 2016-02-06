package pt.mashashi.javaroles.typed;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * 
 * @author Rafael
 *
 */
@SuppressWarnings("rawtypes")
class MethodVisitor extends VoidVisitorAdapter {
    	
	private int line;
	private String var;
	private List<String> roleName;
	private String methodResolve;
	
	public MethodVisitor(int line, String methodResolve){
		this.line = line;
		this.methodResolve = methodResolve;
		this.roleName = new LinkedList<String>();
	}
	
	public List<String> getRoleNames() {
		return roleName;
	}
	
	class VariableVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(VariableDeclarationExpr n, Object arg) {
        	for(VariableDeclarator v: n.getVars()){
        		if(v.getId().getName().equals(var)){
	        		roleName.add(n.getType().toString());
	        	}
        	}
        }
    }
	
	class ConditionalExprVisitor extends VoidVisitorAdapter {
		@Override
        public void visit(ConditionalExpr n, Object arg) {
        	if(n.getBeginLine()==line){
        		throw new ConditionalExprNotSupportedByRoleSystemException(n.toString());
        	}
        }
    }
	
	class CallVisitor extends VoidVisitorAdapter {
		public CallVisitor() {}
        @SuppressWarnings("unchecked")
		@Override
        public void visit(MethodCallExpr n, Object arg) {
        	
            if(n.getBeginLine() <= line && n.getEndLine() >= line){ 
            	// This guard deals with methods across several lines
        		
            	if(n.getClass().equals(MethodCallExpr.class)){
            		
            		if(methodResolve.equals(n.getName())){
	            		
            			int rolesRead = roleName.size();
	            		
	            		{ // Deal with casts only able to get strings no t
	            			String scope = n.getScope().toString();
	            			Pattern p = Pattern.compile("^\\(\\((.*)\\) (.*)\\)$");
	            			Matcher m =p.matcher(scope);
	            			if(m.matches()){
	            				roleName.add(m.group(1));
	            				return ;
	            			}
	            		}
	            		
	            		Node parent = n;
	            		Parameterizable m = null;
	            		
	            		if(roleName.size()==rolesRead){ 
	            			// Compute for same block or super block	
	            			// Get the function scope for process parameters
			            	var = n.getScope().toString();
			            	while(m == null){
			            		do{
				        			parent = parent.getParentNode();
				        			try {
				            			m = new Parameterizable(parent);
				            		} catch (Parameterizable.NotParameterizableException e) {}
				        		}while(m ==null && !parent.getClass().equals(BlockStmt.class));			            		
			            		if(roleName.size()==rolesRead){
			            			parent.accept(new VariableVisitor(), null);
			            		}
			            	}
	            		}
	            		
	            		if(roleName.isEmpty()){ 
	            			// Compute for constructor or method
	        				parent = parent.getParentNode();
	        				for(Parameter p : m.getParameters()){
	        					if(p.getId().getName().equals(var)){
	        						roleName.add(p.getType().toString());
	        					}
	        				}
		        		}
	            		
	            	}else{
			        	List<Node> childs = n.getChildrenNodes();
			        	for(Node c : childs){
			        		c.accept(this, null);
			        	}
			        }
	            }
	        }
        }
    }
	
    @SuppressWarnings("unchecked")
	@Override
    public void visit(MethodDeclaration n, Object arg) {
    	if(n.getBeginLine()<line && n.getEndLine()>line){
    		n.accept(new ConditionalExprVisitor(), null);
    		n.accept(new CallVisitor(), null);
    	}
    }
    
}