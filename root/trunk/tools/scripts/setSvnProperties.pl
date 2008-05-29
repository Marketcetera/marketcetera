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

# Create lists of ignored files.

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
	'public/source/oms' => ('store')
);

my(@externals)=(
	'public/source/photon/plugins/org.jruby.bsf/lib/ruby/1.8',
	'public/source/photon/plugins/org.jruby.bsf/lib/ruby/site_ruby',
	'public/source/photon/plugins/org.marketcetera.core/src/main',
	'public/source/photon/plugins/org.marketcetera.core.tests/src/test'
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
	my($absName)=File::Spec->rel2abs($_);
	$absName=~s#\\#/#g;

	my($external);
	foreach $external (@externals) {
		if ($absName=~/${external}$/) {
			warn 'Ignoring external directory: '.$File::Find::name."\n";
			$File::Find::prune=1;
			return;
		}
	}

	if (-d && (/^\.svn$/io || /^target$/io)) {
		$File::Find::prune=1;
		return;
	}

	if (-d) {
		if (!(-e $_.'/.svn')) {
			warn 'Ignoring non-subversion directory: '.$File::Find::name."\n";
			return;
		}
		my(@patterns)=();
		my($key);
		foreach $key (keys(%patternsCustom)) {
			if ($absName=~/${key}$/) {
				@patterns=(@patterns,$patternsCustom{$key});
				last;
			}
		}
		if (($absName=~m#/source/photon/#) && (-e $_.'/META-INF')) {
			@patterns=(@patterns,@patternsPhotonTop);
		} elsif ((-e $_.'/pom.xml') && (-e $_.'/src')) {
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

	run('svn propdel svn:executable '.$_);
	my($mime);
	if (/\.java$/io) {
		$mime='text/plain';
	} elsif (/\.txt$/io) {
		$mime='text/plain';
	} elsif (/\.xml$/io) {
		$mime='text/xml';
	} else {
		$mime=`file --brief --mime $_`;
		chop($mime);
	}
	run('svn propset svn:mime-type "'.$mime.'" '.$_);
	run('svn propset svn:keywords "Id Revision" '.$_);
}

find(\&walk,$ARGV[0]);
