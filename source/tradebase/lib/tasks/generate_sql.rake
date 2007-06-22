namespace "db" do
 namespace "generate" do
   desc "generate sql for migrations"
   task :migration_sql => :environment do
     MigrationSql.execute
   end
 end
end