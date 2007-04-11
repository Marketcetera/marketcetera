package org.marketcetera.photon.parser;

import jfun.parsec.Tok;
import jfun.parsec.tokens.TokenType;
import jfun.parsec.tokens.TypedToken;
import junit.framework.Test;
import junit.framework.TestCase;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraTestSuite;

@ClassVersion("$Id$")
public class LexerTest extends TestCase {

    public LexerTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        MarketceteraTestSuite suite = new MarketceteraTestSuite(LexerTest.class);
        return suite;
    }

    public void testLexer(){
    	CommandParser parser = new CommandParser();
    	
    	Tok[] parsedTokens = parser.lex("ASDF asd 123.4 123;4 abc;d abc.d 1 1.1.1 .01 e1.0 123.b");

    	int i = 0;
    	Tok aToken = parsedTokens[i++];
        assertEquals(TokenType.Word,  ((TypedToken)aToken.getToken()).getType());
        assertEquals("ASDF", ((TypedToken)aToken.getToken()).getText());

        aToken = parsedTokens[i++];
        assertEquals(TokenType.Word,  ((TypedToken)aToken.getToken()).getType());
        assertEquals("asd", ((TypedToken)aToken.getToken()).getText());

        aToken = parsedTokens[i++];
        assertEquals(TokenType.Decimal,  ((TypedToken)aToken.getToken()).getType());
        assertEquals("123.4", ((TypedToken)aToken.getToken()).getText());

        aToken = parsedTokens[i++];
        assertEquals(TokenType.Word,  ((TypedToken)aToken.getToken()).getType());
        assertEquals("123;4", ((TypedToken)aToken.getToken()).getText());

        aToken = parsedTokens[i++];
        assertEquals(TokenType.Word,  ((TypedToken)aToken.getToken()).getType());
        assertEquals("abc;d", ((TypedToken)aToken.getToken()).getText());

        aToken = parsedTokens[i++];
        assertEquals(TokenType.Word,  ((TypedToken)aToken.getToken()).getType());
        assertEquals("abc.d", ((TypedToken)aToken.getToken()).getText());

        aToken = parsedTokens[i++];
        assertEquals(TokenType.Integer,  ((TypedToken)aToken.getToken()).getType());
        assertEquals("1", ((TypedToken)aToken.getToken()).getText());

        aToken = parsedTokens[i++];
        assertEquals(TokenType.Word,  ((TypedToken)aToken.getToken()).getType());
        assertEquals("1.1.1", ((TypedToken)aToken.getToken()).getText());

        aToken = parsedTokens[i++];
        assertEquals(TokenType.Decimal,  ((TypedToken)aToken.getToken()).getType());
        assertEquals(".01", ((TypedToken)aToken.getToken()).getText());

        aToken = parsedTokens[i++];
        assertEquals(TokenType.Word,  ((TypedToken)aToken.getToken()).getType());
        assertEquals("e1.0", ((TypedToken)aToken.getToken()).getText());

        aToken = parsedTokens[i++];
        assertEquals(TokenType.Word,  ((TypedToken)aToken.getToken()).getType());
        assertEquals("123.b", ((TypedToken)aToken.getToken()).getText());
    }

    public void testTypedTokens()
    {
    	CommandParser parser = new CommandParser();
    	Tok[] parsedTokens = parser.lex("17 18.5 16.4 12.3 19");

    	int i = 0;
        Tok aToken = parsedTokens[i++];
        assertEquals(TokenType.Integer,  ((TypedToken)aToken.getToken()).getType());
        assertEquals("17", ((TypedToken)aToken.getToken()).getText());

        aToken = parsedTokens[i++];
        assertEquals(TokenType.Decimal,  ((TypedToken)aToken.getToken()).getType());
        assertEquals("18.5", ((TypedToken)aToken.getToken()).getText());

        aToken = parsedTokens[i++];
        assertEquals(TokenType.Decimal,  ((TypedToken)aToken.getToken()).getType());
        assertEquals("16.4", ((TypedToken)aToken.getToken()).getText());

        aToken = parsedTokens[i++];
        assertEquals(TokenType.Decimal,  ((TypedToken)aToken.getToken()).getType());
        assertEquals("12.3", ((TypedToken)aToken.getToken()).getText());

        aToken = parsedTokens[i++];
        assertEquals(TokenType.Integer,  ((TypedToken)aToken.getToken()).getType());
        assertEquals("19", ((TypedToken)aToken.getToken()).getText());

}
}
