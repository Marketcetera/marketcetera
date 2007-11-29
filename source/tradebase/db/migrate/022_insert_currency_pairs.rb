class InsertCurrencyPairs < ActiveRecord::Migration
  def self.up
    CurrencyPair.new(
	:first_currency_id => Currency.find_by_alpha_code("EUR").id,
	:second_currency_id => Currency.find_by_alpha_code("USD").id,
	:description => "Euro").save()

    CurrencyPair.new(
	:first_currency_id => Currency.find_by_alpha_code("GBP").id,
	:second_currency_id => Currency.find_by_alpha_code("USD").id,
	:description => "Cable").save()

    CurrencyPair.new(
	:first_currency_id => Currency.find_by_alpha_code("USD").id,
	:second_currency_id => Currency.find_by_alpha_code("JPY").id,
	:description => "Gopher").save()

    CurrencyPair.new(
	:first_currency_id => Currency.find_by_alpha_code("USD").id,
	:second_currency_id => Currency.find_by_alpha_code("CHF").id,
	:description => "Swissy").save()

    CurrencyPair.new(
	:first_currency_id => Currency.find_by_alpha_code("AUD").id,
	:second_currency_id => Currency.find_by_alpha_code("USD").id,
	:description => "Aussie").save()

    CurrencyPair.new(
	:first_currency_id => Currency.find_by_alpha_code("USD").id,
	:second_currency_id => Currency.find_by_alpha_code("CAD").id,
	:description => "Loonie").save()

    CurrencyPair.new(
	:first_currency_id => Currency.find_by_alpha_code("GBP").id,
	:second_currency_id => Currency.find_by_alpha_code("JPY").id,
	:description => "Geppy").save()

    CurrencyPair.new(
	:first_currency_id => Currency.find_by_alpha_code("NZD").id,
	:second_currency_id => Currency.find_by_alpha_code("USD").id,
	:description => "Kiwi").save()
  end

  def self.down
    #Currency.delete_all
  end
end
