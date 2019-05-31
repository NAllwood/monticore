
# MontiCore Grammars - an Overview

[MontiCore](http://www.monticore.de) is a language workbench. It uses 
grammars to describe DSLs. The extended 
grammar format allows to compose grammars, to inherit, extend, embedd 
and aggregate grammars (see the reference manual for details).

Here comes a list of grammars available in the MontiCore core project 
together with short descriptions and their status:


## Status of Grammars 

The typical status of a grammar is:

1. **MontiCore stable**:
Such a grammar is meant to be stable in the further development of 
MontiCore. The grammar is tested and assumed to be of high quality.
It may rarely happen that smaller extensions are made in a conservative 
form, which means that (1) composition with any other grammars,
(2) extensions and adaptations and (3) handwritten extensions will 
still work.

1. **Beta: In Stabilization**:
Such a grammar is in the process of becoming stable. One might already 
include the grammar, but some changes may still appear.
(See task list for potential changes.)

1. **Alpha: Intention to become stable**:
Such a grammar is relatively fresh, but intended to become stable 
and useful. Changes may occur, e.g. when restructuring or bug fixing.

1. **Deprecated**:
The grammar should not be used anymore, it is deprecated and a newer
version of the content exists in another grammar.

1. **Unclarified**:
Some of the grammars are just there to be used for example as
tests or as inspirations for your own definitions. It may be that 
such a grammar becomes stable, if enough interest exists.

The deprecated grammars are typically not listed in this overview.
There may also be further unclarfied grammars around.


## General: List of Grammars in package de.monticore

### MCBasics.mc4  (stable)
* This grammar defines absolute basics, such as spaces, 
Java-like comments and Names. 
It should be useful in many languages.
  
  
## Types: List of Grammars in package de.monticore.types

These grammars generally deal with type definitions and build on each 
other:

### MCBasicTypes.mc4 (stable)
* This grammar defines basic types. This eases the reuse of type 
structures in languages similar to Java, that are somewhat 
simplified, e.g. without generics.
* The grammar contains types from Java, e.g., primitives, void, 
classes (also sometimes called "reference types").
 
### MCCollectionTypes.mc4 (stable)
* This grammar defines four generics: List<A>, Map<A,B>, Set<A> and 
Optional<A> on top of basic types.
* These four generics correspond to a typical predefined set of generic 
types for example used in connection with UML class diagrams or the
OCL. UML associations typically have those association multiplicities and 
therefore these types are of interest.
* This eases the reuse of type structures in languages similar to Java,
that are somewhat simplified, e.g. without general generics.


### MCSimpleGenericTypes.mc4 (stable)
* This grammar introduces freely defined generic types
such as Blubb<A>, Bla<B,C>, Foo<Blubb<D>>
* These generics are covering a wide range of uses for generic types,
although they don't cover type restrictions on the arguments, like in 
Java. 


### MCFullGenericTypes.mc4 (stable)
* This grammar completes the type definitions to 
support the full Java type system including wildcards Blubb<? extends A>
* A general advice: When you are not sure that you need this kind of
types, then use a simpler version from above. Type checking ist tricky.



## Expressions: List of Grammars in package de.monticore.expressions

Expressions are defined in several grammars forming a (nonlinear) hierachy,
so that developers can choose the optimal grammar they want to build on 
for their language and combine these with the appropriate typing 
infrastructure.

This modularity of expressions and associated types greatly eases 
the reuse of type structures in languages similar to Java.


### ExpressionsBasis.mc4 (stable)
* This grammar defines core interfaces for expressions and the 
kinds of symbols necessary.
* A hierarchy of conservative extensions to this grammar realize
these interfaces in various forms.


### CommonExpressions.mc4 (stable)
* This grammar defines a typical standard set of operations for
expressions. 
* This is a subset of Java as well as OCL/P, 
mainly for arithmetic, comparisons, variable use (v), 
attribute use (o.att), method call (foo(arg,arg2)) and brackets (exp).


### BitExpressions.mc4 (stable)
* This grammar defines a typical standard set of operations for
expressions. 
* This is a subset of Java for binary expressions 
like <<, >>, >>>, &, ^ and |


### AssignmentExpressions.mc4 (stable)
* This grammar defines all Java expressions that have side effects.
* This includes assignment expressions like =, +=, etc. and 
suffix and prefix expressions like ++, --, etc.


### JavaClassExpressions.mc4 (stable)
* This grammar defines Java specific class expressions like super, 
this, type cast, etc.
* This grammar should only be included, when a mapping to Java is
intended and the full power of Java should be available in the 
modelling language.


### SetExpressions.mc4 (Beta: In Stabilization)
* This grammar defines set expressions like union, intersection etc.
these operations are typical for a logic with set operations, like 
UML's OCL.


### OCLExpressions.mc4 (Alpha: Needs restructuring)
* This grammar defines a expressions typical to UMLs OCL .
* This grammar will be restructured especially for the non expression part.




## Further grammars (status: unclarified):

* Cardinality.mc4
* Completeness.mc4
* lexicals/Lexicals.mc4
* literals/Literals.mc4
* MCBasicLiterals.mc4
* MCCommon.mc4
* MCHexNumbers.mc4
* MCJavaLiterals.mc4
* MCNumbers.mc4
* StringLiterals.mc4
* UMLModifier.mc4
* UMLStereotype.mc4






