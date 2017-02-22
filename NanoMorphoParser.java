import java.util.Vector;
import java.util.HashMap;
import java.io.*;

/**
	Parser fyrir NanoMorpho.
	Höfundur:   Yngvi Birgir Bergþórsson, febrúar 2017
				Sigurbjörn Viðar Karlsson, febrúar 2017
*/

public class NanoMorphoParser {
 private static NanoLexer lexer;
 private static FileReader fileReader;
 final static int ERROR = -1;
 public final static int OPNAME = 1001;
 public final static int NAME = 1002;
 public final static int LITERAL = 1003;
 public final static int FUNCTION = 1004;
 public final static int WHILE = 1005;
 public final static int IF = 1006;
 public final static int ELSE = 1007;
 public final static int ELSEIF = 1008;
 public final static int RETURN = 1009;
 public final static int VAR = 1010;

 static void advance() throws Exception {
   lexer.advance();
  }
  /*
  static String over( int tok ) throws Exception
  {
      return lexer.over(tok);
  }
  */
  

  /** 
   * Compares the current lexeme in the lexer to the input string.
   * Throws error if String does not match.
   * Other wise, it reads the next token.
   *
   * @param tok		String to be compared to
   * @return         the unpacked character translation table
   */
 static void over(String tok) throws Exception {
  lexer.over(tok);
 }

 private static int varCount;
 private static HashMap < String, Integer > varTable;

 /*	private static void addVar( String name )
 	{
 		if( varTable.get(name) != null )
 			throw new Error("Variable "+name+" already exists, near line "+lexer.getNextLine());
 		varTable.put(name,varCount++);
 	}

 	private static int findVar( String name )
 	{
 		Integer res = varTable.get(name);
 		if( res == null )
 			throw new Error("Variable "+name+" does not exist, near line "+lexer.getNextLine());
 		return res;
 	} 
 */
 static public void main(String[] args) throws Exception {
  //Object[] code = null;
  fileReader = new FileReader(args[0]);
  lexer = new NanoLexer(fileReader);
  NanoMorphoParser yyparser = new NanoMorphoParser();
  try {
	  
   advance();
 
   program();

  } catch (Throwable e) {
   System.out.println(e.getMessage());
  }
  //     generateProgram(args[0],code);
 }
 
 //program		=	{ function }
 // 			;
 static void program() throws Exception {
  while (!lexer.eof()) {
	function();
  }
  System.out.println("END OF FILE");
 }

/**
	function	= 	NAME, '(', [ NAME, { ',', NAME } ] ')'
				'{', { decl, ';' }, { expr, ';' }, '}'
				;
**/
 static void
 function() throws Exception {

  advance();
  over("NAME");
  over("(");

  if (lexer.strToken != ")") {
   over("NAME");
   while (lexer.strToken != ")") {
    over(",");
    over("NAME");
   }
  }
  over(")");

  //body
  over("{");
  decl();

  while (lexer.strToken != "}") {
   expr();
   over(";");
  }

  over("}");
 }

 // decl		=	'var', NAME, { ',', NAME }
 static void decl() throws Exception {
  while (lexer.strToken == "VAR") {
   over("VAR");
   over("NAME");
   while (lexer.strToken != ";") {
    over(",");
    over("NAME");
   }
   over(";");
  }
 }

 /**
	expr		=	'return', expr
			|	NAME, '=', expr
			|	binopexpr
				;
 **/
 static void expr() throws Exception {

  if (lexer.strToken == "RETURN") {
   over("RETURN");
   expr();
   return;
  } else if (lexer.peek(0) == '=') {
   over("NAME");
   over("=");
   expr();
   return;
  } else {
   binopexpr();
   return;
  }
 }

/**
binopexpr	=	smallexpr, { OPNAME, smallexpr }
			;
**/
 static void binopexpr() throws Exception {
  smallexpr();
  while (lexer.strToken == "OPNAME") {
   smallexpr();
  }
 }

 /**
	smallexpr	=	NAME
			|	NAME, '(', [ expr, { ',', expr } ], ')'
			|	OPNAME, smallexpr
			| 	LITERAL 
			|	'(', expr, ')'
			|	'if', expr, body, { 'elsif', expr, body }, [ 'else', body ]
			|	'while', expr, body
			;
 **/
 static void smallexpr() throws Exception {
  switch (lexer.token) {
   case NAME:
    //over("NAME");
    if (lexer.peek(0) == '(') {
     over("NAME");
     over("(");
     if (lexer.strToken != ")") {
      expr();
      while (lexer.strToken != ")") {
       over(",");
       expr();
      }
     }
     over(")");
     return;
    } else {
     over("NAME");
     return;
    }
   case OPNAME:
    over("OPNAME");
    smallexpr();
    break;
   case LITERAL:
    over("LITERAL");
    break;
   case 40: // (
    over("(");
    expr();
    over(")");
    break;
   case IF:
    over("IF");
    over("(");
    expr();
    over(")");
    body();
    while (lexer.strToken == "ELSEIF") {
     over("ELSEIF");
     over("(");
     expr();
     over(")");
     body();
    }
    if (lexer.strToken == "ELSE") {
     over("ELSE");
     body();
    }
    break;

   case WHILE:
    over("WHILE");
    over("(");
    expr();
    over(")");
    body();
    break;
   default:
    throw new Error("Unknown token: " + lexer.strToken);
  }
 }

 /**
	body		=	'{', { expr, ';' }, '}'
				;
 **/
 static void body() throws Exception {
   over("{");
   expr();
   over(";");
   while (lexer.strToken != "}") {
    expr();
    over(";");
   }
    over("}");
  }
  /*
      static void binopexpr( int pri ) throws Exception
      {
          if( pri>7 )
              return smallexpr();
          else if( pri==2 )
          {
              Object[] e = binopexpr(3);
              if( getNextToken()==OPNAME && priority(lexer.getNextLexeme())==2 )
              {
                  String op = advance();
                  e = new Object[]{"CALL",op,new Object[]{e,binopexpr(2)}};
              }
              return e;
          }
          else
          {
              Object[] e = binopexpr(pri+1);
              while( getNextToken()==OPNAME && priority(lexer.getNextLexeme())==pri )
              {
                  String op = advance();
                  e = new Object[]{"CALL",op,new Object[]{e,binopexpr(pri+1)}};
              }
              return e;
          }
      }
      
      static int priority( String opname )
      {
          switch( opname.charAt(0) )
          {
          case '^':
          case '?':
          case '~':
              return 1;
          case ':':
              return 2;
          case '|':
              return 3;
          case '&':
              return 4;
          case '!':
          case '=':
          case '<':
          case '>':
              return 5;
          case '+':
          case '-':
              return 6;
          case '*':
          case '/':
          case '%':
              return 7;
          default:
              throw new Error("Invalid opname");
          }
      }

      static void smallexpr() throws Exception
      {
  		//...
      }

      static void body() throws Exception
      {
  		...
      }
      /*
      static void generateProgram( String filename, Object[] funs )
      {
          String programname = filename.substring(0,filename.indexOf('.'));
          System.out.println("\""+programname+".mexe\" = main in");
          System.out.println("!");
          System.out.println("{{");
          for( Object f: funs )
          {
              generateFunction((Object[])f);
          }
          System.out.println("}}");
          System.out.println("*");
          System.out.println("BASIS;");
      }
      
      static void generateFunction( Object[] fun )
      {
  		...
      }
      
      static int nextLab = 0;
      
      static void generateExpr( Object[] e )
      {
  		...
      }
      
      static void generateBody( Object[] bod )
      {
  		...
      }*/
}