class CreateIdRepository < ActiveRecord::Migration
  def self.up
#    create_table(:id_repository, :primary_key => 'nextAllowedID' ) do |t|
    create_table(:id_repository) do |t|
      t.column :nextAllowedID, :integer, :null => false
    end
    
    IdRepository.new(:nextAllowedID => 1).save()
  end

  def self.down
    drop_table :id_repository
  end
end
