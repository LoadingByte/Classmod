0.3.0
-----

### Additions
* The properties now set the parents of stored child feature holders instead of the PropertyAccessorFactory.
* JAXB contexts can be created with context paths and jaxb.index files supplied by Classmod.
* The property implementations provide feature definition factory methods.
* Initializable features that provide an initialize() method for better custom feature definitions with persistence support.
* Properties now support getter and setter function executors; that removes the need for GET_X and SET_X functions.
* Collection properties are normal properties with add() and remove() methods instead of the set() method.

### Fixes
* The DefaultFunctionInvocation implementation can now handle null arguments.
* The DefaultFunctionInvocation implementation no longer throws unexpected exceptions if there are less arguments than parameters.
* The DefaultFunctionInvocation implementation now transforms varargs into arrays (["testString", 0, 1, 2] -> ["testString", [0, 1, 2]]).
* The JAXB persistence system actually works (a lot of bugfixes done here).
* Refactored a whole bunch of code based on automatic suggestions.
* The LockableClass initialization is now done in the actual lockable classes instead of the feature holder.

0.2.1
-----

### Fixes
* Resolved many JavaDoc errors.

### Notes
* Maven will publish the sources and JavaDocs from this version onwards.

0.2.0
-----

### Additions
* Function executors call the next executor by themselves through a FunctionInvocation object (call chain).
* Made the whole function system more consistent (e.g. with publicly accessible invocation counters).

### Fixes
* Refactored a whole bunch of code based on automatic suggestions.
* Some internal changes which are only important for Classmod implementations.

### Notes
* In order to use this update, function executors must use the new method signature "invoke(FunctionInvocation, Object...)" and call the FunctionInvocation.next() method somewhere.

0.1.1
-----

### Fixes
* Fixed some build information which is important for Maven and Jenkins.

0.1.0
-----

### Notes
* Added the MoCl system for creating modifiable classes from Disconnected.
