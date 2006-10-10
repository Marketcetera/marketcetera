class InsertSubAccountTypes < ActiveRecord::Migration
  include SubAccountsHelper
  
  def self.up
    SubAccountType.new(:accounting_account_type => "A", :description => SubAccountType::DESCRIPTIONS[:ShortTermInvestmentDescription]).save()
    SubAccountType.new(:accounting_account_type => "A", :description => SubAccountType::DESCRIPTIONS[:CashDescription]).save()
    SubAccountType.new(:accounting_account_type => "R", :description => SubAccountType::DESCRIPTIONS[:DividendRevenueDescription]).save()
    SubAccountType.new(:accounting_account_type => "A", :description => SubAccountType::DESCRIPTIONS[:UnrealizedGainLossDescription]).save()
    SubAccountType.new(:accounting_account_type => "R", :description => SubAccountType::DESCRIPTIONS[:ChangeonCloseOfInvestmentDescription]).save()
    SubAccountType.new(:accounting_account_type => "E", :description => SubAccountType::DESCRIPTIONS[:CommissionsDescription]).save()
    SubAccountType.new(:accounting_account_type => "R", :description => SubAccountType::DESCRIPTIONS[:InterestRevenueDescription]).save()
  end

  def self.down
    #SubAccountType.delete_all
  end
end
    