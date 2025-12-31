# Truth Table Generator
A java project where the goal is to generate a correctly evaluated truth table based of an input expression given by the user.

I think almost every computer engineer is tired of writing out truth tables to be able to evaluate a boolean expression, but this generator solves this for you!

Download/clone the repository and run the TruthTableGenerator.java file.
You should see a simple Java Swing window pop up where you can enter your expression. 
I use JDK-21 but I believe that any version after JDK-11 should work!

* Current operator support is AND ('*'), OR('+'), XOR ('⊕') and NOT('!')
* Variable names can be as long as you please
* The "!" operator needs to come before an expression or variable
* You can use parenthesis to emphasise precedence
* It should give you an error message if you give it an invalid input

If you want to add uniary or binary operators you only need to add them to the Operators enum. If you want to add operators
that take more than two operands you need to add a new subclass in the Expression.java file together with adding it to the Operator enum.



