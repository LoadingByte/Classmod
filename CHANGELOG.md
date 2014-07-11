0.3.0
-----

### Additions
* Improved the performance of the DefaultFeatureHolder's get() method by storing the features in a map with their names as the keys.
* Added a new factory system which can be used to create implementations without directly depending on them.
* Initializable features that provide an initialize() method for better custom feature definitions with persistence support.
* JAXB contexts can be created with context paths and jaxb.index files supplied by Classmod.
* Properties now set the parents of stored child feature holders (The PropertyAccessorFactory used to do that).
* Properties have persistence support for maps and arrays.
* Properties now support getter and setter function executors; that removes the need for GET_X and SET_X functions.
* Collection properties are normal properties with add() and remove() methods instead of the set() method.
* The interface ValueSupplier abstracts the principle of a get() method and is extended by every property. It can be used whenever the type of property isn't known.
* All property definitions can take ValueFactory instances which supply them with initial values/collections.
* The "ignoreEquals" flag on properties makes their equals() method always return true, so they are excluded from equality checks of feature holders.
* The storage system replaces the old property template method pattern.
* The AbstractFunctionDefinition implementation supports overriding executors (two executors with the same name, but with different variants).
* The DefaultFunctionInvocation and the AbstractFunction classes operate faster because they skip the argument validation process when there are no arguments and no parameters.
* ChildFeatureHolders must provide the type of their parent objects as a class object for more type-safety.

### Removals
* Removed the ExecutorInvocationException. That exception was thrown by every function executor / function and produced unnecessary try-catchs. RuntimeExceptions should be used instead.
* Removed the whole lock system; the old system wasn't compatible with the new property system and technically just added complexity instead of benefit.
* Removed the limit/delay system; it was intended for update functions whose functionality should be implemented by a specialized feature.
* Removed the AbstractFeature.setParent() method that was used to change the holder of a feature after creation.
* Removed the FunctionDefinitionFactory utility class (it was replaced with a ClassmodFactory mapping).

### Fixes
* The DefaultFunctionInvocation implementation can now handle null arguments.
* The DefaultFunctionInvocation implementation no longer throws unexpected exceptions if there are less arguments than parameters.
* The DefaultFunctionInvocation implementation now transforms varargs into arrays (["testString", 0, 1, 2] -> ["testString", [0, 1, 2]]).
* The JAXB persistence system actually works (a lot of bugfixes done here).
* Listified everything because sets (especially HashSets) are not safe for modifiable objects.
* Refactored a whole bunch of code based on automatic suggestions.

### Notes
* Classmod is now using the version 1.7 of Java.
* Introducing many major performance improvements, especially related to functions. Classmod applications should run much smoother now.
* The update changed the parameter order of every add/removeExecutor method to (name, variant, executor) in order to logically conform the new function executor override feature. Users of the function system must be refactored.
* Some setLocked() statements and all Lockable annotations must be removed in order to conform with the new update (see the removals section for further explanation why).
* The new storage and factory systems force all definition code to be updated.

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
