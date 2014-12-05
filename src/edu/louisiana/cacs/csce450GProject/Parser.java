//		Alex Sylvester 			ajs6790
//		cmps450G			parser





package edu.louisiana.cacs.csce450Project;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.NullPointerException;
import java.lang.Character;
import java.util.*;

public class Parser{
	/*
	* YOUR CODE GOES HERE
	* 
	* You must implement two methods
	* 1. parse
	* 2. printParseTree
     
    * Print the intermediate states of the parsing process,
    * including the intermediate states of the parse tree,make
    * as specified in the class handout.
    * If the input is legal according to the grammar,
    * print ACCEPT, else UNGRAMMATICAL.
    * If the parse is successful, print the final parse tree.

	* You can modify the input and output of these function but not the name
	*/

//////////////////////////////////////////////////////////////////////////////////////////
	// lexical analyzer variables
	static int charClass;
	static String lexeme;
	static char nextChar;
	static int token;
	static int nextToken;
	static int data;
	static String tokens;

	static FileReader inputStream = null;
	
	static final int LETTER = 0;
	static final int DIGIT = 1;
	static final int UNKNOWN = 99;
	static final int EOF = -1;

	static final int INT_LIT = 10;
	static final int IDENT = 11;
	static final int ADD_OP = 21;
	static final int MULT_OP = 23;
	static final int LEFT_PAREN = 25;
	static final int RIGHT_PAREN = 26;
	static final int DOLLAR = 27;
///////////////////////////////////////////////////////////////////////////////////////////
	// parser stuff
	public static Queue<String> input_tokens = new LinkedList<String>();
	public static Stack stack = new Stack();	
	public static Stack pTreeStack = new Stack();
	public static Stack eStack = new Stack();
	public static Stack tStack = new Stack();
	public static Stack fStack = new Stack();
	public static Stack idStack = new Stack();
	public static Stack opStack = new Stack();
	public static Stack parStack = new Stack();

	static int size;
	static int action;
	static int state;
	static int gotoValue;
	static char LHS;
	static int RHS;

	static final int R1 = 71;
	static final int R2 = 72;
	static final int R3 = 73;
	static final int R4 = 74;
	static final int R5 = 75;
	static final int R6 = 76;

	static final int S5 = 81;
	static final int S6 = 82;
	static final int S7 = 83;
	static final int S4 = 84;
	static final int S11 = 85;
	static final int ACCEPT = 86;
	


////////////////////////////////////////////////////////////////////////////////////////////
	public Parser(String fileName){
		tokens = "";
		System.out.println("File to parse : "+fileName);
		try{
			inputStream = new FileReader(fileName);
			getChar();
			do{
				lex();
				} while(nextToken != EOF);
		} catch (FileNotFoundException e){
				System.out.println("File open error.");
				System.out.println("Directory: "+ System.getProperty("user.dir"));
		} finally {
			if (inputStream != null) {
				try{inputStream.close();}
				catch (IOException e){
					System.out.println("File close error.");
				}
			}
		}
		System.out.println("");
		stack.push(0);
		//stack.push("id");
		//stack.push(5);
		//System.out.println("stack = "+stack.peek());
		//getQ();
		//System.out.println("Initial Input: "+tokens +"\nPeek of top: " + input_tokens.peek());
	}

	
	
	public void printParseTree(){
		System.out.println("\n\nHello World from " + getClass().getName()+ "\n\n");
		
	}



	public void parse(){
		//action = ACCEPT;
		Integer i;
		String s;
		String h1 = "Stack", h2 = "inputTokens", h3 = "A-lookup", h4 = "A-value", h5 = "LHS", h6 = "RHS", h7 = "t-stack";
		String h8 = "gotolook", h9 = "gotovalue", h10 = "stackAction";//, h11 = "parseTreeStack";
		System.out.printf("%-40s %-20s %-10s %-10s %-10s %-10s %-20s %-10s %-10s %-20s", h1, h2, h3, h4, h5, h6, h7, h8, h9, h10);//, h11);
		System.out.println("\n__________________________________________________________________________________________________________________________________________________________________");

		int j = 0;
		do{
			
			i = (Integer) stack.peek();
			s = input_tokens.peek();
			//state = Integer.parseInt(strings);
			//System.out.println("state = "+ s);
		
			getQ();
			System.out.printf("%-40s %-20s", stack, tokens);
			Action(i, s);
			//System.out.println("action = "+action);
			if(action>80 && action<86) shift(i, s);
			else if(action>70 && action<77) reduce(i, s);

			//j++;
		} while(action != ACCEPT);

		
        printParseTree();
	}

////////////////////////////////////////////////////////////////////////////////////////
// PARSER FUNCTIONS
////////////////////////////////////////////////////////////////////////////////////////
	public void getQ(){
		tokens = "";
		Iterator it = input_tokens.iterator();
		while(it.hasNext()){
			String iteratorValue = (String)it.next();
			tokens += iteratorValue;
		}
	}

