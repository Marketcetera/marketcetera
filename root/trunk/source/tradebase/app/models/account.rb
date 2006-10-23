class Account < ActiveRecord::Base
  has_many :sub_accounts
  has_many :trades
  
  validates_length_of :nickname, :minimum => 1
  validates_length_of :institution_identifier, :minimum => 1
  
  UNASSIGNED_NAME = '[UNASSIGNED]'
  
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
  
  def Account.find_by_nickname(nick)
    if(nick.blank?)
      nick = UNASSIGNED_NAME
    end  
    
    super(nick)
  end
  
  def to_s
    (self.nickname.nil? ) ? '[]' : "["+self.nickname+"]"
  end
end
