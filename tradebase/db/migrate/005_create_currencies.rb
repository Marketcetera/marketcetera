class CreateCurrencies < ActiveRecord::Migration
  def self.up
    create_table :currencies do |t|
      t.column :alpha_code, :string, :limit => 3
      t.column :numeric_code, :integer, :limit => 3
      t.column :description, :string, :limit => 255
      t.column :obsolete, :boolean
      t.column :created_on, :datetime
      t.column :updated_on, :datetime
    end
    add_index :currencies, :alpha_code, :unique
    add_index :currencies, :numeric_code, :unique
  end

  def self.down
    drop_table :currencies
  end
end
