#
#		parsearg.rb - parse arguments
#			$Release Version: $
#			$Revision: 2910 $
#			$Date: 2007-02-01 20:42:15 -0800 (Thu, 01 Feb 2007) $
#			by Yasuo OHBA(SHL Japan Inc. Technology Dept.)
#
# --
#
#	
#

warn "Warning:#{caller[0].sub(/:in `.*'\z/, '')}: parsearg is deprecated after Ruby 1.8.1; use optparse instead"

$RCS_ID=%q$Header: /src/ruby/lib/parsearg.rb,v 1.2.2.2 2006/08/04 22:00:21 drbrain Exp $

require "getopts"

def printUsageAndExit()
  if $USAGE
    eval($USAGE)
  end
  exit()
end

def setParenthesis(ex, opt, c)
  if opt != ""
    ex = sprintf("%s$OPT_%s%s", ex, opt, c)
  else
    ex = sprintf("%s%s", ex, c)
  end
  return ex
end

def setOrAnd(ex, opt, c)
  if opt != ""
    ex = sprintf("%s$OPT_%s %s%s ", ex, opt, c, c)
  else
    ex = sprintf("%s %s%s ", ex, c, c)
  end
  return ex
end

def setExpression(ex, opt, op)
  if !op
    ex = sprintf("%s$OPT_%s", ex, opt)
    return ex
  end
  case op.chr
  when "(", ")"
    ex = setParenthesis(ex, opt, op.chr)
  when "|", "&"
    ex = setOrAnd(ex, opt, op.chr)
  else
    return nil
  end
  return ex
end

# parseArgs is obsolete.  Use OptionParser instead.

def parseArgs(argc, nopt, single_opts, *opts)
  if (noOptions = getopts(single_opts, *opts)) == nil
    printUsageAndExit()
  end
  if nopt
    ex = nil
    pos = 0
    for o in nopt.split(/[()|&]/)
      pos += o.length
      ex = setExpression(ex, o, nopt[pos])
      pos += 1
    end
    begin
      if !eval(ex)
	printUsageAndExit()
      end
    rescue
      print "Format Error!! : \"" + nopt + "\"\t[parseArgs]\n"
      exit!(-1)
    end
  end
  if ARGV.length < argc
    printUsageAndExit()
  end
  return noOptions
end
