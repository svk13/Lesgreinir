import java.util.Vector;
import java.util.HashMap;

/**
	Parser fyrir NanoMorpho.
	Höfundur: Yngvi Birgir Bergþórsson, febrúar 2017
		Sigurbjörn Viðar Karlsson, febrúar 2017
*/

public class NanoMorphoParser
{
	private NanoLexer nanoMorphoLexer;
	
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

	public NanoMorphoParser( Reader r )
	{
		nanoMorphoLexer = new NanoLexer(r,this);
	}
	
    static String advance() throws Exception
    {
        return nanoMorphoLexer.advance();
    }
    
    static String over( int tok ) throws Exception
    {
        return nanoMorphoLexer.over(tok);
    }
    
    static String over( char tok ) throws Exception
    {
        return nanoMorphoLexer.over(tok);
    }
    
    static int getNextToken()
    {
        return nanoMorphoLexer.getNextToken();
    }

	private static int varCount;
	private static HashMap<String,Integer> varTable;

	private static void addVar( String name )
	{
		if( varTable.get(name) != null )
			throw new Error("Variable "+name+" already exists, near line "+nanoMorphoLexer.getNextLine());
		varTable.put(name,varCount++);
	}

	private static int findVar( String name )
	{
		Integer res = varTable.get(name);
		if( res == null )
			throw new Error("Variable "+name+" does not exist, near line "+nanoMorphoLexer.getNextLine());
		return res;
	}
    
    static public void main( String[] args ) throws Exception
    {
        //Óþarfi þar til þulusmiður kemur til sögunnar
		//Object[] code = null;
		nanoMorphoLexer = new NanoLexer();
		NanoLispParser yyparser = new NanoLispParser(new FileReader(args[0]));
        try
        {
            nanoMorphoLexer.startLexer(args[0]);
            program();
        }
        catch( Throwable e )
        {
            System.out.println(e.getMessage());
        }
        generateProgram(args[0],code);
    }

    static void program() throws Exception
    {
		System.out.println( "\""+name+".mexe\"=main in" );
		System.out.println( " ! { { " ); 
		for ( int i=0 ; i != p.length ; i++ ){
			generateFunction(( Object[] )p[i] ); 
		}
		System.out.println ( " }}*BASIS ; " ) ;
    }

    static Object[] function() throws Exception
    {
        varCount = 0;
        varTable = new HashMap<String,Integer>();
		...
    }

    static int decl() throws Exception
    {
        int varcount = 1;
		...
        return varcount;
    }

    static Object[] expr() throws Exception
    {
		...
    }

    static Object[] binopexpr( int pri ) throws Exception
    {
        if( pri>7 )
            return smallexpr();
        else if( pri==2 )
        {
            Object[] e = binopexpr(3);
            if( getNextToken()==OPNAME && priority(nanoMorphoLexer.getNextLexeme())==2 )
            {
                String op = advance();
                e = new Object[]{"CALL",op,new Object[]{e,binopexpr(2)}};
            }
            return e;
        }
        else
        {
            Object[] e = binopexpr(pri+1);
            while( getNextToken()==OPNAME && priority(nanoMorphoLexer.getNextLexeme())==pri )
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

    static Object[] smallexpr() throws Exception
    {
		...
    }

    static Object[] body() throws Exception
    {
		...
    }
    
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
    }
}