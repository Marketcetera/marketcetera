# Helper method to generate SQL from migrations
# Borrowed from Jay Fields at http://blog.jayfields.com/2006/11/rails-generate-sql-from-migrations.html

# Modifies the standard ActiveRecord SQL connection behaviour to
# add a 'write out to file' behaviour to it.
# Goes through all the migrations, and outputs the resulting SQL to file and
# also runs the migrations.
# It makes sense to start with an empty (version=0) database before running this tool.
# The files will be placed in db/marketcetera_tables directory.

require 'find'

class SqlWriter

 def self.write(path, string, orig_sql_method, migrationSelf)
   File.open(path, 'a') { |file| file << string + ";\n" }

   orig_sql_method.bind(migrationSelf).call(string)
 end

end

class MigrationSql
 class << self
   def execute
     connection = ActiveRecord::Base.connection
     old_method = connection.class.instance_method(:execute)
     puts "oldMethod is #{old_method}"

     define_execute(connection) { |*args| SqlWriter.write(@sql_write_path, args.first, old_method, self) }

     root = RAILS_ROOT + "/db/migrate"
     output_dir = root + "/../marketcetera_tables"
     Dir.mkdir output_dir unless File.exists? output_dir

     Dir.foreach(root) do |path|
         if(File.extname(path) == ".rb")
             fullPath = root + '/'+path
             require fullPath
             file = File.basename(fullPath, ".rb")
             write_sql(connection, output_dir, file, :up, false)
             #write_sql(connection, output_dir, file, :up)
             #write_sql(connection, output_dir, file, :down)
             # Run it a 3rd time if you need to actually have it be at last version when you are done.
             # otherwise you'll end up having constraint violations when you create subsquent tables 
             #write_sql(connection, output_dir, file, :up)
         end
     end

     define_execute(connection) { |*args| old_method.bind(self).call(*args) }
   end

   def write_sql(connection, output_dir, file, direction, use_direction = true)
     suffix = (use_direction) ? "_#{direction}" : ""
     destFile = output_dir + "/" + file  + suffix +".sql"
     File.delete(destFile) unless (!File.exists?(destFile)) 
     connection.instance_variable_set :@sql_write_path, destFile
     file.gsub(/^\d\d\d_/,'').camelize.constantize.send direction
   end

   def define_execute(connection, &block)
     connection.class.send :define_method, :execute, &block
   end
 end
end