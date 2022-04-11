package org.samig.prog.ex;

import java.util.Stack;

public class EvaluateExpression 


{
   public static int evaluate(String expression)
   {
       char[] tokens = expression.toCharArray();

        // Stack for numbers: 'values'
       Stack<Integer> values = new
                             Stack<Integer>();

       // Stack for Operators: 'ops'
       Stack<Character> ops = new Stack<Character>();

       for (int i = 0; i < tokens.length; i++)
       {
            
           // Current token is a
           // whitespace, skip it
           if (tokens[i] == ' ')
               continue;

           // Current token is a number,
           // push it to stack for numbers
           if (tokens[i] >= '0' &&
                tokens[i] <= '9')
           {
               StringBuffer sbuf = new
                           StringBuffer();
                
               // There may be more than one
               // digits in number
               while (i < tokens.length &&
                       tokens[i] >= '0' &&
                         tokens[i] <= '9')
                   sbuf.append(tokens[i++]);
               values.push(Integer.parseInt(sbuf.
                                     toString()));
              
               // right now the i points to
               // the character next to the digit,
               // since the for loop also increases
               // the i, we would skip one
               //  token position; we need to
               // decrease the value of i by 1 to
               // correct the offset.
                 i--;
           }

           // Current token is an opening brace,
           // push it to 'ops'
           else if (tokens[i] == '(')
               ops.push(tokens[i]);

           // Closing brace encountered,
           // solve entire brace
           else if (tokens[i] == ')')
           {
               while (ops.peek() != '(')
                 values.push(applyOp(ops.pop(),
                                  values.pop(),
                                values.pop()));
               ops.pop();
           }

           // Current token is an operator.
           else if (tokens[i] == '+' ||
                    tokens[i] == '-' ||
                    tokens[i] == '*' ||
                       tokens[i] == '/')
           {
               // While top of 'ops' has same
               // or greater precedence to current
               // token, which is an operator.
               // Apply operator on top of 'ops'
               // to top two elements in values stack
               while (!ops.empty() &&
                      hasPrecedence(tokens[i],
                                   ops.peek()))
                 values.push(applyOp(ops.pop(),
                                  values.pop(),
                                values.pop()));

               // Push current token to 'ops'.
               ops.push(tokens[i]);
           }
       }

       // Entire expression has been
       // parsed at this point, apply remaining
       // ops to remaining values
       while (!ops.empty())
           values.push(applyOp(ops.pop(),
                            values.pop(),
                          values.pop()));

       // Top of 'values' contains
       // result, return it
       return values.pop();
   }

   // Returns true if 'op2' has higher
   // or same precedence as 'op1',
   // otherwise returns false.
   public static boolean hasPrecedence(
                          char op1, char op2)
   {
       if (op2 == '(' || op2 == ')')
           return false;
       if ((op1 == '*' || op1 == '/') &&
           (op2 == '+' || op2 == '-'))
           return false;
       else
           return true;
   }

   // A utility method to apply an
   // operator 'op' on operands 'a'
   // and 'b'. Return the result.
   public static int applyOp(char op,
                          int b, int a)
   {
       switch (op)
       {
       case '+':
           return a + b;
       case '-':
           return a - b;
       case '*':
           return a * b;
       case '/':
           if (b == 0)
               throw new
               UnsupportedOperationException(
                     "Cannot divide by zero");
           return a / b;
       }
       return 0;
   }

   // Driver method to test above methods
   public static void main(String[] args)
   {
       System.out.println(EvaluateExpression.
                       evaluate("10 + 2 * 6"));
       System.out.println(EvaluateExpression.
               evaluate2("10 + 2 * 6"));
       System.out.println(EvaluateExpression.
                     evaluate("100 * 2 + 12"));
       System.out.println(EvaluateExpression.
               evaluate2("100 * 2 + 12"));
       System.out.println(EvaluateExpression.
                  evaluate("100 * ( 2 + 12 )"));
       System.out.println(EvaluateExpression.
               evaluate2("100 * ( 2 + 12 )"));
       System.out.println(EvaluateExpression.
            evaluate("100 * ( 2 + 12 ) / 14"));
       System.out.println(EvaluateExpression.
               evaluate2("100 * ( 2 + 12 ) / 14"));
       System.out.println(EvaluateExpression.
               evaluate2("((4 - 2^3 + 1) * -sqrt(3*3+4*4)) / 2"));
       System.out.println(EvaluateExpression.
               evaluate("(((2+3*7)/6)*((4+5)/10))+3"));
          System.out.println(EvaluateExpression.
                  evaluate2("(((2+3*7)/6)*((4+5)/10))+3"));
   }
   
   public static double evaluate2(final String str) {
	    return new Object() {
	        int pos = -1, ch;

	        void nextChar() {
	            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }

	        boolean eat(int charToEat) {
	            while (ch == ' ') nextChar();
	            if (ch == charToEat) {
	                nextChar();
	                return true;
	            }
	            return false;
	        }

	        double parse() {
	            nextChar();
	            double x = parseExpression();
	            if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
	            return x;
	        }

	        // Grammar:
	        // expression = term | expression `+` term | expression `-` term
	        // term = factor | term `*` factor | term `/` factor
	        // factor = `+` factor | `-` factor | `(` expression `)`
	        //        | number | functionName factor | factor `^` factor

	        double parseExpression() {
	            double x = parseTerm();
	            for (;;) {
	                if      (eat('+')) x += parseTerm(); // addition
	                else if (eat('-')) x -= parseTerm(); // subtraction
	                else return x;
	            }
	        }

	        double parseTerm() {
	            double x = parseFactor();
	            for (;;) {
	                if      (eat('*')) x *= parseFactor(); // multiplication
	                else if (eat('/')) x /= parseFactor(); // division
	                else return x;
	            }
	        }

	        double parseFactor() {
	            if (eat('+')) return parseFactor(); // unary plus
	            if (eat('-')) return -parseFactor(); // unary minus

	            double x;
	            int startPos = this.pos;
	            if (eat('(')) { // parentheses
	                x = parseExpression();
	                eat(')');
	            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
	                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
	                x = Double.parseDouble(str.substring(startPos, this.pos));
	            } else if (ch >= 'a' && ch <= 'z') { // functions
	                while (ch >= 'a' && ch <= 'z') nextChar();
	                String func = str.substring(startPos, this.pos);
	                x = parseFactor();
	                if (func.equals("sqrt")) x = Math.sqrt(x);
	                else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
	                else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
	                else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
	                else throw new RuntimeException("Unknown function: " + func);
	            } else {
	                throw new RuntimeException("Unexpected: " + (char)ch);
	            }

	            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

	            return x;
	        }
	    }.parse();
	}
}