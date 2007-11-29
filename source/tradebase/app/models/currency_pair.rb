class CurrencyPair < ActiveRecord::Base
  belongs_to :first_currency,
             :class_name => 'Currency',
             :foreign_key => 'first_currency_id'
  belongs_to :second_currency,
             :class_name => 'Currency',
             :foreign_key => 'second_currency_id'

  has_many :trades, :as => :tradeable
  has_many :positions, :as => :tradeable

  def validate
    errors.add(:first_currency, "unknown") if first_currency.nil?
    errors.add(:second_currency, "unknown") if second_currency.nil?
  end

  def to_s
    "#{first_currency_code}/#{second_currency_code}"
  end

  def first_currency_code
    first_currency.nil? ? "" : first_currency.alpha_code
  end

  def second_currency_code
    second_currency.nil? ? "" : second_currency.alpha_code
  end

  def CurrencyPair.get_currency_pair(symbol, create_missing=true)
    # EUR/USD
    # EURUSD
    matched = /^([A-Z]{3})\/([A-Z]{3})$/.match(symbol)
    if (matched.nil?)
      matched = /^([A-Z]{3})([A-Z]{3})$/.match(symbol)
      if (matched.nil?)
        raise "Illegal currency pair symbol, #{symbol}"
      end
    end
    first_currency_code = matched[1]
    second_currency_code = matched[2] 
    first_currency = get_currency(first_currency_code, create_missing)
    second_currency = get_currency(second_currency_code, create_missing)
    currency_pair = CurrencyPair.find(:first, :conditions => { :first_currency_id => first_currency, :second_currency_id => second_currency } )
    currency_pair = CurrencyPair.create(:first_currency => first_currency, :second_currency => second_currency) if (currency_pair.nil? && create_missing) 
    currency_pair
  end

  def CurrencyPair.get_currency(currency_code, create_missing)
    currency = Currency.find_by_alpha_code(currency_code)
    currency = Currency.create(:alpha_code => currency_code) if (currency.nil? && create_missing)
    currency
  end

end
