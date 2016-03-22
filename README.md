# JavaRoles [![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/Mashashi/javaroles/master/LICENSE) ![travis-ci-branch:master](https://travis-ci.org/Mashashi/javaroles.svg?branch=master) [![Coverage Status](https://coveralls.io/repos/github/Mashashi/javaroles/badge.svg?branch=master)](https://coveralls.io/github/Mashashi/javaroles?branch=master)

A very rudimentary and light weighted implementation of role object modelling for Java.

Main features are: 
* Class composition 
* Interface name resolution
* Java class enhancement

See the in code documentation and test cases to get the felling on how it works.

## examples

Defining the interface for the roles...
```java
package pt.mashashi.example;

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
package pt.mashashi.example;

import pt.mashashi.javaroles.annotations.ObjRigid;
import pt.mashashi.javaroles.annotations.ObjRole;
import pt.mashashi.javaroles.annotations.Player;
import pt.mashashi.javaroles.impl.composition.TurnOffRole;

@Player
public class AnimalRoles implements Human, Monkey{

    @ObjRole public Human human;

    @ObjRole public Monkey monkey;

    @ObjRigid public Human original;

    public AnimalRoles(Human human, Monkey monkey){
        this.human = human;
        this.monkey = monkey;
        //if(this.human!=null){
        //    ((Portuguese)this.human).core = this;
        //}
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

Defining class role Portuguese with injected AnimalRoles rigid type...
```java
package pt.mashashi.example;

import pt.mashashi.javaroles.annotations.InjObjRigid;

public class Portuguese implements Human{

    @InjObjRigid public AnimalRoles core;

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
        return core.original.dance()+" modified!";
    }

}
```
Note: We can't call the core method directly this would lead to a StackOverFlowException.

Defining a class role Bonobo...
```java
package pt.mashashi.example;

public class Bonobo implements Monkey{

    public static final String HALLO = "Ugauga";
    public static final String EAT = "Nhamnham";

    public Bonobo() {}

    @Override
    public String hello(){
        return "Ugauga";
    }

    @Override
    public String eat() {
        return EAT;
    }

}
```

Putting it all together...
```java
package pt.mashashi.example;

import pt.mashashi.javaroles.impl.composition.RoleRegisterComposition;

public class Main {
    public static void main(String[] args) {
        new RoleRegisterComposition()
        		.includeGivenPkg(Main.class)
        		.registerRoles();
        AnimalRoles a = new AnimalRoles(new Portuguese(), new Bonobo());
        System.out.println(a.hello());
        System.out.println(a.dance());
    }
}
```

Results in the output...
```java
"Hey there"
"Just dance modified!"
```

## use it

### standalone

Check the [releases](https://github.com/Mashashi/javaroles/releases)

### via `jipack.io`
If you use maven, gradel or other build tool you can easily add this library to any project via `jipack.io`.

For maven just add the following to your `pom.xml`.

```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
	<dependencies>
		<dependency>
			<groupId>com.github.Mashashi</groupId>
			<artifactId>javaroles</artifactId>
			<version>vX.X.X</version> <!-- Replace with the appropriate version -->
		</dependency>
	</dependencies>
```

Look through the tags to pick a version. Any that has the format `vX.X.X` will work.

## implementations
These are the two operation modes available.

### composite
The main implementation. Hopefully reliable. (See test cases)

### typed
This implementation is purely academic and highly unstable. It relies on source code analysis to resolve the type currently assigned to the rigid object, in order to resolve the object for which the method call should be dispatched. It does not work under every circumstance. (See test cases)

## opening an issue
Opening an issue for a bug should be done in addition with a pool request with the necessary code for carrying out test that reproduces it.