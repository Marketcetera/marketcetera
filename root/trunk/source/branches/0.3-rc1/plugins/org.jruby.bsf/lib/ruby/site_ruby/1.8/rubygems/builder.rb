#--
# Copyright 2006 by Chad Fowler, Rich Kilmer, Jim Weirich and others.
# All rights reserved.
# See LICENSE.txt for permissions.
#++

require "rubygems/package"
require "rubygems/security"
require "yaml"
require 'rubygems/gem_openssl'

module Gem

  ##
  # The Builder class processes RubyGem specification files
  # to produce a .gem file.
  #
  class Builder
  
    include UserInteraction
    ##
    # Constructs a builder instance for the provided specification
    #
    # spec:: [Gem::Specification] The specification instance
    #
    def initialize(spec)
      @spec = spec
    end
    
    ##
    # Builds the gem from the specification.  Returns the name of the file written.
    #
    def build
      @spec.mark_version
      @spec.validate
      
      # if the signing key was specified, then load the file, and swap
      # to the public key (TODO: we should probably just omit the
      # signing key in favor of the signing certificate, but that's for
      # the future, also the signature algorihtm should be configurable)
      signer = nil
      if @spec.respond_to?(:signing_key) && @spec.signing_key
        signer = Gem::Security::Signer.new(@spec.signing_key, @spec.cert_chain)
        @spec.signing_key = nil
        @spec.cert_chain = signer.cert_chain.map { |cert| cert.to_s }
      end

      file_name = @spec.full_name+".gem"

      Package.open(file_name, "w", signer) do |pkg|
          pkg.metadata = @spec.to_yaml
          @spec.files.each do |file|
              next if File.directory? file
              pkg.add_file_simple(file, File.stat(file_name).mode & 0777,
                                  File.size(file)) do |os|
                                      os.write File.open(file, "rb"){|f|f.read}
                                  end
          end
      end
      say success
      file_name
    end
    
    def success
      <<-EOM
  Successfully built RubyGem
  Name: #{@spec.name}
  Version: #{@spec.version}
  File: #{@spec.full_name+'.gem'}
EOM
    end
  end
end
