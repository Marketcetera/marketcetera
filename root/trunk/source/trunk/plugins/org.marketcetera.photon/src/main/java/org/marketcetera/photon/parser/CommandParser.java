package org.marketcetera.photon.parser;

import java.math.BigDecimal;

import jfun.parsec.FromString;
import jfun.parsec.FromToken;
import jfun.parsec.Lexers;
import jfun.parsec.Map;
import jfun.parsec.Mapn;
import jfun.parsec.Parser;
import jfun.parsec.Parsers;
import jfun.parsec.Scanners;
import jfun.parsec.Terms;
import jfun.parsec.Tok;
import jfun.parsec.UserException;
import jfun.parsec._;
import jfun.parsec.pattern.CharPredicates;
import jfun.parsec.pattern.Patterns;
import jfun.parsec.tokens.TokenType;
import jfun.parsec.tokens.Tokenizers;
import jfun.parsec.tokens.TypedToken;

import org.marketcetera.core.AccountID;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.InternalID;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.IPhotonCommand;
import org.marketcetera.photon.commands.CancelCommand;
import org.marketcetera.photon.commands.MessageCommand;
import org.marketcetera.photon.commands.SendOrderToOrderManagerCommand;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;

public class CommandParser {
	//////////////////////////////////////////////////////////////
	// Lexer infrastructure
	final Parser<?> whitespaceScanner = Scanners.isWhitespaces("whitespaceScanner");

	final Parser<Tok> _commandPrefixTokenLexer = Lexers.getCaseInsensitive(new String[0], CommandImage.getImages()).getLexer();
	final Parser<Tok[]> commandPrefixLexer =  Parsers.map("commandPrefixToArray", whitespaceScanner.optional().seq(_commandPrefixTokenLexer), new ToArrayMap());

	final Parser<Tok> decimalLexer = Lexers.decimal("decimalLexer").followedBy(Parsers.sum(Parsers.eof(), whitespaceScanner));
	final Parser<_> integerScanner = Scanners.isPattern(Patterns.seq(Patterns.optional(Patterns.isChar('-')), Patterns.many(1,CharPredicates.isDigit())), "expected integer");
	final Parser<Tok> integerLexer = Lexers.lexer("integerLexer", Scanners.delimited(integerScanner, "integer"), Tokenizers.forInteger()).followedBy(Parsers.sum(Parsers.eof(), whitespaceScanner));
	// the atomize calls here essentially implement backtracking, that is if the parse fails, it will 
	// back up to the original spot in the parse.  by plus'ing them together we get two tries at the 
	// same spot in the parse.
	final Parser<Tok> numberLexer = Parsers.plus(Parsers.atomize("1",integerLexer),Parsers.atomize("2",decimalLexer));

	final Parser<_> wordScanner = Scanners.isPattern("wordScanner", Patterns.regex("[a-zA-z0-9/.;\\-]").many(1), "expected word");
	final Parser<Tok> wordLexer = Lexers.lexer("wordLexer", wordScanner, Tokenizers.forWord());
	

	final Parser<Tok> tokenLexer = Parsers.plus(numberLexer, wordLexer);

	final Parser<Tok[]> mainLexeme = Lexers.lexeme("mainLexeme",
			whitespaceScanner.many(), tokenLexer).followedBy(Parsers.eof());
	final Parser<Tok[]> wordLexeme = Lexers.lexeme("wordLexeme",
			whitespaceScanner.many(), wordLexer).followedBy(Parsers.eof());

	//////////////////////////////////////////////////////////////
	// Parsers
	final Parser<Object> priceParser = Parsers.token(
			"priceParser", new FromToken<Object>() {
				private static final long serialVersionUID = 625605776498362995L;
				public Object fromToken(Tok tok) {
					String stringImage = ((TypedToken<TokenType>)tok.getToken()).getText().toUpperCase();
					PriceImage pi =  PriceImage.fromName(stringImage);
					if (pi == null){
						try {
							new BigDecimal(stringImage);
						} catch (Exception ex){
							throw new UserException(tok.getIndex(), "Expected price or "+PriceImage.MKT.getImage());
						}
						pi = PriceImage.LIMIT;
						pi.setImage(stringImage);
					}
					return pi;
				}
			});

	final Parser<SideImage> sideImageParser = Parsers.token(
			"sideImageParser", new FromToken<SideImage>() {
				public SideImage fromToken(Tok tok) {
					return SideImage.fromName(((TypedToken<TokenType>)tok.getToken()).getText().toUpperCase());
				}
			});

