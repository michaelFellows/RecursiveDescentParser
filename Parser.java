/* Complete all the methods.
EBNF of Mini Language
Program" --> "("Sequence State")".
Sequence --> "("Statements")".
Statements --> Stmt*
Stmt --> "(" {NullStatement | Assignment | Conditional | Loop | Block}")".
State -->  "("Pairs")".
Pairs -->  Pair*.
Pair --> "("Identifier Literal")".
NullStatement --> "skip".
Assignment --> "assign" Identifier Expression.
Conditional --> "conditional" Expression Stmt Stmt.
Loop --> "loop" Expression Stmt.
Block --> "block" Statements.
Expression --> Identifier | Literal | "("Operation Expression Expression")".
Operation --> "+" |"-" | "*" | "/" | "<" | "<=" | ">" | ">=" | "=" | "!=" | "or" | "and".

Note: Treat Identifier and Literal as terminal symbols. Every symbol inside " and " is a terminal symbol. The rest are non terminals.

*/
public class Parser{
  private Token currentToken;
  Scanner scanner;
  boolean exitConditional = true, exitLoop = true, hasNextStatement = true, hasNextPair = true;

  private void accept(byte expectedKind) {
    if (currentToken.kind == expectedKind){
      currentToken = scanner.scan();
    }
    else
      new Error("Syntax error: " + currentToken.spelling + " is not expected.",
        currentToken.line);
  }

  private void acceptIt() {
    currentToken = scanner.scan();
  }

  public void parse() {
    SourceFile sourceFile = new SourceFile();
    scanner = new Scanner(sourceFile.openFile());
    currentToken = scanner.scan();
    parseProgram();
    if (currentToken.kind != Token.EOT)
      new Error("Syntax error: Redundant characters at the end of program.",
        currentToken.line);
  }

  //Program" --> "("Sequence State")".
  private void parseProgram() {
    accept(Token.LPAREN);
    parseSequence();
    parseState();
    accept(Token.RPAREN);
  }

  //Sequence --> "("Statements")".
  private void parseSequence(){
    accept(Token.LPAREN);
    parseStatements();
    accept(Token.RPAREN);
  }

  //Statements --> Stmt*
  private void parseStatements(){
    while(hasNextStatement){
      parseStmt();
    }
  }

  //Stmt --> "(" {NullStatement | Assignment | Conditional | Loop | Block}")".
  private void parseStmt(){
    boolean stmtLoop =true;
    accept(Token.LPAREN);
    while(stmtLoop) {
      if (currentToken.kind == Token.SKIP) {
        parseNullStatement();
      } 
      else if (currentToken.kind == Token.ASSIGN) {
        parseAssignment();
      }
      else if (currentToken.kind == Token.CONDITIONAL) {
        exitConditional=false;
        parseConditional();
        exitConditional=true;
      }
      else if (currentToken.kind == Token.LOOP) {
        exitLoop=false;
        parseLoop();
        exitLoop=true;
      }
      else if (currentToken.kind == Token.BLOCK) {
        parseBlock();
      }
      else {
        stmtLoop = false;
      }
    }
    accept(Token.RPAREN);
    if(exitConditional && exitLoop) {
      if(currentToken.kind == Token.RPAREN)
        hasNextStatement=false;
    }
  }

  //State -->  "("Pairs")".
  private void parseState(){
    accept(Token.LPAREN);
    parsePairs();
    accept(Token.RPAREN);
  }

  //Pairs --> Pair*.
  private void parsePairs(){
    while(hasNextPair){
      parsePair();
    }
  }

  //Pair --> "("Identifier Literal")".
  private void parsePair(){
    accept(Token.LPAREN);
    accept(Token.IDENTIFIER);
    accept(Token.LITERAL);
    accept(Token.RPAREN);
    if(currentToken.kind == Token.RPAREN){
      hasNextPair=false;
    } 
  }

  //NullStatement --> skip.
  private void parseNullStatement(){
    accept(Token.SKIP);
  }

  //Assignment --> "assign" Identifier Expression.
  private void parseAssignment(){
    accept(Token.ASSIGN);
    accept(Token.IDENTIFIER);
    parseExpression();
  }

  //Conditional --> "conditional" Expression Stmt Stmt.
  private void parseConditional(){
    accept(Token.CONDITIONAL);
    parseExpression();
    parseStmt();
    parseStmt();
  }

  //Loop --> "loop" Expression Stmt.
  private void parseLoop(){
    accept(Token.LOOP);
    parseExpression();
    parseStmt();
  }

  //Block --> "block" Statements.
  private void parseBlock(){
    accept(Token.BLOCK);
    parseStatements();
  }

  //Expression --> Identifier | Literal | "("Operation Expression Expression")".
  private void parseExpression(){
    if(currentToken.kind == Token.IDENTIFIER){
      accept(Token.IDENTIFIER);
    }
    else if(currentToken.kind == Token.LITERAL){
      accept(Token.LITERAL);
    }
    else{
      accept(Token.LPAREN);
      parseOperation();
      parseExpression();
      parseExpression();
      accept(Token.RPAREN);
    }
  }

  //Operation --> "+" |"-" | "*" | "/" | "<" | "<=" | ">" | ">=" | "=" | "!=" | "or" | "and".
  private void parseOperation(){
    accept(Token.OPERATOR);
  }
}
