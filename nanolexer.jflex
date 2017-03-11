/**
	JFlex lexgreiningardæmi  fyrir NanoMorpho.
	Höfundur: Yngvi Birgir Bergþórsson, janúar 2017

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
%line
%column

%{

// Tokens:
public final static int ERROR = -1;
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

// Breyta sem mun innihalda les (lexeme):
public static String lexeme1;
public static String lexeme2;
public static int token1;
public static int token2;
public static int line1,line2,column1, column2;


public int myType(String tok){
	int tmp;
			switch(tok)
		{	
			case "FUNCTION":  
				tmp = FUNCTION;
				return tmp;
			case "LITERAL":  
				tmp = LITERAL;
				return tmp;
			case "NAME":  
				tmp = NAME;
			//	isName);
				return tmp;
			case "VAR":
				tmp = VAR;
			//	isVAR);
				return tmp;
			case "(":
				tmp = 40;
				return tmp;
			case ")":
				tmp = 41;
				return tmp;
			case "{": 
				tmp = 123;
				return tmp;
			case "}":
				tmp = 125;
				return tmp;
			case ";":
				tmp = 59;
				return tmp;
			case ",":
				tmp = 44;
				return tmp;
			case "=":
				tmp = 61;
				return tmp;	
			case "OPNAME":
				tmp = OPNAME;
				return tmp;
			case "ERROR":
				tmp = ERROR;
				return tmp;
			case "WHILE":
				tmp = WHILE;
				return tmp;
			case "IF":
				tmp = IF;
				return tmp;
			case "ELSE":
				tmp = ELSE;
				return tmp;
			case "ELSEIF":
				tmp = ELSEIF;
				return tmp;
			case "RETURN":
				tmp = RETURN;
				return tmp;
			default: throw new Error("Unknown token: "  + tok);
		}
	
}


// Þetta keyrir lexgreininn:
public static void main( String[] args ) throws Exception
{
	NanoLexer lexer = new NanoLexer(new FileReader(args[0]));
	lexer.startLex();
	while( token1!=0 )
	{
		System.out.println(""+token1+": \'"+lexeme1+"\'");
		//lexer.line();
		lexer.advance();
		
	}
}

public void line(){
	System.out.println("Line: "+yyline+": \t column: \'"+yycolumn+"\'");
}

public int getNextLine()
{
	return this.line1+1;
}

public int getToken(){
	return this.token1;
}
public String getLexeme(){
	return this.lexeme1;
}
public int getNextToken(){
	return this.token2;
}
public String getNextLexeme(){
	return this.lexeme2;
}

public void startLex() throws Exception{
	
	token1 = this.yylex();
	lexeme1 = this.yytext();

	if(token1==0){
		token2=0;
		return;
	}
	token2 = this.yylex();
	lexeme2 = this.yytext();
	
}

public String over(String tok) throws Exception{
	so("Over: "+ lexeme1 + " \t" + tok);
    so("Token1: "+token1);
	if( !lexeme1.equals(tok) ) throw new Error("Expected: "+tok+" Found: "+lexeme1+" At line: " + yyline+" Column: " + yycolumn);

    String result = lexeme1;
	this.advance();
	return result;
}

public String over(int tok) throws Exception{
	so("Over: "+ lexeme1 + " \t" + tok);
    so("Token1: "+token1);
	if( token1!=tok ) throw new Error("Expected: "+tok+" Found: "+token1+" At line: " + yyline+" Column: " + yycolumn);
    //this.advance();
	String result = lexeme1;
	this.advance();
	return result;

}

public String advance() throws Exception{
	/*token1 = token2;
	lexeme1 = lexeme2;
	if(token1==0) return;
	token2 = this.yylex();
	lexeme2 = this.yytext();*/

	String res = lexeme1;
	token1 = token2;
	lexeme1 = lexeme2;
	line1 = line2;
	column1 = column2;
	if( token2==0 ) return res;

	token2 = this.yylex();
    lexeme2 = this.yytext();
	line2 = this.yyline;
	column2 = this.yycolumn;
    //so("token1: " + token1 + " Lexeme1 : " + lexeme1 + " Token2: " + token2 + " lexeme2  " + lexeme2);
	return res;


}


public void so(String out){
	System.out.println(out);
}

// Notkun: char c = l.getToken();
		// Eftir:  c er tókið (token) sem stendur fyrir
		//         það mál sem næsta les í l flokkast í.
	

		public boolean eof(){
			return this.zzEOFDone;
		
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
	//lexeme1 = yytext();
	return yycharat(0);
}

{_STRING} | {_FLOAT} | {_CHAR} | {_INT} | null | true | false {
	//lexeme1 = yytext();
	return LITERAL;
}

"while" {
	//lexeme1 = yytext();
	return WHILE;
}

"if" {
	//lexeme1 = yytext();
	return IF;
}

"else" {
	//lexeme1 = yytext();
	return ELSE;
}

"elseif" {
	//lexeme1 = yytext();
	return ELSEIF;
}

"function" {
	//lexeme1 = yytext();
	return FUNCTION;
}

"return" {
	//lexeme1 = yytext();
	return RETURN; 
}

"var" {
	//lexeme1 = yytext();
	return VAR;
}


{_OPNAME} {
	//lexeme1 = yytext();
	return OPNAME;
}

{_NAME} {
	//lexeme1 = yytext();
	return NAME;
}


";;;".*$ {
}

[ \t\r\n\f] {
}

. {

	return ERROR;
}
