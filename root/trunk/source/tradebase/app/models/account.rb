class Account < ActiveRecord::Base
  has_many :sub_accounts
  has_many :trades
  
  # Given an account, creates all the sub-accounts for it.  
  def before_create()
    SubAccountType.preloaded.each {|sat| 
        sa = SubAccount.new(:description=>sat.description, :sub_account_type=>sat, :account=>self)
        self.sub_accounts << sa
        logger.debug("created sub-account ["+sat.description+"]")
    }
  end
  
  # find sub-acount by sub-account-type description
  def find_sub_account_by_sat(desc)
    if(self.sub_accounts.nil?)  
      return nil
    else 
      return self.sub_accounts.select {|sa| sa.sub_account_type.description == desc }[0]
    end
  end
  
end
