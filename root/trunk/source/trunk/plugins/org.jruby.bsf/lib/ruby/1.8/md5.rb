# just for compatibility; requiring "md5" is obsoleted
#
# $RoughId: md5.rb,v 1.4 2001/07/13 15:38:27 knu Exp $
# $Id: md5.rb 2062 2006-06-10 19:14:15Z headius $

require 'digest/md5'

MD5 = Digest::MD5

class MD5
  def self.md5(*args)
    new(*args)
  end
end
