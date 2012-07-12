require "migrate_plus"

class CreateEquityOptionSeries < ActiveRecord::Migration
  include MigratePlus

  def self.up
    create_table :equity_option_series do |t|
      t.column :cash_deliverable, :decimal, :precision => 15, :scale => 5
      t.column :m_symbol_id, :integer
      t.column :cash_deliverable_currency_id, :integer
      t.column :created_on, :datetime
      t.column :updated_on, :datetime
    end
    add_fkey :equity_option_series, :cash_deliverable_currency_id, :currencies
    add_fkey :equity_option_series, :m_symbol_id, :m_symbols
  end

  def self.down
    drop_table :equity_option_series
  end
end
