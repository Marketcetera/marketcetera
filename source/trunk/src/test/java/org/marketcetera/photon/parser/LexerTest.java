package org.marketcetera.photon.parser;

import junit.framework.TestCase;

import org.marketcetera.core.ClassVersion;

@ClassVersion("$Id$")
public class LexerTest extends TestCase {

    public LexerTest(String name)
    {
        super(name);
    }

    public void testLexer(){
        Lexer lexer = new Lexer();
        lexer.setInput("ASDF asd 123,4 123.4 123;4 abc;d abc.d 1 1.1.1\t .01\te1.0\t123.b");

        Token aToken = lexer.getNextToken();
        assertEquals(Token.StringToken.class,  aToken.getClass());
        assertEquals("ASDF", aToken.getImage());

        aToken = lexer.getNextToken();
        assertEquals(Token.StringToken.class,  aToken.getClass());
        assertEquals("asd", aToken.getImage());

        aToken = lexer.getNextToken();
        assertEquals(Token.StringToken.class,  aToken.getClass());
        assertEquals("123,4", aToken.getImage());

        aToken = lexer.getNextToken();
        assertEquals(Token.FloatToken.class,  aToken.getClass());
        assertEquals("123.4", aToken.getImage());
        assertEquals(123.4, ((Token.FloatToken)aToken).doubleValue(), .001);

        aToken = lexer.getNextToken();
        assertEquals(Token.StringToken.class,  aToken.getClass());
        assertEquals("123;4", aToken.getImage());

        aToken = lexer.getNextToken();
        assertEquals(Token.StringToken.class,  aToken.getClass());
        assertEquals("abc;d", aToken.getImage());

        aToken = lexer.getNextToken();
        assertEquals(Token.StringToken.class,  aToken.getClass());
        assertEquals("abc.d", aToken.getImage());

        aToken = lexer.getNextToken();
        assertEquals(Token.IntToken.class,  aToken.getClass());
        assertEquals("1", aToken.getImage());
        assertEquals(1, ((Token.IntToken)aToken).intValue());

        aToken = lexer.getNextToken();
        assertEquals(Token.StringToken.class,  aToken.getClass());
        assertEquals("1.1.1", aToken.getImage());

        aToken = lexer.getNextToken();
        assertEquals(Token.FloatToken.class,  aToken.getClass());
        assertEquals(".01", aToken.getImage());
        assertEquals(.01, ((Token.FloatToken)aToken).doubleValue(), .001);

        aToken = lexer.getNextToken();
        assertEquals(Token.StringToken.class,  aToken.getClass());
        assertEquals("e1.0", aToken.getImage());

        aToken = lexer.getNextToken();
        assertEquals(Token.StringToken.class,  aToken.getClass());
        assertEquals("123.b", aToken.getImage());
    }

    public void testTypedTokens()
    {
        Lexer lexer = new Lexer();
        lexer.setInput("17 18.5 16.4 12.3 19");

        Token aToken = lexer.getNextToken();
        assertEquals(Token.IntToken.class,  aToken.getClass());
        assertEquals("17", aToken.getImage());
        assertEquals(17, ((Token.NumberToken)aToken).intValue());

        aToken = lexer.getNextToken();
        assertEquals(Token.FloatToken.class,  aToken.getClass());
        assertEquals("18.5", aToken.getImage());
        assertEquals(18, ((Token.NumberToken)aToken).intValue());
        assertEquals(18.5, ((Token.NumberToken)aToken).doubleValue(), .001);

        aToken = lexer.getNextToken();
        assertEquals(Token.FloatToken.class,  aToken.getClass());
        assertEquals("16.4", aToken.getImage());
        assertEquals(16, ((Token.NumberToken)aToken).intValue());
        assertEquals(16.4, ((Token.NumberToken)aToken).doubleValue(), .001);

        aToken = lexer.getNextToken();
        assertEquals(Token.FloatToken.class,  aToken.getClass());
        assertEquals("12.3", aToken.getImage());
        assertEquals(12, ((Token.NumberToken)aToken).intValue());
        assertEquals(12.3, ((Token.NumberToken)aToken).doubleValue(), .001);

        aToken = lexer.getNextToken();
        assertEquals(Token.IntToken.class,  aToken.getClass());
        assertEquals("19", aToken.getImage());
        assertEquals(19, ((Token.NumberToken)aToken).intValue());
        assertEquals(19, ((Token.NumberToken)aToken).doubleValue(), .001);

}
}
