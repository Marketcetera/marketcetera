require "migrate_plus"

# modifies the accounts table to allow for null institution_identifier w/ default value 
# This may be necessary for Ruby 1.8.6
class ModifyAccounts < ActiveRecord::Migration
  include MigratePlus

  def self.up
    # make accounts columns nullable
    change_column :accounts, :institution_identifier, :string, :limit => 64, :default=>"", :null =>false 
  end
end
