# Subversion property setter.
#
# Author: tlerios@marketcetera.com
# Since: 0.5.0
# Version: $Id$
# $License$


use strict;
use File::Find;


# Error checking.

if (@ARGV!=1) {
	warn "\n";
	warn "Usage: ",__FILE__," <Root directory>\n";
	die "\n";
}

# Configuration.

my($ignoreFile)=$ENV{'TEMP'}.'/ignoreFile';

my(@patternsEmacs)=('*~','#*#');
my(@patternsEclipse)=('.classpath','.project','.settings');
my(@patternsIntelliJ)=('*.ipr','*.iws','*.iml');
my(@patternsMaven)=('target');
my(@patternsPython)=('*$py.class');

my(@patternsMavenTop)=(
	@patternsEmacs,
	@patternsEclipse,
	@patternsIntelliJ,
	@patternsMaven,
	@patternsPython
);

my(@patternsPhotonTop)=(
	@patternsEmacs,
	@patternsIntelliJ,
	@patternsMaven,
	@patternsPython
);

my(@patternsAll)=(
	@patternsEmacs,
	@patternsPython
);

my(%patternsCustom)=(
	'public'
	  => ('profiles.xml'),
	'private/source/photon/plugins/com.marketcetera.marketdata.opentick'
	  => ('bin'),
	'private/source/photon/plugins/com.marketcetera.marketdata.opentick.tests'
	  => ('bin'),
	'public/source/photon/plugins/com.swtworkbench.community.xswt'
	  => ('bin'),
	'public/source/photon/plugins/org.marketcetera.core'
	  => ('core-0.5.0.jar'),
	'public/source/photon/plugins/org.marketcetera.core.tests'
	  => ('core-0.5.0-tests.jar'),
	'public/source/photon/plugins/org.marketcetera.bogusfeed'
	  => ('bin'),
);

my(@externals)=(
	'public/source/photon/plugins/org.jruby.bsf/lib/ruby/1.8',
	'public/source/photon/plugins/org.jruby.bsf/lib/ruby/site_ruby',
	'public/source/tradebase/vendor/plugins/debug_view_helper',
);

my(@executables)=(
	'private/tools/install4j/misc/getbins.sh',
	'private/tools/install4j/template-win/.shortcuts/editORSConfig.bat',
	'private/tools/install4j/template-win/.shortcuts/editTradebaseConfig.bat',
	'private/tools/install4j/template-win/setEnv.bat',
	'private/tools/install4j/template-win/sql/create_db.bat',
	'private/tools/install4j/template-win/sql/start_mysql.bat',
	'private/tools/install4j/template-win/sql/stop_mysql.bat',
	'private/tools/install4j/template-win/sql/wait.bat',
	'private/tools/install4j/template-win/startServerComponents.bat',
	'public/source/tradebase/script/about',
	'public/source/tradebase/script/breakpointer',
	'public/source/tradebase/script/console',
	'public/source/tradebase/script/destroy',
	'public/source/tradebase/script/generate',
	'public/source/tradebase/script/performance/benchmarker',
	'public/source/tradebase/script/performance/profiler',
	'public/source/tradebase/script/plugin',
	'public/source/tradebase/script/poller',
	'public/source/tradebase/script/process/reaper',
	'public/source/tradebase/script/process/spawner',
	'public/source/tradebase/script/runner',
	'public/source/tradebase/script/server',
);

# Run command.

sub run($)
{
	my($cmd)=$_[0];
	warn 'Running: '.$cmd."\n";
	system($cmd);
}

# Set up file search.

sub walk ()
{
	if (-d && (/^\.svn$/io || /^target$/io)) {
		warn 'Ignoring subversion/target: '.$File::Find::name."\n";
		$File::Find::prune=1;
		return;
	}

	my($absName)=File::Spec->rel2abs($_);
	$absName=~s#\\#/#g;
	($absName)=($absName=~/(((public)|(private)).*)/io);

	my($external);
	foreach $external (@externals) {
		if ($absName eq $external) {
			warn 'Ignoring external directory: '.$File::Find::name."\n";
			$File::Find::prune=1;
			return;
		}
	}

	if (-d) {
		if (!(-e $_.'/.svn')) {
			warn 'Ignoring non-subversion directory: '.$File::Find::name."\n";
			return;
		}
		my(@patterns)=();
		my($key);
		foreach $key (keys(%patternsCustom)) {
			if ($absName eq $key) {
				@patterns=(@patterns,$patternsCustom{$key});
				last;
			}
		}
		if (($absName=~m#/source/photon/#) && (-e $_.'/META-INF')) {
			@patterns=(@patterns,@patternsPhotonTop);
		} elsif (-e $_.'/pom.xml') {
			@patterns=(@patterns,@patternsMavenTop);
		} else {
			@patterns=(@patterns,@patternsAll);
		}
		open(IGNORE,'>'.$ignoreFile);
		my($pattern);
		foreach $pattern (@patterns) {
			print IGNORE $pattern."\n";
		}
		close(IGNORE);
		run('svn propset svn:ignore -F '.$ignoreFile.' '.$_);
		unlink($ignoreFile);
		return;
	}

	my($quoted)='"'.$_.'"';
	my($executable)=0;
	my($key);
	foreach $key (@executables) {
		if ($absName eq $key) {
			$executable=1;
		}
	}
	if ($executable) {
		run('svn propset svn:executable "*" '.$quoted);
	} else {
		run('svn propdel svn:executable '.$quoted);
	}

	my($mime);
	if (/\.java$/io) {
		$mime='text/plain';
	} elsif (/\.txt$/io) {
		$mime='text/plain';
	} elsif (/\.xml$/io) {
		$mime='text/xml';
	} else {
		$mime=`file --brief --mime $quoted`;
		chop($mime);
	}
	run('svn propset svn:mime-type "'.$mime.'" '.$quoted);
	run('svn propset svn:keywords "Id Revision" '.$quoted);
}

find(\&walk,$ARGV[0]);
