require "migrate_plus"

class CreateSubAccounts < ActiveRecord::Migration
  include MigratePlus

  def self.up
    create_table :sub_accounts do |t|
      t.column :description, :string, :limit=>255
      t.column :account_id, :integer, :null => false
      t.column :sub_account_type_id, :integer, :null => false
      t.column :created_on, :datetime
      t.column :updated_on, :datetime
    end
    add_fkey :sub_accounts, :account_id, :accounts
    add_fkey :sub_accounts, :sub_account_type_id, :sub_account_types
  end

  def self.down
    drop_table :sub_accounts
  end
end
