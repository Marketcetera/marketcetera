class CreateMSymbols < ActiveRecord::Migration
  def self.up
    create_table :m_symbols do |t|
      t.column :root, :string, :limit => 30, :null => false
      t.column :bloomberg, :string, :limit => 30
      t.column :isin, :string, :limit => 12
      t.column :reuters, :string, :limit => 30
      t.column :created_on, :datetime
      t.column :updated_on, :datetime
    end
  end

  def self.down
    drop_table :m_symbols
  end
end
