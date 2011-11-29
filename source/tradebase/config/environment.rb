# Be sure to restart your web server when you modify this file.

# Uncomment below to force Rails into production mode when 
# you don't control web/app server and can't set it the proper way
# ENV['RAILS_ENV'] ||= 'production'

# Specifies gem version of Rails to use when vendor/rails is not present
RAILS_GEM_VERSION = '1.2.3'

# Bootstrap the Rails environment, frameworks, and default configuration
require File.join(File.dirname(__FILE__), 'boot')

# Number of items to list per page 
MaxPerPage=25

# Tradebase base currency
BaseCurrency="USD"

# DateTime should look like this: 04-Jan-08 13:32:23 - timezone gets appended separately
DATETIME_FORMAT = "%d-%b-%y %H:%M:%S"

TZ_ID = "US/Pacific"

# Whether or not to treat Account as Strategy for incoming recorded trades
StrategyAsAccount = true

# Whether  or not we are running on an appliance
RUN_ON_APPLIANCE = false

# Location of the Java library for running JMX reader connector code
# The default location is in lib directory
JAVA_JMX_CONNECTOR_LIB='lib/marketcetera-jmx-connector-1.0.jar'

# Version of the application
RELEASE_VERSION = "2.2.0"

Rails::Initializer.run do |config|
  # Settings in config/environments/* take precedence those specified here
  
  # Skip frameworks you're not going to use (only works if using vendor/rails)
  # config.frameworks -= [ :action_web_service, :action_mailer ]

  # Add additional load paths for your own custom dirs
  config.load_paths += %W( #{RAILS_ROOT}/app/models/reports )

  # Force all environments to use the same logger level 
  # (by default production uses :info, the others :debug)
  # config.log_level = :debug

  # Use the database for sessions instead of the file system
  # (create the session table with 'rake db:sessions:create')
  # config.action_controller.session_store = :active_record_store

  # Use SQL instead of Active Record's schema dumper when creating the test database.
  # This is necessary if your schema can't be completely dumped by the schema dumper, 
  # like if you have constraints or database-specific column types
  # config.active_record.schema_format = :sql

  # Activate observers that should always be running
  # config.active_record.observers = :cacher, :garbage_collector

  # Make Active Record use UTC-base instead of local time
  # config.active_record.default_timezone = :utc
  
  # See Rails::Configuration for more options

  # Store all dates in UTC in db
  config.active_record.default_timezone = :utc

end

# Add new inflection rules using the following format 
# (all these examples are active by default):
 Inflector.inflections do |inflect|
#   inflect.plural /^(ox)$/i, '\1en'
#   inflect.singular /^(ox)en/i, '\1'
#   inflect.irregular 'person', 'people'
#   inflect.uncountable %w( fish sheep )
    inflect.uncountable %w( equity_option_series series equityoptionseries )
 end

# Include your application configuration below
