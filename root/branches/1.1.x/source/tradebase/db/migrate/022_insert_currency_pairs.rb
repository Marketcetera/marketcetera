class InsertCurrencyPairs < ActiveRecord::Migration
    def self.up
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("AUD").id,
                        :second_currency_id => Currency.find_by_alpha_code("CAD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("AUD").id,
                        :second_currency_id => Currency.find_by_alpha_code("CHF").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("AUD").id,
                        :second_currency_id => Currency.find_by_alpha_code("JPY").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("AUD").id,
                        :second_currency_id => Currency.find_by_alpha_code("NZD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("AUD").id,
                        :second_currency_id => Currency.find_by_alpha_code("SGD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("AUD").id,
                        :second_currency_id => Currency.find_by_alpha_code("USD").id,
                        :description => "Aussie").save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("CAD").id,
                        :second_currency_id => Currency.find_by_alpha_code("CHF").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("CAD").id,
                        :second_currency_id => Currency.find_by_alpha_code("JPY").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("CAD").id,
                        :second_currency_id => Currency.find_by_alpha_code("SGD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("CHF").id,
                        :second_currency_id => Currency.find_by_alpha_code("JPY").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("CHF").id,
                        :second_currency_id => Currency.find_by_alpha_code("NOK").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("CHF").id,
                        :second_currency_id => Currency.find_by_alpha_code("PLN").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("CHF").id,
                        :second_currency_id => Currency.find_by_alpha_code("SGD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("AED").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("AUD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("BHD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("CAD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("CHF").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("CZK").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("DKK").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("GBP").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("HKD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("HUF").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("ILS").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("INR").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("JPY").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("KWD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("MXN").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("NOK").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("NZD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("OMR").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("PLN").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("SAR").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("SEK").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("SGD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("SKK").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("THB").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("TRY").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("USD").id,
                        :description => "Euro").save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("EUR").id,
                        :second_currency_id => Currency.find_by_alpha_code("ZAR").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("GBP").id,
                        :second_currency_id => Currency.find_by_alpha_code("AUD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("GBP").id,
                        :second_currency_id => Currency.find_by_alpha_code("CAD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("GBP").id,
                        :second_currency_id => Currency.find_by_alpha_code("CHF").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("GBP").id,
                        :second_currency_id => Currency.find_by_alpha_code("DKK").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("GBP").id,
                        :second_currency_id => Currency.find_by_alpha_code("HUF").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("GBP").id,
                        :second_currency_id => Currency.find_by_alpha_code("ILS").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("GBP").id,
                        :second_currency_id => Currency.find_by_alpha_code("JPY").id,
                        :description => "Geppy").save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("GBP").id,
                        :second_currency_id => Currency.find_by_alpha_code("NOK").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("GBP").id,
                        :second_currency_id => Currency.find_by_alpha_code("NZD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("GBP").id,
                        :second_currency_id => Currency.find_by_alpha_code("PLN").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("GBP").id,
                        :second_currency_id => Currency.find_by_alpha_code("SEK").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("GBP").id,
                        :second_currency_id => Currency.find_by_alpha_code("SGD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("GBP").id,
                        :second_currency_id => Currency.find_by_alpha_code("USD").id,
                        :description => "Cable").save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("HKD").id,
                        :second_currency_id => Currency.find_by_alpha_code("JPY").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("NOK").id,
                        :second_currency_id => Currency.find_by_alpha_code("JPY").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("NOK").id,
                        :second_currency_id => Currency.find_by_alpha_code("SEK").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("NZD").id,
                        :second_currency_id => Currency.find_by_alpha_code("CAD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("NZD").id,
                        :second_currency_id => Currency.find_by_alpha_code("CHF").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("NZD").id,
                        :second_currency_id => Currency.find_by_alpha_code("DKK").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("NZD").id,
                        :second_currency_id => Currency.find_by_alpha_code("JPY").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("NZD").id,
                        :second_currency_id => Currency.find_by_alpha_code("SEK").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("NZD").id,
                        :second_currency_id => Currency.find_by_alpha_code("SGD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("NZD").id,
                        :second_currency_id => Currency.find_by_alpha_code("USD").id,
                        :description => "Kiwi").save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("SEK").id,
                        :second_currency_id => Currency.find_by_alpha_code("JPY").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("SGD").id,
                        :second_currency_id => Currency.find_by_alpha_code("JPY").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("AED").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("BHD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("CAD").id,
                        :description => "Loonie").save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("CHF").id,
                        :description => "Swissy").save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("CLP").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("CZK").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("DKK").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("HKD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("HUF").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("ILS").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("INR").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("JPY").id,
                        :description => "Gopher").save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("KWD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("MXN").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("NOK").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("OMR").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("PLN").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("RUB").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("SAR").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("SEK").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("SGD").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("SKK").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("THB").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("TRY").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("USD").id,
                        :second_currency_id => Currency.find_by_alpha_code("ZAR").id).save()
        CurrencyPair.new(
                :first_currency_id => Currency.find_by_alpha_code("ZAR").id,
                        :second_currency_id => Currency.find_by_alpha_code("JPY").id).save()
    end

  def self.down
    #Currency.delete_all
  end
end
