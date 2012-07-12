namespace "db" do
 namespace "generate" do
   desc "generate sql for migrations, start with VERSION=0, leaves db in migrated state"
   task :migration_sql => :environment do
     MigrationSql.execute
   end
 end
end