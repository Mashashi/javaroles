package pt.mashashi.javaroles.test;

import org.junit.Ignore;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class SpecificTestRule implements MethodRule {
    protected String method;
    public SpecificTestRule() {
        method = "testProxyRules";
    }
    @Override
    public Statement apply(final Statement statement,
            final FrameworkMethod frameworkMethod, final Object o) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                boolean runMethod = false;
                //Ignore ignore = frameworkMethod.getAnnotation(Ignore.class);
                if ((method != null)  && method.equals(frameworkMethod.getName())) {
                    runMethod = true;
                } 
                /*else if (ignore == null) {
                    runMethod = true;
                }*/
                if (runMethod) {
                    statement.evaluate();
                }
            }
        };
    }
}