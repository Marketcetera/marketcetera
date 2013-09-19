# Run-time Windows and Unix executable script generator.
#
# Author: tlerios@marketcetera.com
# Since: 0.5.0
# Version: $Id$
# $License$
#
# FUTURE: If messages are an output of the generated scripts, then 
#         those messages will have to be localized using resource
#         bundles, not hard-coded.


use strict;


# Error checking.

if (@ARGV%3) {
        warn "\n";
        warn "Usage: ",__FILE__," [<root> <script_name> <class>]+\n";
        die "\n";
}

# Configuration.

my($commonArgs)='-Xms384m -Xmx600m -XX:MaxPermSize=512m';
my($commonArgsClient)=$commonArgs.' -client';
my($commonArgsServer)=$commonArgs.' -server';

# Generate scripts.

while (@ARGV) {
        my($root)=shift(@ARGV);
        my($scriptBase)=shift(@ARGV);
        my($class)=shift(@ARGV);

    # Shorthands.

        my($artifact)=($root=~m/[\/\\]([^\/\\]+)$/io);
        my($bin)=$root.'/bin';
        my($logs)=$root.'/logs';

    # Set the appropriate jvm args

        if (($scriptBase eq "ors") || ($scriptBase eq "strategyagent")) {
            $commonArgs=$commonArgsServer
        } else {
            $commonArgs=$commonArgsClient
        }

    # Create output directory.

        mkdir($bin) if (!(-e $bin));
        mkdir($logs) if (!(-e $logs));

    # Create the Windows script.

        my($sep)="\r\n";
        my($script)=$bin.'/'.$scriptBase.'.bat';
        open(OUT,'>'.$script);
        binmode(OUT);
        print OUT '@ECHO OFF'.$sep.$sep;

        print OUT 'REM'.$sep;
        print OUT 'REM This startup file is automatically generated by tools/scripts/createScript.pl'.$sep;
        print OUT 'REM'.$sep.$sep;

        print OUT 'SETLOCAL'.$sep.$sep;

        print OUT 'CALL "%~dp0..\\..\\setEnv.bat"'.$sep.$sep;

        print OUT 'SET APPLICATION_DIR='.$artifact.$sep.$sep;

        if ($artifact eq "ors") {  # for ors and orsadmin
            print OUT 'SET started_mysql=true'.$sep.$sep;

            print OUT 'CD %METC_HOME%\\mysql'.$sep;
            print OUT 'bin\\mysqladmin --verbose --host=%MYSQL_HOST% --port=%MYSQL_PORT% --user=root --password=%MYSQL_ROOT_PASSWORD% status > nul 2>&1'.$sep;
            print OUT 'IF %ERRORLEVEL% NEQ 0 GOTO STARTMYSQL'.$sep;
            print OUT 'IF %ERRORLEVEL% EQU 0 GOTO STARTAPP_NOMYSQL'.$sep.$sep;

            print OUT ':STARTMYSQL'.$sep;
            print OUT 'CD %METC_HOME%\\sql'.$sep;
            print OUT 'CALL start_mysql.bat'.$sep;
            print OUT 'IF %ERRORLEVEL% NEQ 0 GOTO PAUSEEND'.$sep;
            print OUT 'GOTO STARTAPP'.$sep.$sep;

            print OUT ':STARTAPP_NOMYSQL'.$sep;
            print OUT 'SET started_mysql=false'.$sep.$sep;

            print OUT ':STARTAPP'.$sep;
        }

        print OUT 'CD %METC_HOME%\\%APPLICATION_DIR%'.$sep.$sep;

        print OUT 'SET THE_CLASSPATH=.\\conf'.$sep;
        print OUT 'FOR /F %%f IN (\'DIR /B /O:N .\\lib\\*.jar\') DO CALL :SETCP .\\lib\\%%f'.$sep;
        print OUT 'FOR /F %%f IN (\'DIR /B /O:N ..\\jre\\lib\\*.jar\') DO CALL :SETCP ..\\jre\\lib\\%%f'.$sep.$sep;

        print OUT 'java.exe '.$commonArgs.
                ' -Dorg.marketcetera.appDir=%METC_HOME%\\%APPLICATION_DIR%^'.$sep;

        if ($artifact eq "strategyagent") {
            print OUT ' -Dstrategy.classpath=%METC_HOME%\\%APPLICATION_DIR%\\src^'.$sep;
        }

        print OUT ' -cp "%THE_CLASSPATH%"^'.$sep;
        print OUT ' '.$class.' %*'.$sep.$sep;

        if ($artifact eq "ors") {  # for ors and orsadmin
            print OUT 'IF %started_mysql%==false GOTO END'.$sep.$sep;

            print OUT ':STOPMYSQL'.$sep;
            print OUT 'CD %METC_HOME%\\sql'.$sep;
            print OUT 'CALL stop_mysql.bat'.$sep.$sep;
        }

        print OUT 'GOTO END'.$sep.$sep;

        print OUT ':SETCP'.$sep;
        print OUT 'SET THE_CLASSPATH=%THE_CLASSPATH%;%1'.$sep;
        print OUT 'GOTO:EOF'.$sep.$sep;

        if ($artifact eq "ors") {  # for ors and orsadmin
            print OUT ':PAUSEEND'.$sep;
            print OUT 'PAUSE'.$sep;
            print OUT 'GOTO END'.$sep.$sep;
        }

        print OUT ':END'.$sep;
        print OUT 'ENDLOCAL'.$sep;
        close(OUT);

    # Create the Unix script.

        my($sep)="\n";
        my($script)=$bin.'/'.$scriptBase.'.sh';
        open(OUT,'>'.$script);
        binmode(OUT);
        print OUT '#!/bin/sh'.$sep.$sep;

        print OUT '##'.$sep;
        print OUT '## This startup file is automatically generated by tools/scripts/createScript.pl'.$sep;
        print OUT '##'.$sep.$sep;

        if ($artifact eq "ors") { # for ors and orsadmin
            print OUT 'mysql_stop()'.$sep;
            print OUT '{'.$sep;
            print OUT '    if [ "${started_mysql}" = "true" ]; then'.$sep;
            print OUT '        ${METC_HOME}/sql/stop_mysql.sh'.$sep;
            print OUT '    fi'.$sep;
            print OUT '    exit $1'.$sep;
            print OUT '}'.$sep.$sep;

            print OUT 'trap mysql_stop EXIT INT TERM'.$sep.$sep;
        }

        print OUT '. "$(dirname $0)/../../setEnv.sh"'.$sep.$sep;

        print OUT 'APPLICATION_DIR='.$artifact.$sep.$sep;

        if ($artifact eq "ors") {  # for ors and orsadmin
            print OUT 'started_mysql=true'.$sep.$sep;

            print OUT 'cd ${METC_HOME}/mysql'.$sep;
            print OUT './bin/mysqladmin --verbose --host=${MYSQL_HOST} --port=${MYSQL_PORT} --user=root --password=${MYSQL_ROOT_PASSWORD} status > /dev/null 2>&1'.$sep;
            print OUT 'if [ $? -ne 0 ]; then'.$sep;
            print OUT '    ${METC_HOME}/sql/start_mysql.sh'.$sep;
            print OUT '    if [ $? -ne 0 ]; then'.$sep;
            print OUT '        exit 1'.$sep;
            print OUT '    fi'.$sep;
            print OUT 'else'.$sep;
            print OUT '    started_mysql=false'.$sep;
            print OUT 'fi'.$sep.$sep;
        }

        print OUT 'cd ${METC_HOME}/${APPLICATION_DIR}'.$sep.$sep;

        print OUT 'THE_CLASSPATH=./conf'.$sep;
        print OUT 'for file in `ls -1 ./lib/*.jar`'.$sep;
        print OUT 'do'.$sep;
        print OUT '    THE_CLASSPATH=${THE_CLASSPATH}:${file}'.$sep;
        print OUT 'done'.$sep.$sep;

        print OUT 'java '.$commonArgs.
                ' -Dorg.marketcetera.appDir=${METC_HOME}/${APPLICATION_DIR}\\'.$sep;

        if ($artifact eq "strategyagent") {
            print OUT ' -Dstrategy.classpath=${METC_HOME}/${APPLICATION_DIR}/src\\'.$sep;
            print OUT ' -Djava.library.path=${METC_HOME}/${APPLICATION_DIR}/modules/lib\\'.$sep;
        }

        print OUT ' -cp "${THE_CLASSPATH}"\\'.$sep;
        print OUT ' '.$class.' $*'.$sep;
        close(OUT);
        chmod(0755,$script);
}
