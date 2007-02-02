require "migrate_plus"

class CreatePostings < ActiveRecord::Migration
  include MigratePlus

  def self.up
    create_table :postings do |t|
      t.column :quantity, :decimal, :precision=>15, :scale=>5
      t.column :sub_account_id, :integer, :null => false
      t.column :journal_id, :integer, :null => false
      t.column :currency_id, :integer, :null => false
      t.column :pair_id, :integer, :null => false
      t.column :created_on, :datetime
      t.column :updated_on, :datetime
    end
    add_fkey :postings, :sub_account_id, :sub_accounts
    add_fkey :postings, :journal_id, :journals
    add_fkey :postings, :currency_id, :currencies
  end

  def self.down
    drop_table :postings
  end
end
