# JavaRoles ![travis-ci-branch:master](https://travis-ci.org/Mashashi/javaroles.svg?branch=master)

A very rudimentary and light weighted implementation of role object modelling for Java.

Main features are mixin classes and interface name resolution.

See the in code documentation and test cases to get the felling how it works.

# implementations
These are the two operation modes available.

### composite
The main implementation. Hopefully reliable. (See test cases)

### typed
This implementation is purely academic and highly unstable. It relies on source code analysis to resolve the type currently assigned to the rigid object, in order to resolve the object for which the method call should be dispatched. It does not work under every circumstance. (See test cases)