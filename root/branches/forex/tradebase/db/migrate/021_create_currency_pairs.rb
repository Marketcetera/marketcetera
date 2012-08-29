require "migrate_plus"

#
## To get currencies with human-readable names:
#
# SELECT first_currency_id, C1.alpha_code,
#      second_currency_id, C2.alpha_code
# FROM currency_pairs
# JOIN currencies C1 ON first_currency_id = C1.id
# JOIN currencies C2 ON second_currency_id = C2.id
#

class CreateCurrencyPairs < ActiveRecord::Migration
  include MigratePlus

  def self.up
    create_table :currency_pairs do |t|
      t.column :description, :string, :limit => 255
      t.column :first_currency_id, :integer, :null => false
      t.column :second_currency_id, :integer, :null => false
      t.column :created_on, :datetime
      t.column :updated_on, :datetime
    end
    add_fkey :currency_pairs, :first_currency_id, :currencies, {:fcolumn => :id, :index => true }
    add_fkey :currency_pairs, :second_currency_id, :currencies, {:fcolumn => :id, :index => true }
    
  end

  def self.down
    drop_table :currency_pairs
  end
end
