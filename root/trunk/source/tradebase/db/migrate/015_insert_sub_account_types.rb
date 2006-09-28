class InsertSubAccountTypes < ActiveRecord::Migration
  include SubAccountsHelper
  
  def self.up
    SubAccountType.new(:accounting_account_type => "A", :description => ShortTermInvestmentDescription).save()
    SubAccountType.new(:accounting_account_type => "A", :description => CashDescription).save()
    SubAccountType.new(:accounting_account_type => "R", :description => DividendRevenueDescription).save()
    SubAccountType.new(:accounting_account_type => "A", :description => UnrealizedGainLossDescription).save()
    SubAccountType.new(:accounting_account_type => "R", :description => ChangeonCloseOfInvestmentDescription).save()
    SubAccountType.new(:accounting_account_type => "E", :description => CommissionsDescription).save()
    SubAccountType.new(:accounting_account_type => "R", :description => InterestRevenueDescription).save()
  end

  def self.down
    Currency.delete_all
  end
end
    