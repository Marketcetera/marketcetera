=begin
= $RCSfile: openssl.rb,v $ -- Loader for all OpenSSL C-space and Ruby-space definitions

= Info
  'OpenSSL for Ruby 2' project
  Copyright (C) 2002  Michal Rokos <m.rokos@sh.cvut.cz>
  All rights reserved.

= Licence
  This program is licenced under the same licence as Ruby.
  (See the file 'LICENCE'.)

= Version
  $Id: openssl.rb,v 1.1 2003/07/23 16:11:29 gotoyuzo Exp $
=end

require 'openssl.so'

require 'openssl/bn'
require 'openssl/cipher'
require 'openssl/digest'
require 'openssl/ssl'
require 'openssl/x509'

