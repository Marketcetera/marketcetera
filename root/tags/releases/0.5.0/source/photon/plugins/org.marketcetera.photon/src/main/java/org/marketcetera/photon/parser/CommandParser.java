package org.marketcetera.photon.parser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jfun.parsec.FromString;
import jfun.parsec.FromToken;
import jfun.parsec.Lexers;
import jfun.parsec.Map;
import jfun.parsec.Map2;
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

import org.marketcetera.core.IDFactory;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.IPhotonCommand;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.commands.CancelCommand;
import org.marketcetera.photon.commands.MessageCommand;
import org.marketcetera.photon.commands.SendOrderToOrderManagerCommand;
import org.marketcetera.photon.views.OptionDateHelper;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.DataDictionary;
import quickfix.FieldMap;
import quickfix.FieldType;
import quickfix.Message;
import quickfix.field.MaturityMonthYear;
import quickfix.field.OpenClose;
import quickfix.field.OrderQty;
import quickfix.field.PutOrCall;
import quickfix.field.SecurityType;
import quickfix.field.StrikePrice;

public class CommandParser {
	
	public static final Pattern optionExpirationPattern = Pattern.compile("([0-9]{2}|[0-9]{4})?([a-zA-Z]+)([0-9]+|[0-9]*\\.[0-9]+)(C|P)");
	
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

	final String WORD_CHARS = "[a-zA-z0-9/.;\\-+]";
	final Parser<_> wordScanner = Scanners.isPattern("wordScanner", Patterns.regex(WORD_CHARS).many(1), "expected word");
	final Parser<Tok> wordLexer = Lexers.lexer("wordLexer", wordScanner, Tokenizers.forWord());
	final Pattern optionSymbolPattern = Pattern.compile("([a-zA-z]{1,3})\\+([a-zA-z])([a-zA-z])(\\."+WORD_CHARS+")?");

	final Parser<Tok> tokenLexer = Parsers.plus(numberLexer, wordLexer);

	final Parser<Tok[]> mainLexeme = Lexers.lexeme("mainLexeme",
			whitespaceScanner.many(), tokenLexer).followedBy(Parsers.eof());
	final Parser<Tok[]> wordLexeme = Lexers.lexeme("wordLexeme",
			whitespaceScanner.many(), wordLexer).followedBy(Parsers.eof());
	final Parser<Tok[]> numberLexeme = Lexers.lexeme("numberLexeme", 
			whitespaceScanner.many(), numberLexer).followedBy(Parsers.eof());
	
	OptionDateHelper optionDateHelper = new OptionDateHelper();
	
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

