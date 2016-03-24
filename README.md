# JavaRoles ![travis-ci-branch:master](https://travis-ci.org/Mashashi/javaroles.svg?branch=master)

A very rudimentary and light weighted implementation of role object modelling for Java.

Main features are: 
* Class composition 
* Interface name resolution

See the in code documentation and test cases to get the felling on how it works.

## implementations
These are the two operation modes available.

### composite
The main implementation. Hopefully reliable. (See test cases)

### typed
This implementation is purely academic and highly unstable. It relies on source code analysis to resolve the type currently assigned to the rigid object, in order to resolve the object for which the method call should be dispatched. It does not work under every circumstance. (See test cases)

## examples

### composite 

Defining the interface for the roles...
```java
public interface Human {
	String hello(); 
	String die(String age);  
	String eat();
	String dance();
}
public interface Monkey {
	String hello();
	String eat();
}
```

Defining rigid type AnimalRoles...
```java
public class AnimalRoles implements Human, Monkey{

	@ObjectForRole public Human human;

	@ObjectForRole public Monkey monkey;

	public AnimalRoles(Human human, Monkey monkey){
	    this.human = human;
	    this.monkey = monkey;
	    if(this.human!=null){
	        ((Portuguese)this.human).core = this;
	    }
	}

	@Override
	public String hello() {
	    return "Default hallo";
	}

	@Override
	public String die(String age) {
	    return "Default they kill me..."+age;
	}

	@Override
	@TurnOffRole
	public String eat() {
	    return "Default eat...";
	}

	@Override
	public String dance() {
	    return "Just dance";
	}

	public String notInRole(){
	    return "Oh oh";
	}

}
```

Defining class role Monkey...
```java
public class Bonobo implements Monkey{
	
	public Bonobo() {}

	@Override
	public String hello(){
	    return "Ugauga";
	}

	@Override
	public String eat() {
	    return "Nhamnham";
	}

}
```

Defining class role Portuguese...
```java
@RoleObject(types = { AnimalRoles.class })
public class Portuguese implements Human{

	public AnimalRoles core;

	public Portuguese() {}

	@Override
	public String hello() {
	    return "Hey there";
	}

	@Override
	public String die(String age) {
	    return "They killed me "+age;
	}

	@Override
	public String eat() {
	    return "Eating boiled pork now";
	}

	@Override
	public String dance() {
	    return core.dance()+" modified!";
	}

}
```
Note: If you wouldn't have used **@RoleObject(types = { AnimalRoles.class })** it was not possible to call the original method on AnimalRoles class. And if we tried to do it a ProbablyRigidTypeNotDeclaredException would be thrown.

Putting it all together...
```java
public static void Main(String[] args){
	new RoleRegisterComposition().registerRools();
	AnimalRoles a = new AnimalRoles(new Portuguese(), new Bonobo());
	System.out.println(a.hello());
	System.out.println(a.dance());
}
```

Results in the output...
```java
"Hey there"
"Dance modified!"
```