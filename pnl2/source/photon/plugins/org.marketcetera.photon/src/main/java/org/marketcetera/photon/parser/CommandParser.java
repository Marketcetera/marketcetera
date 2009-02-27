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
import jfun.parsec.tokens.Tokenizers;
import jfun.parsec.tokens.TypedToken;

import org.marketcetera.photon.IBrokerIdValidator;
import org.marketcetera.photon.IPhotonCommand;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.commands.CancelCommand;
import org.marketcetera.photon.commands.MessageCommand;
import org.marketcetera.photon.commands.SendOrderToOrderManagerCommand;
import org.marketcetera.photon.views.OptionDateHelper;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.MSymbol;

import quickfix.DataDictionary;
import quickfix.FieldMap;
import quickfix.FieldType;
import quickfix.Message;
import quickfix.field.MaturityMonthYear;
import quickfix.field.OrderQty;
import quickfix.field.PutOrCall;
import quickfix.field.SecurityType;
import quickfix.field.StrikePrice;

public class CommandParser
    implements Messages
{
	
	public static final Pattern optionExpirationPattern = Pattern.compile("([0-9]{2}|[0-9]{4})?([a-zA-Z]+)([0-9]+|[0-9]*\\.[0-9]+)(C|P)"); //$NON-NLS-1$
	
	//////////////////////////////////////////////////////////////
	// Lexer infrastructure
	final Parser<?> whitespaceScanner = Scanners.isWhitespaces("whitespaceScanner"); //$NON-NLS-1$

	final Parser<Tok> _commandPrefixTokenLexer = Lexers.getCaseInsensitive(new String[0], CommandImage.getImages()).getLexer();
	final Parser<Tok[]> commandPrefixLexer =  Parsers.map("commandPrefixToArray", whitespaceScanner.optional().seq(_commandPrefixTokenLexer), new ToArrayMap()); //$NON-NLS-1$

	final Parser<Tok> decimalLexer = Lexers.decimal("decimalLexer").followedBy(Parsers.sum(Parsers.eof(), whitespaceScanner)); //$NON-NLS-1$
	final Parser<_> integerScanner = Scanners.isPattern(Patterns.seq(Patterns.optional(Patterns.isChar('-')), Patterns.many(1,CharPredicates.isDigit())), "expected integer"); //$NON-NLS-1$
	final Parser<Tok> integerLexer = Lexers.lexer("integerLexer", Scanners.delimited(integerScanner, "integer"), Tokenizers.forInteger()).followedBy(Parsers.sum(Parsers.eof(), whitespaceScanner)); //$NON-NLS-1$ //$NON-NLS-2$
	// the atomize calls here essentially implement backtracking, that is if the parse fails, it will 
	// back up to the original spot in the parse.  by plus'ing them together we get two tries at the 
	// same spot in the parse.
	final Parser<Tok> numberLexer = Parsers.plus(Parsers.atomize("1",integerLexer),Parsers.atomize("2",decimalLexer)); //$NON-NLS-1$ //$NON-NLS-2$

	final String WORD_CHARS = "[a-zA-z0-9/.;\\-+]"; //$NON-NLS-1$
	final Parser<_> wordScanner = Scanners.isPattern("wordScanner", Patterns.regex(WORD_CHARS).many(1), "expected word"); //$NON-NLS-1$ //$NON-NLS-2$
	final Parser<Tok> wordLexer = Lexers.lexer("wordLexer", wordScanner, Tokenizers.forWord()); //$NON-NLS-1$
	final Pattern optionSymbolPattern = Pattern.compile("([a-zA-z]{1,3})\\+([a-zA-z])([a-zA-z])(\\."+WORD_CHARS+")?"); //$NON-NLS-1$ //$NON-NLS-2$

	final Parser<Tok> tokenLexer = Parsers.plus(numberLexer, wordLexer);

	final Parser<Tok[]> mainLexeme = Lexers.lexeme("mainLexeme", //$NON-NLS-1$
			whitespaceScanner.many(), tokenLexer).followedBy(Parsers.eof());
	final Parser<Tok[]> wordLexeme = Lexers.lexeme("wordLexeme", //$NON-NLS-1$
			whitespaceScanner.many(), wordLexer).followedBy(Parsers.eof());
	final Parser<Tok[]> numberLexeme = Lexers.lexeme("numberLexeme",  //$NON-NLS-1$
			whitespaceScanner.many(), numberLexer).followedBy(Parsers.eof());
	
	OptionDateHelper optionDateHelper = new OptionDateHelper();
	
	//////////////////////////////////////////////////////////////
	// Parsers
	final Parser<Object> priceParser = Parsers.token(
			"priceParser", new FromToken<Object>() { //$NON-NLS-1$
				private static final long serialVersionUID = 625605776498362995L;
                public Object fromToken(Tok tok) {
					String stringImage = ((TypedToken<?>)tok.getToken()).getText().toUpperCase();
					PriceImage.LIMIT.setImage("0"); //$NON-NLS-1$
					PriceImage pi =  PriceImage.fromName(stringImage);
					if (pi == null){
						try {
							new BigDecimal(stringImage);
						} catch (Exception ex){
							throw new UserException(tok.getIndex(),
							                        EXPECTED_PRICE.getText(PriceImage.MKT.getImage()));
						}
						pi = PriceImage.LIMIT;
						pi.setImage(stringImage);
					}
					return pi;
				}
			});

	final Parser<FieldMap> optionExpirationParser = Parsers.token(
			"optionExpirationParser", new FromToken<FieldMap>() { //$NON-NLS-1$
				private static final long serialVersionUID = 625605776498362995L;
                public FieldMap fromToken(Tok tok) {
					String stringImage = ((TypedToken<?>)tok.getToken()).getText();
					Matcher matcher = optionExpirationPattern.matcher(stringImage);
					if (matcher.matches()){
						String expirationYearString = matcher.group(1);
						expirationYearString = (expirationYearString!=null && expirationYearString.length()==2) ? "20"+expirationYearString : expirationYearString; //$NON-NLS-1$
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
			"sideImageParser", new FromToken<SideImage>() { //$NON-NLS-1$
                private static final long serialVersionUID = 1L;
                public SideImage fromToken(Tok tok) {
					return SideImage.fromName(((TypedToken<?>)tok.getToken()).getText().toUpperCase());
				}
			});

	final Parser<TimeInForceImage> timeInForceParser = Parsers.token(
			"sideImageParser", new FromToken<TimeInForceImage>() { //$NON-NLS-1$
                private static final long serialVersionUID = 1L;
                public TimeInForceImage fromToken(Tok tok) {
					return TimeInForceImage.fromName(((TypedToken<?>)tok.getToken()).getText().toUpperCase());
				}
			});
	
	final Parser<CommandImage> commandImageParser = Parsers.token(
			"commandImageParser", new FromToken<CommandImage>() { //$NON-NLS-1$
                private static final long serialVersionUID = 1L;
                public CommandImage fromToken(Tok tok) {
					return CommandImage.fromName(((TypedToken<?>)tok.getToken()).getText().toUpperCase());
				}
			});

	final Parser<BigInteger> integerParser = Terms.integerParser(new FromString<BigInteger>(){
        private static final long serialVersionUID = 1L;
        public BigInteger fromString(int arg0, int arg1, String arg2) {
			return new BigInteger(arg2);
		}
	});
	
	final Parser<BigDecimal> orderQtyParser = Terms.decimalParser(new FromString<BigDecimal>(){
        private static final long serialVersionUID = 1L;
        public BigDecimal fromString(int arg0, int arg1, String arg2) {
			if (orderQtyIsInt){
				// throw an exception if it cannot be parsed as an int
				new BigInteger(arg2);
			}
			return new BigDecimal(arg2);
		}
	});
	
	final Parser<String> wordParser = Terms.wordParser(new FromString<String>(){
        private static final long serialVersionUID = 1L;
        public String fromString(int arg0, int arg1, String arg2) {
			return arg2;
		}
	});

	final Parser<String> accountParser = Parsers.token("accountIDParser", new FromToken<String>(){ //$NON-NLS-1$
        private static final long serialVersionUID = 1L;
        public String fromToken(Tok tok) {
			return ((TypedToken<?>)tok.getToken()).getText();
		}
	});

	final Parser<String> brokerParser = Parsers.token("brokerParser", new FromToken<String>(){ //$NON-NLS-1$
        private static final long serialVersionUID = 1L;
        public String fromToken(Tok tok) {
			return ((TypedToken<?>)tok.getToken()).getText();
		}
	});

	final Parser<IPhotonCommand> resendRequestMapper = Parsers.map2(
			integerParser, integerParser,
			new Map2<BigInteger, BigInteger, IPhotonCommand>() {
                private static final long serialVersionUID = 1L;
                public IPhotonCommand map(BigInteger arg0, BigInteger arg1) {
					return new SendOrderToOrderManagerCommand(messageFactory.newResendRequest(arg0, arg1));
				}
			});
	
	final Parser<IPhotonCommand> orderCommandMapper = Parsers.mapn(
			(Parser<?>[])new Parser<?>[]{sideImageParser, orderQtyParser, wordParser, 
					Parsers.atomize("optionExpirationParserAtom", optionExpirationParser).optional(),  //$NON-NLS-1$
					priceParser, brokerParser.optional(), timeInForceParser.optional(), accountParser.optional()} ,
		new Mapn<IPhotonCommand>(){
            private static final long serialVersionUID = 1L;
        public IPhotonCommand map(Object... vals) {
					int i = 0;
					SideImage sideImage = (SideImage) vals[i++];
					BigDecimal quantity = (BigDecimal) vals[i++];
					String symbol = (String) vals[i++];
					FieldMap optionSpecifier = (FieldMap) vals[i++];
					PriceImage priceImage = (PriceImage) vals[i++];
					String broker = null;
					TimeInForceImage timeInForce = TimeInForceImage.DAY;
					String accountID = null;
					if (vals.length > i && vals[i] != null) {
						broker = (String) vals[i++];
						if (Messages.COMMAND_PARSER_AUTO_SELECT_BROKER_KEYWORD.getText().equals(broker)) {
							broker = null;
						} else if (brokerIdValidator != null && !brokerIdValidator.isValid(broker)) {
							throw new jfun.parsec.UserException(COMMAND_PARSER_INVALID_BROKER_ID.getText(broker, COMMAND_PARSER_AUTO_SELECT_BROKER_KEYWORD.getText()));
						}
						if (vals.length > i && vals[i] != null) {
							timeInForce = (TimeInForceImage) vals[i++];
							if (vals.length > i && vals[i] !=null) {
								accountID = (String) vals[i];
							}
						} else if (vals.length > i+1 && vals[i+1] != null){
							throw new jfun.parsec.UserException(MISSING_TIME_IN_FORCE.getText());
						}
					}
					
					Message message=null;
					if (PriceImage.MKT.equals(priceImage))	{
						message = messageFactory.newMarketOrder("", //$NON-NLS-1$ 
								sideImage.getFIXCharValue(), quantity, new MSymbol(symbol), timeInForce.getFIXValue(), accountID);
					} else {
						message = messageFactory.newLimitOrder("", //$NON-NLS-1$
								sideImage.getFIXCharValue(), quantity, new MSymbol(symbol), new BigDecimal(priceImage.getImage()), timeInForce.getFIXValue(), accountID);
					}
					if (optionSpecifier != null || optionSymbolPattern.matcher(symbol).matches()){
						if (optionSpecifier != null){
							FIXMessageUtil.copyFields(message,optionSpecifier);
						} else {
							message.setString(SecurityType.FIELD, SecurityType.OPTION);
						}
					} else {
						message.setString(SecurityType.FIELD, SecurityType.COMMON_STOCK);
					}
					return new SendOrderToOrderManagerCommand(message, broker);
				}
			}
	);

	final Parser<IPhotonCommand> cancelMapper = Parsers.map("cancelMapper",wordParser, //$NON-NLS-1$
			new Map<String,IPhotonCommand>(){
                private static final long serialVersionUID = 1L;
            public IPhotonCommand map(String word) {
				  return new CancelCommand(word);
			  }
	});

	final Parser<IPhotonCommand> newOrderCommandParser = Parsers.parseTokens("mainParser",mainLexeme, //$NON-NLS-1$
			orderCommandMapper, "module"); //$NON-NLS-1$

	final Parser<IPhotonCommand> cancelCommandParser = Parsers.parseTokens("mainParser",wordLexeme, //$NON-NLS-1$
			cancelMapper, "module"); //$NON-NLS-1$
	
	final Parser<IPhotonCommand> resendRequestCommandParser = Parsers.parseTokens("mainParser",numberLexeme, //$NON-NLS-1$
			resendRequestMapper, "module"); //$NON-NLS-1$

	final Parser<IPhotonCommand> mainParser = Parsers.plus(
			getCommandWithPrefix(CommandImage.ORDER,newOrderCommandParser),
			getCommandWithPrefix(CommandImage.CANCEL,cancelCommandParser),
			getCommandWithPrefix(CommandImage.RESEND_REQUEST,resendRequestCommandParser)
			);

	private <T> Parser<T> getCommandWithPrefix(CommandImage image, Parser<T> suffixParser){
		String theImage = image.getImage();
		return Parsers.atomize(theImage+"Atom", Parsers.parseTokens(theImage+"PrefixParser",commandPrefixLexer, //$NON-NLS-1$ //$NON-NLS-2$
				Parsers.token(new IsReserved(theImage)), "module1").seq(suffixParser)); //$NON-NLS-1$
	}

	private FIXMessageFactory messageFactory;

	private DataDictionary dataDictionary;

	private boolean orderQtyIsInt = false;

	private IBrokerIdValidator brokerIdValidator;

	public MessageCommand parseNewOrder(String theInputString) {
		SendOrderToOrderManagerCommand result = (SendOrderToOrderManagerCommand)Parsers.runParser(theInputString, newOrderCommandParser,
		"user input"); //$NON-NLS-1$
		return result;
	}
	
	public IPhotonCommand parseCommand(String theInputString){
		return Parsers.runParser(theInputString, mainParser,
			"user input"); //$NON-NLS-1$
	}
	
	public Tok[] lex(String theInputString){
		return Parsers.runParser(theInputString, mainLexeme, "lex only"); //$NON-NLS-1$
	}
	public void setMessageFactory(FIXMessageFactory factory) {
		this.messageFactory = factory;
	}
	public void setDataDictionary(DataDictionary dd) {
		this.dataDictionary = dd;
		orderQtyIsInt = FieldType.Int == dataDictionary.getFieldTypeEnum(OrderQty.FIELD);
	}
	public void setBrokerValidator(IBrokerIdValidator validator) {
		this.brokerIdValidator = validator;
	}
	
}
