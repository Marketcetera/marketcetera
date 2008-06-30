class CurrencyPair < ActiveRecord::Base
  belongs_to :first_currency,
             :class_name => 'Currency',
             :foreign_key => 'first_currency_id'
  belongs_to :second_currency,
             :class_name => 'Currency',
             :foreign_key => 'second_currency_id'

  has_many :trades, :as => :tradeable
  has_many :positions, :as => :tradeable
  has_many :marks, :as => :tradeable

  # We don't want duplicate currency pairs
  validates_uniqueness_of :first_currency_id, :scope => [:second_currency_id]

  def validate
    errors.add(:first_currency, "unknown") if first_currency.nil?
    errors.add(:second_currency, "unknown") if second_currency.nil?
  end

  def to_s
    "#{first_currency_code}/#{second_currency_code}"
  end
  
  def m_symbol_root
    to_s
  end

  def first_currency_code
    first_currency.nil? ? "" : first_currency.alpha_code
  end

  def second_currency_code
    second_currency.nil? ? "" : second_currency.alpha_code
  end

  # throws an exception if one of the underlying currencies is not present
  def CurrencyPair.get_currency_pair(symbol, create_missing = false)
    # EUR/USD
    # EURUSD
    # eur/usd

    # case-insensitive
    symbol = (symbol.nil?) ? nil : symbol.upcase
    matched = /^([A-Z]{3})\/([A-Z]{3})$/.match(symbol)
    if (matched.nil?)
      matched = /^([A-Z]{3})([A-Z]{3})$/.match(symbol)
      if (matched.nil?)
        raise UnknownCurrencyPairException.new("Illegal currency pair symbol: #{symbol}")
      end
    end
    first_currency_code = matched[1]
    second_currency_code = matched[2] 
    first_currency = Currency.get_currency(first_currency_code)
    second_currency = Currency.get_currency(second_currency_code)
    currency_pair = CurrencyPair.find(:first, :conditions => { :first_currency_id => first_currency, :second_currency_id => second_currency } )
    currency_pair = CurrencyPair.create(:first_currency => first_currency, :second_currency => second_currency) if (currency_pair.nil? && create_missing)

    raise UnknownCurrencyPairException.new("Unknown currency in pair: #{symbol}") if (!currency_pair.nil? && !currency_pair.valid?)

    currency_pair
  end

  # Helper function to find missing denominator/USD pairs
  # Run it after modifying/adding more currency pairs
  def CurrencyPair.find_missing_pairs
    all = CurrencyPair.find_all
    usd = Currency.get_currency("USD")
    all.each {|p|
      if(p.second_currency != usd)
        top = CurrencyPair.get_currency_pair(p.second_currency.to_s+"usd", false)
        bottom = CurrencyPair.get_currency_pair("usd"+p.second_currency.to_s, false)
        if(top ==nil && bottom==nil)
          puts "missing #{p.second_currency.to_s}/usd combo"
        else
          #puts "for #{p.to_s} found #{top.to_s} and #{bottom}"  
        end
      end
    }
  end
end
