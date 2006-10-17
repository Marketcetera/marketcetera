# Filters added to this controller will be run for all controllers in the application.
# Likewise, all the methods added will be available for all controllers.
class ApplicationController < ActionController::Base
  require 'quickfix'
  require 'quickfix_ruby'
  require 'quickfix_fields'

  helper :application, :trades
end