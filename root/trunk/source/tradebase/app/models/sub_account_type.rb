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

  @@cash = SubAccountType.find_by_description(DESCRIPTIONS[:cash])

  def SubAccountType.CASH
      @@cash
  end
  
  def SubAccountType.preloaded
    if(@@cash.nil?)
        @@cash = SubAccountType.find_by_description(DESCRIPTIONS[:cash])
    end
    SubAccountType.find(:all)
  end
end
