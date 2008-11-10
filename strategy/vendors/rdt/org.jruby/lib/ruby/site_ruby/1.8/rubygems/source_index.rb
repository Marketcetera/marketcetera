#--
# Copyright 2006 by Chad Fowler, Rich Kilmer, Jim Weirich and others.
# All rights reserved.
# See LICENSE.txt for permissions.
#++

require 'rubygems'
require 'rubygems/user_interaction'
require 'rubygems/specification'

module Gem

  # The SourceIndex object indexes all the gems available from a
  # particular source (e.g. a list of gem directories, or a remote
  # source).  A SourceIndex maps a gem full name to a gem
  # specification.
  #
  # NOTE:: The class used to be named Cache, but that became
  #        confusing when cached source fetchers where introduced. The
  #        constant Gem::Cache is an alias for this class to allow old
  #        YAMLized source index objects to load properly.
  #
  class SourceIndex

    include Enumerable

    include Gem::UserInteraction

    # Class Methods. -------------------------------------------------
    class << self
      include Gem::UserInteraction
    
      # Factory method to construct a source index instance for a given
      # path.
      #
      # deprecated::
      #   If supplied, from_installed_gems will act just like
      #   +from_gems_in+.  This argument is deprecated and is provided
      #   just for backwards compatibility, and should not generally
      #   be used.
      # 
      # return::
      #   SourceIndex instance
      #
      def from_installed_gems(*deprecated)
        if deprecated.empty?
          from_gems_in(*installed_spec_directories)
        else
          from_gems_in(*deprecated)
        end
      end
      
      # Return a list of directories in the current gem path that
      # contain specifications.
      # 
      # return::
      #   List of directory paths (all ending in "../specifications").
      #
      def installed_spec_directories
        Gem.path.collect { |dir| File.join(dir, "specifications") }        
      end

      # Factory method to construct a source index instance for a
      #   given path.
      # 
      # spec_dirs::
      #   List of directories to search for specifications.  Each
      #   directory should have a "specifications" subdirectory
      #   containing the gem specifications.
      #
      # return::
      #   SourceIndex instance
      #
      def from_gems_in(*spec_dirs)
        self.new.load_gems_in(*spec_dirs)
      end
      
      # Load a specification from a file (eval'd Ruby code)
      # 
      # file_name:: [String] The .gemspec file
      # return:: Specification instance or nil if an error occurs
      #
      def load_specification(file_name)
        begin
          spec_code = File.read(file_name).untaint
          gemspec = eval spec_code, binding, file_name
          if gemspec.is_a?(Gem::Specification)
            gemspec.loaded_from = file_name
            return gemspec
          end
          alert_warning "File '#{file_name}' does not evaluate to a gem specification"
        rescue SyntaxError => e
          alert_warning e
          alert_warning spec_code
        rescue Exception => e
          alert_warning(e.inspect.to_s + "\n" + spec_code)
          alert_warning "Invalid .gemspec format in '#{file_name}'"
        end
        return nil
      end
      
    end

    # Instance Methods -----------------------------------------------

    # Constructs a source index instance from the provided
    # specifications
    #
    # specifications::
    #   [Hash] hash of [Gem name, Gem::Specification] pairs
    #
    def initialize(specifications={})
      @gems = specifications
    end
    
    # Reconstruct the source index from the list of source
    # directories.
    def load_gems_in(*spec_dirs)
      @gems.clear
      specs = Dir.glob File.join("{#{spec_dirs.join(',')}}", "*.gemspec")
      specs.each do |file_name|
        gemspec = self.class.load_specification(file_name.untaint)
        add_spec(gemspec) if gemspec
      end
      self
    end

    # Returns a Hash of name => Specification of the latest versions of each
    # gem in this index.
    def latest_specs
      result, latest = Hash.new { |h,k| h[k] = [] }, {}

      self.each do |_, spec| # SourceIndex is not a hash, so we're stuck with each
        name = spec.name
        curr_ver = spec.version
        prev_ver = latest[name]

        next unless prev_ver.nil? or curr_ver >= prev_ver

        if prev_ver.nil? or curr_ver > prev_ver then
          result[name].clear
          latest[name] = curr_ver
        end

        result[name] << spec
      end

      result.values.flatten
    end

    # Add a gem specification to the source index.
    def add_spec(gem_spec)
      @gems[gem_spec.full_name] = gem_spec
    end

    # Remove a gem specification named +full_name+.
    def remove_spec(full_name)
      @gems.delete(full_name)
    end

    # Iterate over the specifications in the source index.
    def each(&block) # :yields: gem.full_name, gem
      @gems.each(&block)
    end

    # The gem specification given a full gem spec name.
    def specification(full_name)
      @gems[full_name]
    end

    # The signature for the source index.  Changes in the signature
    # indicate a change in the index.
    def index_signature
      require 'rubygems/digest/sha2'

      Gem::SHA256.new.hexdigest(@gems.keys.sort.join(',')).to_s
    end

    # The signature for the given gem specification.
    def gem_signature(gem_full_name)
      require 'rubygems/digest/sha2'

      Gem::SHA256.new.hexdigest(@gems[gem_full_name].to_yaml).to_s
    end

    def size
      @gems.size
    end
    alias length size

    # Find a gem by an exact match on the short name.
    def find_name(gem_name, version_requirement = Gem::Requirement.default)
      search(/^#{gem_name}$/, version_requirement)
    end

    # Search for a gem by Gem::Dependency +gem_pattern+.  If +only_platform+
    # is true, only gems matching Gem::Platform.local will be returned.  An
    # Array of matching Gem::Specification objects is returned.
    #
    # For backwards compatibility, a String or Regexp pattern may be passed as
    # +gem_pattern+, and a Gem::Requirement for +platform_only+.  This
    # behavior is deprecated and will be removed.
    def search(gem_pattern, platform_only = false)
      version_requirement = nil
      only_platform = false

      case gem_pattern # TODO warn after 2008/03, remove three months after
      when Regexp then
        version_requirement = platform_only || Gem::Requirement.default
      when Gem::Dependency then
        only_platform = platform_only
        version_requirement = gem_pattern.version_requirements
        gem_pattern = if gem_pattern.name.empty? then
                        //
                      else
                        /^#{Regexp.escape gem_pattern.name}$/
                      end
      else
        version_requirement = platform_only || Gem::Requirement.default
        gem_pattern = /#{gem_pattern}/i
      end

      unless Gem::Requirement === version_requirement then
        version_requirement = Gem::Requirement.create version_requirement
      end

      specs = @gems.values.select do |spec|
        spec.name =~ gem_pattern and
          version_requirement.satisfied_by? spec.version
      end

      if only_platform then
        specs = specs.select do |spec|
          Gem::Platform.match spec.platform
        end
      end

      specs.sort_by { |s| s.sort_obj }
    end

    # Refresh the source index from the local file system.
    #
    # return:: Returns a pointer to itself.
    #
    def refresh!
      load_gems_in(self.class.installed_spec_directories)
    end

    # Returns an Array of Gem::Specifications that are not up to date.
    #
    def outdated
      dep = Gem::Dependency.new '', Gem::Requirement.default

      remotes = Gem::SourceInfoCache.search dep, true

      outdateds = []

      latest_specs.each do |local|
        name = local.name
        remote = remotes.select  { |spec| spec.name == name }.
                         sort_by { |spec| spec.version.to_ints }.
                         last
        outdateds << name if remote and local.version < remote.version
      end

      outdateds
    end

    def update(source_uri)
      use_incremental = false

      begin
        gem_names = fetch_quick_index source_uri
        remove_extra gem_names
        missing_gems = find_missing gem_names

        return false if missing_gems.size.zero?

        say "missing #{missing_gems.size} gems" if
          missing_gems.size > 0 and Gem.configuration.really_verbose

        use_incremental = missing_gems.size <= Gem.configuration.bulk_threshold
      rescue Gem::OperationNotSupportedError => ex
        alert_error "Falling back to bulk fetch: #{ex.message}" if
          Gem.configuration.really_verbose
        use_incremental = false
      end

      if use_incremental then
        update_with_missing(source_uri, missing_gems)
      else
        new_index = fetch_bulk_index(source_uri)
        @gems.replace(new_index.gems)
      end

      true
    end

    def ==(other) # :nodoc:
      self.class === other and @gems == other.gems 
    end

    def dump
      Marshal.dump(self)
    end

    protected

    attr_reader :gems

    private

    def fetcher
      require 'rubygems/remote_fetcher'

      Gem::RemoteFetcher.fetcher
    end

    def fetch_index_from(source_uri)
      @fetch_error = nil

      indexes = %W[
        Marshal.#{Gem.marshal_version}.Z
        Marshal.#{Gem.marshal_version}
        yaml.Z
        yaml
      ]

      indexes.each do |name|
        spec_data = nil
        begin
          spec_data = fetcher.fetch_path("#{source_uri}/#{name}")
          spec_data = unzip(spec_data) if name =~ /\.Z$/
          if name =~ /Marshal/ then
            return Marshal.load(spec_data)
          else
            return YAML.load(spec_data)
          end
        rescue => e
          if Gem.configuration.really_verbose then
            alert_error "Unable to fetch #{name}: #{e.message}"
          end
          @fetch_error = e
        end
      end
      nil
    end

    def fetch_bulk_index(source_uri)
      say "Bulk updating Gem source index for: #{source_uri}"

      index = fetch_index_from(source_uri)
      if index.nil? then
        raise Gem::RemoteSourceException,
              "Error fetching remote gem cache: #{@fetch_error}"
      end
      @fetch_error = nil
      index
    end

    # Get the quick index needed for incremental updates.
    def fetch_quick_index(source_uri)
      zipped_index = fetcher.fetch_path source_uri + '/quick/index.rz'
      unzip(zipped_index).split("\n")
    rescue ::Exception => ex
      raise Gem::OperationNotSupportedError,
            "No quick index found: " + ex.message
    end

    # Make a list of full names for all the missing gemspecs.
    def find_missing(spec_names)
      spec_names.find_all { |full_name|
        specification(full_name).nil?
      }
    end

    def remove_extra(spec_names)
      dictionary = spec_names.inject({}) { |h, k| h[k] = true; h }
      each do |name, spec|
        remove_spec name unless dictionary.include? name
      end
    end

    # Unzip the given string.
    def unzip(string)
      require 'zlib'
      Zlib::Inflate.inflate(string)
    end

    # Tries to fetch Marshal representation first, then YAML
    def fetch_single_spec(source_uri, spec_name)
      @fetch_error = nil
      begin
        marshal_uri = source_uri + "/quick/Marshal.#{Gem.marshal_version}/#{spec_name}.gemspec.rz"
        zipped = fetcher.fetch_path marshal_uri
        return Marshal.load(unzip(zipped))
      rescue => ex
        @fetch_error = ex
        if Gem.configuration.really_verbose then
          say "unable to fetch marshal gemspec #{marshal_uri}: #{ex.class} - #{ex}"
        end
      end

      begin
        yaml_uri = source_uri + "/quick/#{spec_name}.gemspec.rz"
        zipped = fetcher.fetch_path yaml_uri
        return YAML.load(unzip(zipped))
      rescue => ex
        @fetch_error = ex
        if Gem.configuration.really_verbose then
          say "unable to fetch YAML gemspec #{yaml_uri}: #{ex.class} - #{ex}"
        end
      end
      nil
    end

    # Update the cached source index with the missing names.
    def update_with_missing(source_uri, missing_names)
      progress = ui.progress_reporter(missing_names.size,
        "Updating metadata for #{missing_names.size} gems from #{source_uri}")
      missing_names.each do |spec_name|
        gemspec = fetch_single_spec(source_uri, spec_name)
        if gemspec.nil? then
          ui.say "Failed to download spec #{spec_name} from #{source_uri}:\n" \
                 "\t#{@fetch_error.message}"
        else
          add_spec gemspec
          progress.updated spec_name
        end
        @fetch_error = nil
      end
      progress.done
      progress.count
    end

  end

  # Cache is an alias for SourceIndex to allow older YAMLized source
  # index objects to load properly.
  Cache = SourceIndex

end

