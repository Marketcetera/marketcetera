class CreateSubAccountTypes < ActiveRecord::Migration
  def self.up
    create_table :sub_account_types do |t|
      t.column :description, :string, :limit=>255
      t.column :accounting_account_type, :string, :limit=>1
      t.column :created_on, :datetime
      t.column :updated_on, :datetime
    end
  end

  def self.down
    drop_table :sub_account_types
  end
end
