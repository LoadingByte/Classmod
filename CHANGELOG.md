0.3.0
-----

### Additions
* The properties now set the parents of stored child feature holders instead of the PropertyAccessorFactory.

### Fixes
* Refactored a whole bunch of code based on automatic suggestions.

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