	final Parser<FieldMap> optionExpirationParser = Parsers.token(
			"optionExpirationParser", new FromToken<FieldMap>() {
				private static final long serialVersionUID = 625605776498362995L;
				public FieldMap fromToken(Tok tok) {
					String stringImage = ((TypedToken<TokenType>)tok.getToken()).getText();
					Matcher matcher = optionExpirationPattern.matcher(stringImage);
					if (matcher.matches()){
						String expirationYearString = matcher.group(1);
						expirationYearString = (expirationYearString!=null && expirationYearString.length()==2) ? "20"+expirationYearString : expirationYearString;
						String expirationMonthString = matcher.group(2);
						String strikeString = matcher.group(3);
						String callPutString = matcher.group(4);
						int expirationMonth = optionDateHelper.getMonthNumber(expirationMonthString);
						int expirationYear;
						if (expirationYearString != null){
							expirationYear = Integer.parseInt(expirationYearString);
						} else {
							expirationYear = optionDateHelper.calculateYearFromMonth(expirationMonth);	
						}
						String maturityMonthYearString = optionDateHelper.formatMaturityMonthYear(expirationMonth, expirationYear);
						String putOrCall = PutOrCallImage.fromName(callPutString).getFIXStringValue();
						FieldMap results = new org.marketcetera.photon.parser.FieldMap();
						results.setString(MaturityMonthYear.FIELD, maturityMonthYearString);
						results.setString(StrikePrice.FIELD, strikeString);
						results.setString(PutOrCall.FIELD, putOrCall);
						results.setString(SecurityType.FIELD, SecurityType.OPTION);
						return results;
					} else {
						return null;
					}
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

	final Parser<BigInteger> integerParser = Terms.integerParser(new FromString<BigInteger>(){
		public BigInteger fromString(int arg0, int arg1, String arg2) {
			return new BigInteger(arg2);
		}
	});
	
	final Parser<BigDecimal> orderQtyParser = Terms.decimalParser(new FromString<BigDecimal>(){
		public BigDecimal fromString(int arg0, int arg1, String arg2) {
			if (orderQtyIsInt){
				// throw an exception if it cannot be parsed as an int
				new BigInteger(arg2);
			}
			return new BigDecimal(arg2);
		}
	});
	
	final Parser<String> wordParser = Terms.wordParser(new FromString<String>(){
		public String fromString(int arg0, int arg1, String arg2) {
			return arg2;
		}
	});

	final Parser<String> accountParser = Parsers.token("accountIDParser", new FromToken<String>(){
		public String fromToken(Tok tok) {
			return ((TypedToken)tok.getToken()).getText();
		}
	});

	final Parser<IPhotonCommand> resendRequestMapper = Parsers.map2(
			integerParser, integerParser,
			new Map2<BigInteger, BigInteger, IPhotonCommand>() {
				public IPhotonCommand map(BigInteger arg0, BigInteger arg1) {
					return new SendOrderToOrderManagerCommand(messageFactory.newResendRequest(arg0, arg1));
				}
			});
	
	final Parser<IPhotonCommand> orderCommandMapper = Parsers.mapn(
			(Parser<Object>[])new Parser[]{sideImageParser, orderQtyParser, wordParser, 
					Parsers.atomize("optionExpirationParserAtom", optionExpirationParser).optional(), 
					priceParser, timeInForceParser.optional(), accountParser.optional()} ,
		new Mapn<IPhotonCommand>(){
		  public IPhotonCommand map(Object... vals) {
					int i = 0;
					SideImage sideImage = (SideImage) vals[i++];
					BigDecimal quantity = (BigDecimal) vals[i++];
					String symbol = (String) vals[i++];
					FieldMap optionSpecifier = (FieldMap) vals[i++];
					PriceImage priceImage = (PriceImage) vals[i++];
					
					TimeInForceImage timeInForce = TimeInForceImage.DAY;
					String accountID = null;
					if (vals.length > i && vals[i] != null) {
						timeInForce = (TimeInForceImage) vals[i];
						i++;
						if (vals.length >= i && vals[i] !=null)
							accountID = (String) vals[i];
					} else if (vals.length > i+1 && vals[i+1] != null){
						throw new jfun.parsec.UserException("Missing time-in-force");
					}
					Message message=null;
					try {
						if (PriceImage.MKT.equals(priceImage))	{
							message = messageFactory.newMarketOrder(idFactory.getNext(), sideImage.getFIXCharValue(), quantity, new MSymbol(symbol), timeInForce.getFIXValue(), accountID);
						} else {
							message = messageFactory.newLimitOrder(idFactory.getNext(), sideImage.getFIXCharValue(), quantity, new MSymbol(symbol), new BigDecimal(priceImage.getImage()), timeInForce.getFIXValue(), accountID);
						}
						if (optionSpecifier != null || optionSymbolPattern.matcher(symbol).matches()){
							if (optionSpecifier != null){
								FIXMessageUtil.copyFields(message,optionSpecifier);
							} else {
								message.setString(SecurityType.FIELD, SecurityType.OPTION);
							}
							if (dataDictionary.isField(OpenClose.FIELD)){
								message.setField(new OpenClose(OpenClose.OPEN));
							}
						} else {
							message.setString(SecurityType.FIELD, SecurityType.COMMON_STOCK);
						}
					} catch (NoMoreIDsException e) {
						PhotonPlugin.getMainConsoleLogger().error(this, e);
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
	
	final Parser<IPhotonCommand> resendRequestCommandParser = Parsers.parseTokens("mainParser",numberLexeme,
			resendRequestMapper, "module");

	final Parser<IPhotonCommand> mainParser = Parsers.plus(
			getCommandWithPrefix(CommandImage.ORDER,newOrderCommandParser),
			getCommandWithPrefix(CommandImage.CANCEL,cancelCommandParser),
			getCommandWithPrefix(CommandImage.RESEND_REQUEST,resendRequestCommandParser)
			);

	private <T> Parser<T> getCommandWithPrefix(CommandImage image, Parser<T> suffixParser){
		String theImage = image.getImage();
		return Parsers.atomize(theImage+"Atom", Parsers.parseTokens(theImage+"PrefixParser",commandPrefixLexer,
				Parsers.token(new IsReserved(theImage)), "module1").seq(suffixParser));
	}

	private IDFactory idFactory;

	private FIXMessageFactory messageFactory;

	private DataDictionary dataDictionary;

	private boolean orderQtyIsInt = false;

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
		this.idFactory = factory;
	}

	public void setMessageFactory(FIXMessageFactory factory) {
		this.messageFactory = factory;
	}
	public void setDataDictionary(DataDictionary dd) {
		this.dataDictionary = dd;
		orderQtyIsInt = FieldType.Int == dataDictionary.getFieldTypeEnum(OrderQty.FIELD);
	}

}