	public void shift(int i, String s){
		String blank = "";
		System.out.printf("%s%s %8s S%s", i, s, blank, state);
		stack.push(input_tokens.poll());
		System.out.printf("%-70s push %s%d", blank, s, state);
		if(s.equals("id")) idStack.push(s);		
		else if(s.equals("(")) parStack.push(s);
		else opStack.push(s);
		//pStack.push(stack.peek());
		//pTreeStack.push(pStack);
		System.out.println("");//+pTreeStack);
		stack.push(state);
	}

	public void morePop(){
		stack.pop();
		stack.pop();
	}

	public void reduce(int i, String s){
		//Integer a;
		String b = new String();
		String blank = "";
		System.out.printf("%s%s          R%s", i, s, state);
		rules();
		System.out.printf("%10s %10s", LHS, RHS);
		//Goto();
		stack.pop();
		stack.pop();
		//System.out.println("a="+a+"\n");
		if(RHS == 3){
			stack.pop();
			stack.pop();
			//System.out.println("a="+a+"\n");
			stack.pop();
			stack.pop();
		} 
		i = (Integer) stack.peek();
		System.out.printf("%20s %10s%s", stack, i, LHS);
		state = i;
		state = Goto();
		System.out.printf("%10s           push %s%s\n", state, LHS, state);
		stack.push(LHS);
		stack.push(state);
		/*switch(LHS){
			case 'F':
				fStack.push(LHS);
				fStack.push(idStack.pop());
				pTreeStack.push(fStack);
				break;
			case 'T':
				if(opStack.empty()==false){
					b = (String)opStack.pop();
					if(b.equals("*") && fStack.empty() == false && tStack.empty()==false){
						while(fStack.empty()== false) fStack.pop();
						while(tStack.empty()== false) tStack.pop();
						tStack.push('T');
						tStack.push(pTreeStack.pop());
						tStack.push(opStack.pop());
						tStack.push(pTreeStack.pop());
					//pTreeStack.push(tStack);
				}}else{
					//opStack.push(b);
					tStack.push(LHS);
					tStack.push(fStack);
					while(fStack.empty()== false) fStack.pop();}
				pTreeStack.push(tStack);
				break;	
			case 'E':
				if(opStack.empty()==false){
					b = (String) opStack.peek();
					if(b.equals("+") && tStack.empty() == false && eStack.empty()==false){
						while(eStack.empty()== false) eStack.pop();
						while(tStack.empty()== false) tStack.pop();
						eStack.push('E');
						tStack.push(pTreeStack.pop());
						tStack.push(opStack.pop());
						tStack.push(pTreeStack.pop());
					//pTreeStack.push(tStack);
				}}
				else{
					eStack.push(LHS);
					eStack.push(tStack);
					while(tStack.empty()== false) tStack.pop();}
				pTreeStack.push(eStack);
				break;
		}
		System.out.printf("%10s \n",pTreeStack);*/
	}

	public void rules(){
		switch(state){
			case 1:
				LHS = 'E';
				RHS = 3;
				break;
			case 2:
				LHS = 'E';
				RHS = 1;
				break;
			case 3:
				LHS = 'T';
				RHS = 3;
				break;
			case 4:
				LHS = 'T';
				RHS = 1;
				break;
			case 5:
				LHS = 'F';
				RHS = 3;
				break;
			case 6:
				LHS = 'F';
				RHS = 1;
				break;
			default:
				break;
		}
	}

