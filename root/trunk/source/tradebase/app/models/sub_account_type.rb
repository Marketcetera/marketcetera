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
  
  CASH = SubAccountType.find_by_description(DESCRIPTIONS[:cash])
  
  
  def SubAccountType.preloaded 
    SubAccountType.find(:all)
  end 
end
