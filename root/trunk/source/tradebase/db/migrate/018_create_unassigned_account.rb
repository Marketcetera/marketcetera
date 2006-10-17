class CreateUnassignedAccount < ActiveRecord::Migration
  def self.up
      Account.new(:nickname => Account::UNASSIGNED_NAME, 
                  :description => "General catch-all for all unassigned trades").save()
  end

  def self.down
#    Account.find_by_nickname(Account::UNASSIGNED_NAME).sub_accounts.each{ |sa| sa.destroy }
#    Account.find_by_nickname(Account::UNASSIGNED_NAME).destroy
  end
end
