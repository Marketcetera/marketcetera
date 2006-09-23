class CreateCurrencies < ActiveRecord::Migration
  def self.up
    create_table :currencies do |t|
      t.column :alpha_code, :string, :limit => 3
    end
  end

  def self.down
    drop_table :currencies
  end
end
