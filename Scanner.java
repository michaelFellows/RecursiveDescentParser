/*
1. You do not need a separate token (number) for each operator. All of them
   should be translated to one token (number) which is OPERATOR. See below
2. In the switch the code for identifier and literal is complete. Do not change it

3. Symbol e means Symbol e means epsilon.

 BNF grammar of Mini Language

 Program" --> "("Sequence State")".
 Sequence --> "("Statements")".
 Statements --> Statements  Stmt | e
 Stmt --> "(" {NullStatement | Assignment | Conditional | Loop | Block}")".
 State -->  "("Pairs")".
 Pairs -->  Pairs Pair | e.
 Pair --> "("Identifier Literal")".
 NullStatement --> "skip".
 Assignment --> "assign" Identifier Expression.
 Conditional --> "conditional" Expression Stmt Stmt.
 Loop --> "loop" Expression Stmt.
 Block --> "block" Statements.
 Expression --> Identifier | Literal | "("Operation Expression Expression")".
 Operation --> "+" |"-" | "*" | "/" | "<" | "<=" | ">" | ">=" | "=" | "!=" | "or" | "and".

 Note: Treat Identifier and Literal as terminal symbols. Every symbol inside " and " is a terminal symbol. The rest are non terminals.

Input file: test.txt
Output:
 Line: 1, spelling = [(], kind = 9
 Line: 1, spelling = [)], kind = 10
 Line: 1, spelling = [sum], kind = 0
 Line: 1, spelling = [a], kind = 0
 Line: 1, spelling = [2], kind = 1
 Line: 1, spelling = [xyz], kind = 0
 Line: 2, spelling = [skip], kind = 6
 Line: 2, spelling = [assign], kind = 2
 Line: 2, spelling = [conditional], kind = 3
 Line: 2, spelling = [loop], kind = 4
 Line: 2, spelling = [block], kind = 5
 Line: 3, spelling = [1234], kind = 1
 Line: 4, spelling = [+], kind = 11
 Line: 4, spelling = [-], kind = 11
 Line: 4, spelling = [*], kind = 11
 Line: 4, spelling = [/], kind = 11
 Line: 4, spelling = [<], kind = 11
 Line: 4, spelling = [<=], kind = 11
 Line: 4, spelling = [>], kind = 11
 Line: 4, spelling = [>=], kind = 11
 Line: 4, spelling = [=], kind = 11
 Line: 4, spelling = [!=], kind = 11
 Line: 4, spelling = [or], kind = 8
 Line: 4, spelling = [and], kind = 7
 Line: 5, spelling = [-], kind = 11
 Line: 5, spelling = [1234], kind = 1
 Line 6: wrong token !

Note: After you get an error message for the symbol = remove this symbol and
run the program. Repeat this until the last wrong token which is: ?

You should get the following error messges:
 Line 6: wrong token !
 Line 6: wrong token ?
*/

 import java.io.*;

 public class Scanner{
  private char currentChar;
  private static byte currentKind;
  private static byte endToken;
  private StringBuffer currentSpelling;
  private BufferedReader inFile;
  private static int line = 1;

  public Scanner(BufferedReader inFile){
    this.inFile = inFile;
    try{
      int i = this.inFile.read();
      if(i == -1) //end of file
      currentChar = '\u0000';
      else
        currentChar = (char)i;
    }
    catch(IOException e){
      System.out.println(e);
    }
  }

  private void takeIt(){
    currentSpelling.append(currentChar);
    try{
      int i = inFile.read();
      if(i == -1) //end of file
      currentChar = '\u0000';
      else
        currentChar = (char)i;
    }
    catch(IOException e){
      System.out.println(e);
    }
  }

  private void discard(){
    try{
      int i = inFile.read();
      if(i == -1) //end of file
      currentChar = '\u0000';
      else
        currentChar = (char)i;
    }
    catch(IOException e){
      System.out.println(e);
    }
  }

  private byte scanToken(){
    byte tokenType = 0;  

    switch(currentChar){
      //These first three cases account for parentheses and End Of Transmission tokens
      case '\u0000': 
      tokenType = Token.EOT; 
      takeIt();
      break;

      case '(': 
      tokenType = Token.LPAREN;  
      takeIt(); 
      break;  

      case ')': 
      tokenType = Token.RPAREN;  
      takeIt(); 
      break;

      default: 
      //Accounts for identifiers and builds the spelling while the current character is a letter
      if(isLetter(currentChar)) {
        while(isLetter(currentChar)) {  
          takeIt();
          tokenType = Token.IDENTIFIER;
        }
        break;
      }
      //Accounts for literals and builds the spelling while the current character is a digit
      else if(isDigit(currentChar)) {
        while(isDigit(currentChar)) {
          takeIt(); 
          tokenType = Token.LITERAL;
        }
        break;
      }
      //Accounts for operators and builds the spelling while the current character is a graphic
      //That spelling is then checked against a string[] of valid operators 
      //If it is not a valid operator, the kind of Token is assigned to Token.NOTHING
      else if(isGraphic(currentChar)) { 
        while(isGraphic(currentChar)) {
          takeIt();
          boolean isOperator = false;
          String[] operators = {"+" ,"-" , "*" , "/" , "<" , "<=" , ">", ">=", "=", "!=" };
          for(int i = 0; i < operators.length; i++){
            if(currentSpelling.toString().equals(operators[i])){
              tokenType = Token.OPERATOR;
              isOperator = true;
              break;
            }
          }
          if(!isOperator){
            tokenType = Token.NOTHING;
          }
          if(isLetter(currentChar) || isDigit(currentChar)){
            break;
          }
        }
      }
      break;
    }
    
    //The next three lines should be commented out when testing this file
    //They should only be run when executing Main.java
    // if(tokenType != 13){
    //   System.out.println("Next token is: " + tokenType + " Next lexeme is: " + currentSpelling);
    // }
    return tokenType;       
  }

  private void scanSeparator(){
    switch(currentChar){
      case ' ': case '\n': case '\r': case '\t':
      if(currentChar == '\n')
        line++;
      discard();
    }
  }

  public Token scan(){
    currentSpelling = new StringBuffer("");
    while(currentChar == ' ' || currentChar == '\n' || currentChar == '\r')
      scanSeparator();
    currentKind = scanToken();
    return new Token(currentKind, currentSpelling.toString(), line);
  }

  private boolean isGraphic(char c){
    return c == '\t' ||(' ' < c && c <= '~');
  }

  private boolean isDigit(char c){
    return '0' <= c && c <= '9';
  }

  private boolean isLetter(char c){
    return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
  }


  /* Lines 180-182 in this file should be commented out before running Scanner.java
     This main method prints out each token in a test file
     Ex: Line: 1, Spelling: [(], Kind = 9 */
  public static void main(String[] args){  
    Token tokenIterator;
    SourceFile sourceFile = new SourceFile();
    BufferedReader reader = sourceFile.openFile();
    Scanner scanner = new Scanner(reader);
    while(currentKind!=13){
      tokenIterator = scanner.scan();           
      if(tokenIterator.kind == 12){
        System.out.println("Line: " + tokenIterator.line + " Wrong token! " + tokenIterator.spelling);
      }
      else if(tokenIterator.kind != 13){
        System.out.println("Line: " + tokenIterator.line + ", " + "Spelling: [" + tokenIterator.spelling + "], " + "Kind = " + tokenIterator.kind);  
      }
    }      
  }
} 

