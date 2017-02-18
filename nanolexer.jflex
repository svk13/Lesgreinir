/**
	JFlex lexgreiningardæmi  fyrir NanoMorpho.
	Höfundur: Yngvi Birgir Bergþórsson, febrúar 2017
		Sigurbjörn Viðar Karlsson, febrúar 2017

	Þennan lesgreini má þýða og keyra með skipununum
		java -jar JFlex-1.6.0.jar nanolexer.jflex
		javac NanoLexer.java
		java NanoLexer inntaksskrá > úttaksskrá
	Einnig má nota forritið 'make', ef viðeigandi 'makefile'
	er til staðar:
		make test
 */

import java.io.*;

%%

%public
%class NanoLexer
%unicode
%byaccj

%{
// Tokens:
final static int ERROR = -1;
final static int IF = 1001;
final static int ELSE = 1002;
final static int ELSIF = 1003;
final static int WHILE = 1004;
final static int VAR = 1005;
final static int RETURN = 1006;
final static int NAME = 1007;
final static int OPNAME = 1008;
final static int LITERAL = 1009;
final static int PROGRAM = 1010;

// Lexer:
public NanoLexer lexer;

// Parser:
public NanoMorphoParser yyparser;

// Breyta sem mun innihalda les (lexeme):
public static String lexeme;

// Constructor
public NanoLexer( java.io.Reader r, NanoMorphoParser yyparser )
{
	this(r);
	this.yyparser = yyparser;
}

// Þetta keyrir lexgreininn:
/*public static void main( String[] args ) throws Exception
{
	lexer = new NanoLexer(new FileReader(args[0]));
	int token = lexer.yylex();
	while( token!=0 )
	{
		System.out.println(""+token+": \'"+lexeme+"\'");
		token = lexer.yylex();
	}
}*/

public int getNextToken() {
	yylex()
}


%}

  /* Reglulegar skilgreiningar */

  /* Regular definitions */

_DIGIT=[0-9]
_FLOAT={_DIGIT}+\.{_DIGIT}+([eE][+-]?{_DIGIT}+)?
_INT={_DIGIT}+
_STRING=\"([^\"\\]|\\b|\\t|\\n|\\f|\\r|\\\"|\\\'|\\\\|(\\[0-3][0-7][0-7])|\\[0-7][0-7]|\\[0-7])*\"
_CHAR=\'([^\'\\]|\\b|\\t|\\n|\\f|\\r|\\\"|\\\'|\\\\|(\\[0-3][0-7][0-7])|(\\[0-7][0-7])|(\\[0-7]))\'
_DELIM=[(),;{}=]
_NAME=([:letter:]|{_DIGIT})+
//virkar bara ef bil er á milli NAME og OPNAME með þessu regexp
_OPNAME=[\+\-*/!%&=><\:\^\~&|?]+

%%
/*Fikt --Yngvi 
_BODY={_CURLY}{_DECL)?{_EXP}{_CURLY}
_FUNCTION={_NAME}{_DELIM}({_NAME}(,{_NAME})?)?{_DELIM}{_BODY}
//final static int FUNCTION = 1006;
*/

  /* Lesgreiningarreglur */
  
  
{_DELIM} {
	lexeme = yytext();
	return yycharat(0);
}

{_STRING} | {_FLOAT} | {_CHAR} | {_INT} | null | true | false {
	lexeme = yytext();
	return LITERAL;
}

"while" {
	lexeme = yytext();
	return WHILE;
}

"if" {
	lexeme = yytext();
	return IF;
}

"else" {
	lexeme = yytext();
	return ELSE;
}

"elseif" {
	lexeme = yytext();
	return ELSEIF;
}

"function" {
	lexeme = yytext();
	return PROGRAM;
}

"return" {
	lexeme = yytext();
	return RETURN; 
}

"var" {
	lexeme = yytext();
	return VAR;
}


{_OPNAME} {
	lexeme = yytext();
	return OPNAME;
}

{_NAME} {
	lexeme = yytext();
	return NAME;
}


";;;".*$ {
}

[ \t\r\n\f] {
}

. {
	lexeme = yytext();
	return ERROR;
}
