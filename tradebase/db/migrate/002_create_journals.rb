require "migrate_plus"

class CreateJournals < ActiveRecord::Migration
  include MigratePlus

  def self.up
    create_table :journals do |t|
      t.column :description, :string, :limit => 255
      t.column :post_date, :date
      t.column :created_on, :datetime
      t.column :updated_on, :datetime
    end
  end

  def self.down
    drop_table :journals
  end
end
