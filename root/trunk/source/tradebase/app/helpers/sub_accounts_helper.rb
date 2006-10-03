module SubAccountsHelper
    ShortTermInvestmentDescription = "Short Term Investment"
    CashDescription = "Cash"
    DividendRevenueDescription = "Dividend Revenue"
    UnrealizedGainLossDescription = "Unrealized Gain/Loss"
    ChangeonCloseOfInvestmentDescription = "Change on Close of Investment"
    CommissionsDescription = "Commissions"
    InterestRevenueDescription = "Interest Revenue"

  # Given an account, creates all the sub-accounts for it.  
  def fill_in_sub_accounts(account)
    SubAccountType.find_all.each {|sat| 
    SubAccount.new(:description=>sat.description, :sub_account_type=>sat, :account=>account).save()}
  end


end