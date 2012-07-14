class Journal < ActiveRecord::Base
  has_many :postings, :dependent => :destroy
  has_many :trades

  # returns the first posting that matches the specified sub-account-type description
  def find_posting_by_sat(desc)
    if(!self.postings.nil?)
      return self.postings.select { |p| p.sub_account.sub_account_type.description == desc}[0]
    else return nil
    end
  end
  
    # find subaccount type by a sub-account-type.description and pair_id
  def find_posting_by_sat_and_pair_id(desc, pair_id)
    if(self.postings.nil?) 
      return nil
    else return self.postings.select {|p| (p.sub_account.sub_account_type.description == desc) && (p.pair_id == pair_id) }[0]
    end
  end
end