	public int Goto(){
		switch(LHS){
			case 'E':
				if(state == 4) gotoValue = 8;
				else gotoValue = 1;
				break;
			case 'T':
				if(state == 6) gotoValue = 9;
				else gotoValue = 2;
				break;
			case 'F':
				if(state==7) gotoValue = 10;
				else gotoValue = 3;
				break;
			default:
				break;
		}
		return gotoValue;
	}
		
	
	public void Action(int ss, String tk){
		state = ss;
			if(tk.equals("id")){
				switch(ss){
					case 0:
					case 4:
					case 6:
					case 7:
						action = S5;
						state = 5;
						break;
					default:
						break;}
			}else if(tk.equals("+")){
				switch(ss){
					case 1:
						action = S6;
						state = 6;
						break;
					case 2:
						action = R2;
						state = 2;
						break;
					case 3:
						action = R4;
						state = 4;
						break;
					case 5:
						action = R6;
						state = 6;
						break;
					case 8:
						action = S6;
						state = 6;
						break;
					case 9:
						action = R1;
						state = 1;
						break;
					case 10:
						action = R3;
						state = 3;
						break;
					case 11:
						action = R5;
						state = 5;
						break;
					default:
						break;}
			}else if(tk.equals("*")){
				switch(ss){
					case 2:
						action = S7;
						state = 7;
						break;
					case 3:
						action = R4;
						state = 4;
						break;
					case 5:
						action = R6;
						state = 6;
						break;
					case 9:
						action = S7;
						state = 7;
						break;
					case 10:
						action = R3;
						state = 3;
						break;
					case 11:
						action = R5;
						state = 5;	
						break;
					default:
						break;			
				}
			}else if(tk.equals("(")){
				switch(ss){
					case 0:
					case 4:
					case 6:
					case 7:
						action = S4;
						state = 4;
						break;
					default:
						break;				
				}
			}else if(tk.equals(")")){
				switch(ss){
					case 2:
						action = R2;
						state = 2;
						break;
					case 3:
						action = R4;
						state = 4;
						break;
					case 5:
						action = R6;
						state = 6;
						break;
					case 8:
						action = S11;
						state = 11;
						break;
					case 9:
						action = R1;
						state = 1;
						break;
					case 10:
						action = R3;
						state = 3;
						break;
					case 11:
						action = R5;
						state = 5;
						break;
					default:
						break;
				}
			}else if(tk.equals("$")){
				switch(ss){
					case 1:
						action = ACCEPT;
						System.out.printf("ACCEPT");
						break;
					case 2:
						action = R2;
						state = 2;
						break;
					case 3:
						action = R4;
						state = 4;
						break;
					case 5:
						action = R6;
						state = 6;
						break;
					case 9:
						action = R1;
						state = 1;
						break;
					case 10:
						action = R3;
						state = 3;
						break;
					case 11:
						action = R5;
						state = 5;
						break;
					default:
						break;
				}
			} else{
				System.out.println("\nUnexpected Input");
			}
		
		return;
	}

	public void reduxLoookUp(int act){
		switch(act){
			case R1:
				LHS = 'E';
				RHS = 3;
				break;
			case R2:
				LHS = 'E';
				RHS = 1;
				break;
			case R3:
				LHS = 'T';
				RHS = 3;
				break;
			case R4:
				LHS = 'T';
				RHS = 1;
				break;
			case R5:
				LHS = 'F';
				RHS = 3;
				break;
			case R6:
				LHS = 'F';
				RHS = 1;
				break;}
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// lexical analyzer functions
////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void addChar(){
		lexeme += nextChar;
	}
		
	public static void getChar(){
	try{
		data = inputStream.read();
		if(data != -1){
			nextChar = (char) data;
			if (Character.isLetter(nextChar))
				charClass = LETTER;
			else if(Character.isDigit(nextChar))
				charClass = DIGIT;
			else charClass = UNKNOWN;
		}
		//else charClass = EOF;
	}
	catch (IOException e){charClass = EOF;}
	//finally{}
	}


	public static void getNonBlank() {
		while (nextChar==' ') getChar();
	}

	public static int lookup(char ch){
		switch(ch) {
			case '(':
				addChar();
				nextToken = LEFT_PAREN;
				break;
			case ')':
				addChar();
				nextToken = RIGHT_PAREN;
				break;
			case '+':
				addChar();
				nextToken = ADD_OP;
				break;
			case '*':
				addChar();
				nextToken = MULT_OP;
				break;
			case '$':
				addChar();
				nextToken = DOLLAR;
				break;
			default:
				addChar();
				nextToken = EOF;
				break;
		}
		return nextToken;
	}


	public static int lex(){
		lexeme = "";
		getNonBlank();
		switch (charClass){
			case LETTER:
				addChar();
				getChar();
				while (charClass == LETTER || charClass == DIGIT){
					addChar();
					getChar();
				}
				nextToken = IDENT;
				break;
			case DIGIT:
				addChar();
				getChar();
				while (charClass == DIGIT){
					addChar();
					getChar();
				}
				nextToken = INT_LIT;
				break;
			case UNKNOWN:
				lookup(nextChar);
				getChar();
				break;
			case EOF:
				nextToken = EOF;
				lexeme = "EOF";
		}
		//tokens += lexeme;
		if(nextToken != EOF){ 
			try{input_tokens.add(lexeme);}
			catch(NullPointerException e){System.out.println("NULL EXCEPTION FUCK");}
		}
		//System.out.println("Next token is: " + nextToken + ", next lexeme is: " + lexeme);
		return nextToken;
	}	
/////////////////////////////////////////////////////
}	// FIN
/////////////////////////////////////////////////////


