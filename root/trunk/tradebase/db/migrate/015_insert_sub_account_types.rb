class InsertSubAccountTypes < ActiveRecord::Migration
  
  def self.up
    SubAccountType.new(:accounting_account_type => "A", :description => SubAccountType::DESCRIPTIONS[:sti]).save()
    SubAccountType.new(:accounting_account_type => "A", :description => SubAccountType::DESCRIPTIONS[:cash]).save()
    SubAccountType.new(:accounting_account_type => "R", :description => SubAccountType::DESCRIPTIONS[:dividendRevenue]).save()
    SubAccountType.new(:accounting_account_type => "A", :description => SubAccountType::DESCRIPTIONS[:unrealizedGainLoss]).save()
    SubAccountType.new(:accounting_account_type => "R", :description => SubAccountType::DESCRIPTIONS[:changeOnCloseOfInv]).save()
    SubAccountType.new(:accounting_account_type => "E", :description => SubAccountType::DESCRIPTIONS[:commissions]).save()
    SubAccountType.new(:accounting_account_type => "R", :description => SubAccountType::DESCRIPTIONS[:interestRevenue]).save()
  end

  def self.down
    #SubAccountType.delete_all
  end
end
    