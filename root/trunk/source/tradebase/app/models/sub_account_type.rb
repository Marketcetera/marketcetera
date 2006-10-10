class SubAccountType < ActiveRecord::Base
  has_many :sub_accounts
  
  DESCRIPTIONS = { :sti => "Short Term Investment",
    :cash => "Cash",
    :dividendRevenue => "Dividend Revenue",
    :unrealizedGainLoss => "Unrealized Gain/Loss",
    :changeOnCloseOfInv => "Change on Close of Investment",
    :commissions => "Commissions",
    :interestRevenue => "Interest Revenue",
  }
  
  def SubAccountType.preloaded 
    SubAccountType.find_all
  end 
end
