# Public subversion script configuration.
#
# Author: tlerios@marketcetera.com
# Since: 0.8.0
# Version: $Id$
# $License$


use File::Spec;


# Prevent multiple inclusions.

$::pubConfig=1;

# Load private counterpart.

my($volume,$dir,$file)=File::Spec->splitpath(__FILE__);
my($prvConfig)=File::Spec->catpath
	($volume,$dir.'../../../private/tools/scripts',$file);
if (!defined($::prvConfig) && (-e $prvConfig)) {
	require $prvConfig;
}

# Set configuration.

@::externals=(
	@::externals,
	'public/source/tradebase/vendor/plugins/debug_view_helper',
);

@::executables=(
	@::executables,
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

1;
