class InsertSubAccountTypes < ActiveRecord::Migration
  def self.up
    SubAccountType.new(:accounting_account_type => "A", :description => "Short Term Investment").save()
    SubAccountType.new(:accounting_account_type => "A", :description => "Cash").save()
    SubAccountType.new(:accounting_account_type => "R", :description => "Dividend Revenue").save()
    SubAccountType.new(:accounting_account_type => "A", :description => "Unrealized Gain/Loss").save()
    SubAccountType.new(:accounting_account_type => "R", :description => "Change on Close of Investment").save()
    SubAccountType.new(:accounting_account_type => "E", :description => "Commissions").save()
    SubAccountType.new(:accounting_account_type => "R", :description => "Interest Revenue").save()
  end

  def self.down
    Currency.delete_all
  end
end
    