class CreateIdRepository < ActiveRecord::Migration
  def self.up
    create_table(:id_repository, :primary_key => 'nextAllowedID') do 
      # no other columns
    end
  end

  def self.down
    drop_table :id_repository
  end
end
