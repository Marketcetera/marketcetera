-- A: asset		DEBIT=increase	CREDIT=decrease
-- L: liability		DEBIT=decrease	CREDIT=increase
-- E: expense		DEBIT=increase	CREDIT=decrease
-- R: revenue		DEBIT=decrease	CREDIT=increase


-- Create an "account"
INSERT INTO accounts (institution_identifier, nickname, description, created_on, updated_on) VALUES ("GS001", "Bollinger", "Goldman Equities 001", NOW(), NOW() );
INSERT INTO sub_accounts (account_id, sub_account_type_id) VALUES (1,1); -- short term investment
INSERT INTO sub_accounts (account_id, sub_account_type_id) VALUES (1,2); -- cash
INSERT INTO sub_accounts (account_id, sub_account_type_id) VALUES (1,3); -- dividend revenue
INSERT INTO sub_accounts (account_id, sub_account_type_id) VALUES (1,4); -- unrealized gain/loss
INSERT INTO sub_accounts (account_id, sub_account_type_id) VALUES (1,5); -- change on close of investment
INSERT INTO sub_accounts (account_id, sub_account_type_id) VALUES (1,6); -- commissions

-- Create an "equity"
INSERT INTO m_symbols (root, bloomberg, reuters, created_on, updated_on) VALUES ("IBM", "IBM Equity", "IBM", NOW(), NOW() );
INSERT INTO equities (m_symbol_id, created_on, updated_on) VALUES (1, NOW(), NOW() );

-- Create a "buy transaction"
INSERT INTO journals (description, post_date, created_on, updated_on) VALUES ("Purchase of 100 shares of IBM for 81.25",DATE_ADD(NOW(), INTERVAL -2 DAY), NOW(), NOW() );
INSERT INTO postings (sub_account_id, journal_id, currency_id, quantity, created_on, updated_on) VALUES (1,1, 154, 8125, NOW(), NOW() ); -- short term investment (A)
INSERT INTO postings (sub_account_id, journal_id, currency_id, quantity, created_on, updated_on) VALUES (2,1, 154, -8125, NOW(), NOW() ); -- cash (A)
INSERT INTO postings (sub_account_id, journal_id, currency_id, quantity, created_on, updated_on) VALUES (2,1, 154, -8, NOW(), NOW() ); -- cash (A)
INSERT INTO postings (sub_account_id, journal_id, currency_id, quantity, created_on, updated_on) VALUES (6,1, 154, 8, NOW(), NOW() ); -- commissions (E)
INSERT INTO trades (journal_id, asset_type, asset_id, quantity, account_id, trade_type, created_on, updated_on) VALUES (1, "E", 1, 100, 1, "T", NOW(), NOW() );


-- Create a "dividend transaction"
INSERT INTO journals (description, post_date, created_on, updated_on) VALUES ("IBM pays .25 cent dividend on 100 shares",DATE_ADD(NOW(), INTERVAL -1 DAY), NOW(), NOW() );
INSERT INTO postings (sub_account_id, journal_id, currency_id, quantity, created_on, updated_on) VALUES (2,2, 154, 25, NOW(), NOW() ); -- cash account (A)
INSERT INTO postings (sub_account_id, journal_id, currency_id, quantity, created_on, updated_on) VALUES (3,2, 154, 25, NOW(), NOW() ); -- dividend revenue (R)

-- Create a "sell transaction"
-- Notes about sell transaction:
-- Amount credited to short term investment should be "average cost", FIFO, or LIFO?
INSERT INTO journals (description, post_date, created_on, updated_on) VALUES ("Sale of 100 shares of IBM at 82.25",NOW(), NOW(), NOW() );
INSERT INTO postings (sub_account_id, journal_id, currency_id, quantity, created_on, updated_on) VALUES (2,3, 154, 8225, NOW(), NOW() ); -- cash (A)
INSERT INTO postings (sub_account_id, journal_id, currency_id, quantity, created_on, updated_on) VALUES (1,3, 154, -8125, NOW(), NOW() ); -- short term investment (A)
INSERT INTO postings (sub_account_id, journal_id, currency_id, quantity, created_on, updated_on) VALUES (5,3, 154, 100, NOW(), NOW() ); -- Change on Close of Investment (R)
INSERT INTO postings (sub_account_id, journal_id, currency_id, quantity, created_on, updated_on) VALUES (2,3, 154, -8, NOW(), NOW() ); -- cash (A)
INSERT INTO postings (sub_account_id, journal_id, currency_id, quantity, created_on, updated_on) VALUES (6,3, 154, 8, NOW(), NOW() ); -- commissions (E)
INSERT INTO trades (journal_id, asset_type, asset_id, quantity, account_id, trade_type, created_on, updated_on) VALUES (1, "E", 1, -100, 1, "T", NOW(), NOW() );

