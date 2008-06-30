class CreateAccounts < ActiveRecord::Migration
  def self.up
    create_table :accounts do |t|
      t.column :nickname, :string, :limit => 255, :unique => true, :null => false
      t.column :description, :string, :limit => 255
      t.column :institution_identifier, :string, :limit => 255, :null => false
      t.column :created_on, :datetime
      t.column :updated_on, :datetime
    end
    add_index :accounts, :nickname
  end

  def self.down
    drop_table :accounts
  end
end
