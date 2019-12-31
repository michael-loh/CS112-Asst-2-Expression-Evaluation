package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
	
	
	private static boolean isOperand(char c){
		if(c == '+' || c == '-' || c == '*' || c == '/')
			return true;
		return false;
	}
	
	private static boolean isDupVars(ArrayList<Variable> vars, String item){
		for(int i = 0; i <  vars.size(); i++){
			if(item.equals(vars.get(i).name)){
				return true;
			}
		}
		return false;
	}
	
	private static boolean isDupArrays(ArrayList<Array> array, String item){
		for(int i = 0; i <  array.size(); i++){
			if(item.equals(array.get(i).name)){
				return true;
			}
		}
		return false;
	}
	
	private static String replaceVars(String expr, ArrayList<Variable> vars){
		
		String variable = "";
		String varReplaced = "";
		String fullReplaced = "";
		expr += "";
		//goes through expr once and replaces ONLY values in vars
		for(int i = 0; i < expr.length(); i++){
			if(Character.isLetter(expr.charAt(i))){
				variable += expr.charAt(i);
				
				if(i == expr.length()-1){
					for(int j = 0; j < vars.size(); j++){
						if(variable.equals(vars.get(j).name)){
							varReplaced += vars.get(j).value + "";
						}	
					}
				}
			}
			
			else{
				
				//searches vars array list
				if(expr.charAt(i)!= '['){	
					for(int j = 0; j < vars.size(); j++){
						if(variable.equals(vars.get(j).name)){
							varReplaced += vars.get(j).value + "";
						}	
					}
				}
				else {
					varReplaced += variable;
				}
				variable = "";
				varReplaced += expr.charAt(i);
			}
		}
		
		
		
		return varReplaced;
	}
	
	private static float addNumbers(String expr){
		String number = "";
		float temp = 0;
		float sum = 0;
		int numSize = 0;
		int operSize = 0;
		
		Stack<Float> num = new Stack();
		Stack oper = new Stack();
		//building stacks
		for(int i = 0; i < expr.length(); i++){
			if(Character.isDigit(expr.charAt(i)) || expr.charAt(i)=='.' || number.equals("")){
				number += expr.charAt(i);
				if(i == expr.length()-1){
					num.push(Float.parseFloat(number));
					numSize++;
				}
			}
			else{
				num.push(Float.parseFloat(number));
				numSize++;
				oper.push(expr.charAt(i));
				operSize++;
				number = "";
			}
		}
		
		//reversing stack to add and subtract in correct order
		
		Stack numRe = new Stack();
		Stack operRe = new Stack(); 
		
		while(!num.isEmpty()){
			numRe.push(num.pop());
		}
		
		while(!oper.isEmpty()){
			operRe.push(oper.pop());
		}
		
		//adding subtracting the numbers
		char op;
		if(!numRe.isEmpty()){
			sum = (float)numRe.pop();
		}
		while(!numRe.isEmpty()){
			op = (char)operRe.pop();
			temp = (float)numRe.pop();
			if(op == '+'){
				sum += temp;
			}
			else if(op == '-'){
				sum -= temp;
			}
				
		}
		return sum;
		
	}
	
	private static String multiplyNumbers(String expr){
		String number = "";
		String expression = "";
		String operations = "";
		Stack num = new Stack();
		Stack oper = new Stack();
		float temp1 = 0;
		float temp2 = 0;
		int numSize = 0;
		int operSize = 0;
		
		
		//mulitplication and division
		for(int i = 0; i < expr.length(); i++){
			//checks if number is digit, decimal, or negative symbol
			if(Character.isDigit(expr.charAt(i)) || expr.charAt(i)=='.' ||(numSize==operSize && isOperand(expr.charAt(i)))){
				number += expr.charAt(i);
				
				if(i == expr.length()-1){
					
					num.push(Float.parseFloat(number));					
					numSize++;
					
					if(!oper.isEmpty()){
						if(oper.peek().equals('*')){
							oper.pop();
							operSize--;
							temp1 = (float) num.pop();
							numSize--;
							temp2 = (float) num.pop();
							numSize--;
							temp1 = temp1 * temp2;
							
							num.push(temp1);
							numSize++;
						}
						else if(oper.peek().equals('/')){
							oper.pop();
							operSize--;
							temp1 = (float) num.pop();
							numSize--;
							temp2 = (float) num.pop();
							numSize--;
							temp2 = temp2/temp1;
							num.push(temp2);
							numSize++;
						}
						
						
					}
				}
				else{
					if(isOperand(expr.charAt(i+1))){
						num.push(Float.parseFloat(number));
						numSize++;
						number = "";
						if(!oper.isEmpty()){
							if(oper.peek().equals('*')){
								oper.pop();
								operSize--;
								temp1 = (float) num.pop();
								numSize--;
								temp2 = (float) num.pop();
								numSize--;
								temp1 = temp1 * temp2;
								num.push(temp1);
								numSize++;
								
							}
							else if(oper.peek().equals('/')){
								oper.pop();
								operSize--;
								temp1 = (float) num.pop();
								numSize--;
								temp2 = (float) num.pop();
								numSize--;
								temp2 = temp2/temp1;
								num.push(temp2);
								numSize++;
							}
						}
					}
				}
			}
			else{
				if(isOperand(expr.charAt(i))){
					oper.push(expr.charAt(i));
					operSize++;
				}
				
			}
			
			
			
		}
		
		while(!num.isEmpty()){
			expression = num.pop() + expression;
			if(!oper.isEmpty()){
				expression = oper.pop() + expression;
			}
		}
		return expression;
	}
			
	private static String arrayReplace(String expr, ArrayList<Array> arrays){
		
		
		String expression = "";
		String recursiveMethodInput = "";
		String var = "";
		boolean beforeBracket = true;
		boolean insideBracket = false;
		int bracketCounter = 1;
		int parenthesisCounter = 0;
		String parenthesisInput = "";
		boolean evalParenthesis = false;
		
		for(int i = 0; i < expr.length(); i++){
			
			//there is a parenthesis within the outermost brackets
			//so we start building the parenthesisInput String
			if(expr.charAt(i)=='('  && !insideBracket){
				evalParenthesis = true;
				
			}
			if(evalParenthesis){
				insideBracket = false;
				parenthesisInput += expr.charAt(i);
			}
			
			if(expr.charAt(i)=='(' && evalParenthesis)
				parenthesisCounter++;
			if(expr.charAt(i)==')' && evalParenthesis){
				parenthesisCounter--;
				if(parenthesisCounter == 0){
					expression += evalParenthesis(parenthesisInput, arrays);
					parenthesisInput = "";
					evalParenthesis = false;
				}
			}
			
			//if insideBracket = true, then it will start at the index after the bracket
			//because we don't want the bracket in the recursiveMethodInput
			if(insideBracket){
				if(expr.charAt(i)=='[')
					bracketCounter++;
				if(expr.charAt(i)==']'){
					bracketCounter--;
					if(bracketCounter==0){
						expression += evalArrayVar(recursiveMethodInput, arrays);
						insideBracket = false;
					}
				}
				recursiveMethodInput += expr.charAt(i);
			}
			
			
			
			
			//adds the characters of expr into expression
			if(!evalParenthesis&&!insideBracket &&(Character.isDigit(expr.charAt(i)) || isOperand(expr.charAt(i)))){
				expression += expr.charAt(i);
			}
			
			//finds the variable
			if(Character.isLetter(expr.charAt(i))&&!insideBracket && beforeBracket){
				
				var += expr.charAt(i);
			}
			
			//there is an array present
			//finds the [] and () and implements recursion methods
			else if(expr.charAt(i) == '['){
				beforeBracket = false;
				insideBracket = true;
			}
			
			
		}//end of loop
		
		String multiplied = multiplyNumbers(expression);
		
		float added = addNumbers(multiplied);
		
		for(int x = 0; x < arrays.size(); x++){
			if(var.equals(arrays.get(x).name)){
				return Float.toString(arrays.get(x).values[(int)added]);
			}
		}
		
		return expression;
	}
	
	
	
	
	
	//recursive method to convert array variables into numbers
	private static String evalArrayVar(String expr, ArrayList<Array>arrays){
		
		String expression = "";
		String recExpr = "";
		String replaced = "";
		String var = "";
		int parenthesisCounter = 0;
		String parenthesisInput = "";
		boolean withinBracket = false;
		boolean evalParenthesis = false;
		boolean needsRecur = false;
		int counter = 0;
		int bCounter = 0;
		
		//evaluates all parenthesis within the array
		String temp = "";
		for(int i = 0; i < expr.length(); i++){
			if(expr.charAt(i)=='('){
				evalParenthesis = true;
				
			}
			if(evalParenthesis){
				if(expr.charAt(i)=='('){
					parenthesisCounter++;
				}
				if(expr.charAt(i)==')'){
					parenthesisCounter--;
					if(parenthesisCounter == 0){
						temp += evalParenthesis(parenthesisInput + ')', arrays);
						expression += temp;
						evalParenthesis = false;
					}
				}
				parenthesisInput += expr.charAt(i);
			}
			else{
				expression += expr.charAt(i);
			}
		}
		//replaces the parenthesis within expr with values;
		expr = expression;
		//resets expression so it can be used for the below loop
		expression = "";
		
		
		//recursively evaluates the array variables within the array variable
		for(int q = 0; q < expr.length(); q++){
			
			//checks if the there exists another array variable within the current array
			//variable
			if(Character.isLetter(expr.charAt(q))){
				needsRecur=true;
			}
			//builds the variable string
			if(needsRecur && counter ==0){
				var += expr.charAt(q);
			}
			//builds the entire string needed for the recursive statement
			if(needsRecur){
				if(expr.charAt(q)=='['){
					counter ++;
					bCounter++;
				}
				if(expr.charAt(q)==']')
					counter --;
				recExpr += expr.charAt(q);
				if(counter==0 && expr.charAt(q)==']'){
					if(bCounter == 1){
						recExpr = Float.toString(replaceArrayVar(recExpr, arrays));
					}
					else if(bCounter >1){
						String replace = var;
						recExpr = recExpr.substring(var.length(), recExpr.length()-1);
						recExpr = evalArrayVar(recExpr, arrays);
						
						String m = multiplyNumbers(recExpr);
						float a = addNumbers(m);
						recExpr = Float.toString(a);
						
						replace += recExpr + ']';
						
						recExpr = Float.toString(replaceArrayVar(replace, arrays));
						
					}
					expression += recExpr;
					recExpr = "";
					var = "";
					
					needsRecur = false;
					
				}
			}
			else{
				expression += expr.charAt(q);
			}
			
		}
		
		
		
		String multiplied = multiplyNumbers(expression);
		float added = addNumbers(multiplied);
		
		return Float.toString(added);
		
	}
	
	
	
	private static float replaceArrayVar(String expr, ArrayList<Array>arrays){
		String index = "";
		String varName = "";
		boolean beforeBracket = true;
		
		for(int i = 0; i < expr.length(); i++){
			if(expr.charAt(i)=='[' ){
				beforeBracket = false;
			}
			
			if(beforeBracket){
				varName += expr.charAt(i);
			}
			else if(!beforeBracket){
				if(Character.isDigit(expr.charAt(i)) || isOperand(expr.charAt(i)) || expr.charAt(i)=='.'){
					index += expr.charAt(i);
				}
			}
		}
		String multiplied = multiplyNumbers(index);
		float added = addNumbers(multiplied);
		for(int j = 0; j < arrays.size(); j++){
			if(varName.equals(arrays.get(j).name)){
				return arrays.get(j).values[(int)added];
			}
		}
		return 0;
	}
	
	private static String evalParenthesis(String expr,ArrayList<Array>arrays){
		
		//eliminate the parenthesis
		String exprShort = "";
		if(expr.charAt(0)=='(' && expr.charAt(expr.length()-1)==')')
			exprShort = expr.substring(1,expr.length()-1);
		else{
			exprShort = expr;
		}
		
		//checks for nested parenthesis and builds recursive input if necessary
		//also builds the evaluated string at the same time
		String recExpr = "";
		int counter = 0;
		String evaluated = "";
		boolean insideParenthesis = false;
		String arrayInput = "";
		boolean solveArray = false;
		int bracketCounter = 0;
		
		for(int i = 0; i < exprShort.length(); i++){
			
			
			//if there is parenthesis
			if(exprShort.charAt(i)=='(' && !solveArray){
				counter++;
				if(!insideParenthesis)
					insideParenthesis = true;
			}
			
			//if there is an array variable in the parenthesis
			if(Character.isLetter(exprShort.charAt(i))){
				insideParenthesis = false;
				solveArray = true;
			}
			
			//if it's inside the array
			if(solveArray){
				arrayInput += exprShort.charAt(i);
			}
			
			//counts brackets and checks for end of array
			if(exprShort.charAt(i) == '['){
				bracketCounter ++;
			}
			if(exprShort.charAt(i) == ']'){
				bracketCounter --;
				if(bracketCounter == 0){
					evaluated += evalArrayVar(arrayInput, arrays);
					solveArray = false;
					
					arrayInput = "";
					
					continue;
				}
			}
			
			
			//if it's inside the parenthesis and not also inside an array within the parenthesis
			if(insideParenthesis){
				recExpr += exprShort.charAt(i);
			}
			else if(!solveArray){
				evaluated += exprShort.charAt(i);
			}
			
			if(exprShort.charAt(i)==')' && !solveArray) {
				counter --;
				if(counter==0){
					recExpr = evalParenthesis(recExpr, arrays);
					evaluated += recExpr;
					recExpr = "";
					insideParenthesis = false;
				}
			}
		}
		
		
		String multiplied = multiplyNumbers(evaluated);
		float added = addNumbers(multiplied);
		return Float.toString(added);
		
	}

	
	/**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	
    	//deletes the spaces in the expression
    	String exprShort = "";						
    	for(int i = 0; i < expr.length(); i++){
    		if(expr.charAt(i)!= ' ')
    			exprShort += expr.charAt(i);
    	}
    	
    	//assigns the variables to the array lists
    	String variable = "";
    	for(int j = 0; j < exprShort.length(); j++){
    		
    		//finds the names of the variables
    		if(Character.isLetter(exprShort.charAt(j))){
    			variable += exprShort.charAt(j);
    		}
    		
    		//assigns the variables to the array lists
    		else{
    			if(isOperand(exprShort.charAt(j)) || exprShort.charAt(j) == '(' || expr.charAt(j) == ')'){
    				if(!isDupVars(vars, variable) && !variable.equals("")){
    					vars.add(new Variable(variable));
    				}
    				variable = "";
    			}
    			else if(exprShort.charAt(j) == '['){
    				if(!isDupArrays(arrays, variable) && !variable.equals(""))
    					arrays.add(new Array(variable));
    				variable = "";
    			}
    				
    		}
    		if(j == exprShort.length()-1 && !variable.equals("")){
				if(!isDupVars(vars, variable)){
					vars.add(new Variable(variable));
				}
				variable = "";
			}
    		
    	}
    	
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	
    	//eliminates spaces
    	String exprShort = "";
    	for(int i = 0; i < expr.length(); i++){
    		if(!(expr.charAt(i) == (' '))){
    			exprShort += expr.charAt(i);
    		}
    	}
    	
    	
    	exprShort = replaceVars(exprShort, vars);
    	
    	String tempArray = "";
    	String expressionReplaced = "";
    	int counter = 0;
    	
    	for(int a = 0; a < exprShort.length(); a++){
    		if(Character.isLetter(exprShort.charAt(a))){
    			
    			for(int b = a; b < exprShort.length(); b++){
    				if(exprShort.charAt(b)=='['){
    					counter ++;
    				}
    				else if(exprShort.charAt(b) == ']'){
    					counter --;
    				}
    				tempArray += exprShort.charAt(b);
    				if(exprShort.charAt(b)==']' && counter == 0){
    					break;
    				}
    				
    				a=b+1;
    			}
    			
    			tempArray = arrayReplace(tempArray, arrays);
    			expressionReplaced += tempArray;
    			tempArray = "";
    			
    		}
    		else{
    			expressionReplaced += exprShort.charAt(a);
    		}
    		
    	}
    	
    	expressionReplaced = evalParenthesis(expressionReplaced, arrays);    	
    	
    	String multiplied = multiplyNumbers(expressionReplaced);
    	return addNumbers(multiplied);
    }
    
}
