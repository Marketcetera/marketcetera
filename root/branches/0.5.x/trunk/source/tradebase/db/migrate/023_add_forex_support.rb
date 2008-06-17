require "migrate_plus"

class AddForexSupport < ActiveRecord::Migration
  include MigratePlus

  def self.up
    add_column :trades, :type, :string
    add_column :trades, :strategy, :string, :limit=>255

    usd = Currency.find_by_alpha_code("USD")
    add_column :marks, :currency_id, :integer, {:default=>usd.id, :null =>false} 
    add_column :marks, :tradeable_type, :string, :limit=>255
    add_column :marks, :type, :string, :limit=>255

    add_fkey :marks, :currency_id, :currencies

    # remove the foreign key to equities table - we have :has_many relationships instead, and we can't
    # have a foreign key into 2 tables
    remove_fkey :marks, :tradeable_id, :equities

    # modify the journal.post_date column to be datetime from date
    change_column :journals, :post_date, :datetime

    # make messages_log columns nullable
    change_column :messages_log, :beginstring, :string, :limit => 8, :null => true
    change_column :messages_log, :sendercompid, :string, :limit => 64, :null => true
    change_column :messages_log, :targetcompid, :string, :limit => 64, :null => true
    add_column :messages_log, :failed_parsing, :boolean, :default => false
  end

  def self.down
    remove_fkey :marks, :currency_id, :currencies
    remove_column :trades, :type
    remove_column :trades, :strategy
    remove_column :marks, :currency_id
    remove_column :marks, :type
    remove_column :marks, :tradeable_type
    # do not re-add foreign key on marks to equities table since we may have duplicate tradeable ids in equities/currencies already

    # modify the journal.post_date column to be date from datetime 
    change_column :journals, :post_date, :date
  end
end