	final Parser<TimeInForceImage> timeInForceParser = Parsers.token(
			"sideImageParser", new FromToken<TimeInForceImage>() {
				public TimeInForceImage fromToken(Tok tok) {
					return TimeInForceImage.fromName(((TypedToken<TokenType>)tok.getToken()).getText().toUpperCase());
				}
			});
	
	final Parser<CommandImage> commandImageParser = Parsers.token(
			"commandImageParser", new FromToken<CommandImage>() {
				public CommandImage fromToken(Tok tok) {
					return CommandImage.fromName(((TypedToken<TokenType>)tok.getToken()).getText().toUpperCase());
				}
			});

	final Parser<BigDecimal> integerParser = Terms.integerParser(new FromString<BigDecimal>(){
		public BigDecimal fromString(int arg0, int arg1, String arg2) {
			return new BigDecimal(arg2);
		}
	});
	
	final Parser<String> wordParser = Terms.wordParser(new FromString<String>(){
		public String fromString(int arg0, int arg1, String arg2) {
			return arg2;
		}
	});

	final Parser<AccountID> accountParser = Parsers.token("accountIDParser", new FromToken<AccountID>(){
		public AccountID fromToken(Tok tok) {
			return new AccountID(((TypedToken)tok.getToken()).getText());
		}
	});

	final Parser<IPhotonCommand> orderCommandMapper = Parsers.mapn(
			(Parser<Object>[])new Parser[]{sideImageParser, integerParser, wordParser, priceParser, timeInForceParser.optional(), accountParser.optional()} ,
		new Mapn<IPhotonCommand>(){
		  public IPhotonCommand map(Object... vals) {
					int i = 0;
					SideImage sideImage = (SideImage) vals[i++];
					BigDecimal quantity = (BigDecimal) vals[i++];
					String symbol = (String) vals[i++];
					PriceImage priceObject = (PriceImage) vals[i++];
					TimeInForceImage timeInForce = TimeInForceImage.DAY;
					AccountID accountID = null;
					if (vals.length >= i && vals[i] !=null)
						timeInForce = (TimeInForceImage) vals[i++];
					if (vals.length >= i && vals[i] !=null)
						accountID = (AccountID) vals[i++];

					Message message=null;
					try {
						if (PriceImage.MKT.equals(priceObject))	{
							message = FIXMessageUtil.newMarketOrder(new InternalID(factory.getNext()), sideImage.getFIXValue(), quantity, new MSymbol(symbol), timeInForce.getFIXValue(), accountID);
						} else {
							message = FIXMessageUtil.newLimitOrder(new InternalID(factory.getNext()), sideImage.getFIXValue(), quantity, new MSymbol(symbol), new BigDecimal(priceObject.getImage()), timeInForce.getFIXValue(), accountID);
						}
					} catch (NoMoreIDsException e) {
						Application.getMainConsoleLogger().error(this, e);
					}
					return new SendOrderToOrderManagerCommand(message);
				}
			}
	);

	final Parser<IPhotonCommand> cancelMapper = Parsers.map("cancelMapper",wordParser,
			new Map<String,IPhotonCommand>(){
			  public IPhotonCommand map(String word) {
				  return new CancelCommand(word);
			  }
	});

	final Parser<IPhotonCommand> newOrderCommandParser = Parsers.parseTokens("mainParser",mainLexeme,
			orderCommandMapper, "module");

	final Parser<IPhotonCommand> cancelCommandParser = Parsers.parseTokens("mainParser",wordLexeme,
			cancelMapper, "module");
	
	final Parser<IPhotonCommand> mainParser = Parsers.plus(getCommandWithPrefix(CommandImage.ORDER,newOrderCommandParser),
			getCommandWithPrefix(CommandImage.CANCEL,cancelCommandParser));

	private <T> Parser<T> getCommandWithPrefix(CommandImage image, Parser<T> suffixParser){
		String theImage = image.getImage();
		return Parsers.atomize(theImage+"Atom", Parsers.parseTokens(theImage+"PrefixParser",commandPrefixLexer,
				Parsers.token(new IsReserved(theImage)), "module1").seq(suffixParser));
	}

	private IDFactory factory;

	public MessageCommand parseNewOrder(String theInputString) {
		SendOrderToOrderManagerCommand result = (SendOrderToOrderManagerCommand)Parsers.runParser(theInputString, newOrderCommandParser,
		"user input");
		return result;
	}
	
	public IPhotonCommand parseCommand(String theInputString){
		return Parsers.runParser(theInputString, mainParser,
			"user input");
	}
	
	public Tok[] lex(String theInputString){
		return Parsers.runParser(theInputString, mainLexeme, "lex only");
	}
	public void setIDFactory(IDFactory factory) {
		this.factory = factory;
	}
	
}
