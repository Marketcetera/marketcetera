require 'rubygems/command'
require 'rubygems/remote_fetcher'
require 'rubygems/source_info_cache'
require 'rubygems/source_info_cache_entry'

class Gem::Commands::SourcesCommand < Gem::Command

  def initialize
    super 'sources',
          'Manage the sources and cache file RubyGems uses to search for gems'

    add_option '-a', '--add SOURCE_URI', 'Add source' do |value, options|
      options[:add] = value
    end

    add_option '-l', '--list', 'List sources' do |value, options|
      options[:list] = value
    end

    add_option '-r', '--remove SOURCE_URI', 'Remove source' do |value, options|
      options[:remove] = value
    end

    add_option '-u', '--update', 'Update source cache' do |value, options|
      options[:update] = value
    end

    add_option '-c', '--clear-all',
               'Remove all sources (clear the cache)' do |value, options|
      options[:clear_all] = value
    end
  end

  def defaults_str
    '--list'
  end

  def execute
    options[:list] = !(options[:add] || options[:remove] || options[:clear_all] || options[:update])

    if options[:clear_all] then
      remove_cache_file("user", Gem::SourceInfoCache.user_cache_file)
      remove_cache_file("system", Gem::SourceInfoCache.system_cache_file)
    end

    if options[:add] then
      source_uri = options[:add]

      sice = Gem::SourceInfoCacheEntry.new nil, nil
      begin
        sice.refresh source_uri

        Gem::SourceInfoCache.cache_data[source_uri] = sice
        Gem::SourceInfoCache.cache.update
        Gem::SourceInfoCache.cache.flush

        Gem.sources << source_uri
        Gem.configuration.write

        say "#{source_uri} added to sources"
      rescue URI::Error, ArgumentError
        say "#{source_uri} is not a URI"
      rescue Gem::RemoteFetcher::FetchError => e
        say "Error fetching #{source_uri}:\n\t#{e.message}"
      end
    end

    if options[:update] then
      Gem::SourceInfoCache.cache.refresh
      Gem::SourceInfoCache.cache.flush

      say "source cache successfully updated"
    end

    if options[:remove] then
      source_uri = options[:remove]

      unless Gem.sources.include? source_uri then
        say "source #{source_uri} not present in cache"
      else
        Gem::SourceInfoCache.cache_data.delete source_uri
        Gem::SourceInfoCache.cache.update
        Gem::SourceInfoCache.cache.flush
        Gem.sources.delete source_uri
        Gem.configuration.write

        say "#{source_uri} removed from sources"
      end
    end

    if options[:list] then
      say "*** CURRENT SOURCES ***"
      say

      Gem.sources.each do |source_uri|
        say source_uri
      end
    end
  end

  private

  def remove_cache_file(desc, fn)
    FileUtils.rm_rf fn rescue nil
    if ! File.exist?(fn)
      say "*** Removed #{desc} source cache ***"
    elsif ! File.writable?(fn)
      say "*** Unable to remove #{desc} source cache (write protected) ***"
    else
      say "*** Unable to remove #{desc} source cache ***"
    end
  end

end

