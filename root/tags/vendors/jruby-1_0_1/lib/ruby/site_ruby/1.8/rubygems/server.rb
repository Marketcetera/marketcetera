require 'rubygems'
Gem.manage_gems
require 'webrick'
require 'yaml'
require 'optparse'
require 'rdoc/template'

# Gem::Server and allows users to serve gems for consumption by
# `gem --remote-install`.
# 
# gem_server starts an HTTP server on the given port and serves the folowing:
# * "/" - Browsing of gem spec files for installed gems
# * "/yaml" - Full yaml dump of metadata for installed gems
# * "/gems" - Direct access to download the installable gems
#
# == Usage
#
#   gem_server [-p portnum] [-d gem_path]
#
# port_num:: The TCP port the HTTP server will bind to
# gem_path::
#   Root gem directory containing both "cache" and "specifications"
#   subdirectories.
class Gem::Server

  DOC_TEMPLATE = <<-WEBPAGE
<?xml version="1.0" encoding="iso-8859-1"?>
<!DOCTYPE html 
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title>RubyGems Documentation Index</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <link rel="stylesheet" href="rdoc-style.css" type="text/css" media="screen" />
</head>
<body>
  <div id="fileHeader">
    <h1>RubyGems Documentation Index</h1>
  </div>
  <!-- banner header -->

<div id="bodyContent">
  <div id="contextContent">
    <div id="description">
      <h1>Summary</h1>
<p>There are %gem_count% gems installed:</p>
<p>
START:specs
IFNOT:is_last
<a href="#%name%">%name%</a>,
ENDIF:is_last
IF:is_last
<a href="#%name%">%name%</a>.
ENDIF:is_last
END:specs
<h1>Gems</h1>

<dl>
START:specs
<dt>
IF:first_name_entry
  <a name="%name%"></a>
ENDIF:first_name_entry
<b>%name% %version%</b>
IF:rdoc_installed
  <a href="%doc_path%">[rdoc]</a>
ENDIF:rdoc_installed
IFNOT:rdoc_installed
  <span title="rdoc not installed">[rdoc]</span>
ENDIF:rdoc_installed
IF:homepage
<a href="%homepage%" target="_blank" title="%homepage%">[www]</a>
ENDIF:homepage
IFNOT:homepage
<span title="no homepage available">[www]</span>
ENDIF:homepage
IF:has_deps
 - depends on 
START:dependencies
IFNOT:is_last
<a href="#%name%" title="%version%">%name%</a>,
ENDIF:is_last
IF:is_last
<a href="#%name%" title="%version%">%name%</a>.
ENDIF:is_last
END:dependencies
ENDIF:has_deps
</dt>
<dd>
%summary%
IF:executables
  <br/>

IF:only_one_executable
    Executable is
ENDIF:only_one_executable
  
IFNOT:only_one_executable
    Executables are
ENDIF:only_one_executable
 
START:executables
IFNOT:is_last
      <span class="context-item-name">%executable%</span>,
ENDIF:is_last
IF:is_last
      <span class="context-item-name">%executable%</span>.
ENDIF:is_last
END:executables
ENDIF:executables
<br/>
<br/>
</dd>
END:specs
</dl>

    </div>
   </div>
  </div>
<div id="validator-badges">
  <p><small><a href="http://validator.w3.org/check/referer">[Validate]</a></small></p>
</div>
</body>
</html>
  WEBPAGE

  # CSS is copy & paste from rdoc-style.css, RDoc V1.0.1 - 20041108
  RDOC_CSS = <<-RDOCCSS
body {
    font-family: Verdana,Arial,Helvetica,sans-serif;
    font-size:   90%;
    margin: 0;
    margin-left: 40px;
    padding: 0;
    background: white;
}

h1,h2,h3,h4 { margin: 0; color: #efefef; background: transparent; }
h1 { font-size: 150%; }
h2,h3,h4 { margin-top: 1em; }

a { background: #eef; color: #039; text-decoration: none; }
a:hover { background: #039; color: #eef; }

/* Override the base stylesheets Anchor inside a table cell */
td > a {
  background: transparent;
  color: #039;
  text-decoration: none;
}

/* and inside a section title */
.section-title > a {
  background: transparent;
  color: #eee;
  text-decoration: none;
}

/* === Structural elements =================================== */

div#index {
    margin: 0;
    margin-left: -40px;
    padding: 0;
    font-size: 90%;
}


div#index a {
    margin-left: 0.7em;
}

div#index .section-bar {
   margin-left: 0px;
   padding-left: 0.7em;
   background: #ccc;
   font-size: small;
}


div#classHeader, div#fileHeader {
    width: auto;
    color: white;
    padding: 0.5em 1.5em 0.5em 1.5em;
    margin: 0;
    margin-left: -40px;
    border-bottom: 3px solid #006;
}

div#classHeader a, div#fileHeader a {
    background: inherit;
    color: white;
}

div#classHeader td, div#fileHeader td {
    background: inherit;
    color: white;
}


div#fileHeader {
    background: #057;
}

div#classHeader {
    background: #048;
}


.class-name-in-header {
  font-size:  180%;
  font-weight: bold;
}


div#bodyContent {
    padding: 0 1.5em 0 1.5em;
}

div#description {
    padding: 0.5em 1.5em;
    background: #efefef;
    border: 1px dotted #999;
}

div#description h1,h2,h3,h4,h5,h6 {
    color: #125;;
    background: transparent;
}

div#validator-badges {
    text-align: center;
}
div#validator-badges img { border: 0; }

div#copyright {
    color: #333;
    background: #efefef;
    font: 0.75em sans-serif;
    margin-top: 5em;
    margin-bottom: 0;
    padding: 0.5em 2em;
}


/* === Classes =================================== */

table.header-table {
    color: white;
    font-size: small;
}

.type-note {
    font-size: small;
    color: #DEDEDE;
}

.xxsection-bar {
    background: #eee;
    color: #333;
    padding: 3px;
}

.section-bar {
   color: #333;
   border-bottom: 1px solid #999;
    margin-left: -20px;
}


.section-title {
    background: #79a;
    color: #eee;
    padding: 3px;
    margin-top: 2em;
    margin-left: -30px;
    border: 1px solid #999;
}

.top-aligned-row {  vertical-align: top }
.bottom-aligned-row { vertical-align: bottom }

/* --- Context section classes ----------------------- */

.context-row { }
.context-item-name { font-family: monospace; font-weight: bold; color: black; }
.context-item-value { font-size: small; color: #448; }
.context-item-desc { color: #333; padding-left: 2em; }

/* --- Method classes -------------------------- */
.method-detail {
    background: #efefef;
    padding: 0;
    margin-top: 0.5em;
    margin-bottom: 1em;
    border: 1px dotted #ccc;
}
.method-heading {
  color: black;
  background: #ccc;
  border-bottom: 1px solid #666;
  padding: 0.2em 0.5em 0 0.5em;
}
.method-signature { color: black; background: inherit; }
.method-name { font-weight: bold; }
.method-args { font-style: italic; }
.method-description { padding: 0 0.5em 0 0.5em; }

/* --- Source code sections -------------------- */

a.source-toggle { font-size: 90%; }
div.method-source-code {
    background: #262626;
    color: #ffdead;
    margin: 1em;
    padding: 0.5em;
    border: 1px dashed #999;
    overflow: hidden;
}

div.method-source-code pre { color: #ffdead; overflow: hidden; }

/* --- Ruby keyword styles --------------------- */

.standalone-code { background: #221111; color: #ffdead; overflow: hidden; }

.ruby-constant  { color: #7fffd4; background: transparent; }
.ruby-keyword { color: #00ffff; background: transparent; }
.ruby-ivar    { color: #eedd82; background: transparent; }
.ruby-operator  { color: #00ffee; background: transparent; }
.ruby-identifier { color: #ffdead; background: transparent; }
.ruby-node    { color: #ffa07a; background: transparent; }
.ruby-comment { color: #b22222; font-weight: bold; background: transparent; }
.ruby-regexp  { color: #ffa07a; background: transparent; }
.ruby-value   { color: #7fffd4; background: transparent; }
  RDOCCSS

  def self.process_args(args)
    options = {}
    options[:port] = 8808
    options[:gemdir] = Gem.dir
    options[:daemon] = false

    opts = OptionParser.new do |opts|
      opts.on_tail("--help", "show this message") do
        puts opts
        exit
      end

      opts.on('-p', '--port=PORT', "Specify the port to listen on") do |port|
        options[:port] = port
      end

      opts.on('-d', '--dir=GEMDIR', 
              "Specify the directory from which to serve Gems") do |gemdir|
        options[:gemdir] = gemdir
      end

      opts.on(      '--daemon', "Run as a daemon") do |daemon|
        options[:daemon] = daemon
      end

    end

    opts.parse! args

    options
  end

  def self.run(args = ARGV)
    options = process_args args
    new(options[:gemdir], options[:port], options[:daemon]).run
  end

  def initialize(gemdir, port, daemon)
    Socket.do_not_reverse_lookup=true

    @gemdir = gemdir
    @port = port
    @daemon = daemon
  end

  def run
    WEBrick::Daemon.start if @daemon

    spec_dir = File.join @gemdir, "specifications"

    s = WEBrick::HTTPServer.new :Port => @port

    s.mount_proc("/yaml") do |req, res|
      res['content-type'] = 'text/plain'
      res['date'] = File.stat(spec_dir).mtime
      res.body << Gem::SourceIndex.from_gems_in(spec_dir).to_yaml
    end

    s.mount_proc("/rdoc-style.css") do |req, res|
      res['content-type'] = 'text/css'
      res['date'] = File.stat(spec_dir).mtime
      res.body << RDOC_CSS
    end

    s.mount_proc("/") do |req, res|
      specs = []
      total_file_count = 0

      Gem::SourceIndex.from_gems_in(spec_dir).each do |path, spec|
        total_file_count += spec.files.size
        deps = spec.dependencies.collect { |dep|
          { "name"    => dep.name, 
            "version" => dep.version_requirements.to_s, }
        }
        deps = deps.sort_by { |dep| [dep["name"].downcase, dep["version"]] }
        deps.last["is_last"] = true unless deps.empty?

        # executables
        executables = spec.executables.sort.collect { |exec| {"executable" => exec} }
        executables = nil if executables.empty?
        executables.last["is_last"] = true if executables

        specs << {
          "authors"        => spec.authors.sort.join(", "),
          "date"           => spec.date.to_s,
          "dependencies"   => deps,
          "doc_path"       => ('/doc_root/' + spec.full_name + '/rdoc/index.html'),
          "executables"    => executables,
          "only_one_executable" => (executables && executables.size==1),
          "full_name"      => spec.full_name,
          "has_deps"       => !deps.empty?,
          "homepage"       => spec.homepage,
          "name"           => spec.name,
          "rdoc_installed" => Gem::DocManager.new(spec).rdoc_installed?,
          "summary"        => spec.summary,
          "version"        => spec.version.to_s,
        }
      end

      specs << {
        "authors" => "Chad Fowler, Rich Kilmer, Jim Weirich, Eric Hodel and others",
        "dependencies" => [],
        "doc_path" => "/doc_root/rubygems-#{Gem::RubyGemsVersion}/rdoc/index.html",
        "executables" => [{"executable" => 'gem', "is_last" => true}],
        "only_one_executable" => true,
        "full_name" => "rubygems-#{Gem::RubyGemsVersion}",
        "has_deps" => false,
        "homepage" => "http://rubygems.org/",
        "name" => 'rubygems',
        "rdoc_installed" => true,
        "summary" => "RubyGems itself",
        "version" => Gem::RubyGemsVersion,
      }

      specs = specs.sort_by { |spec| [spec["name"].downcase, spec["version"]] }
      specs.last["is_last"] = true

      # tag all specs with first_name_entry 
      last_spec = nil
      specs.each do |spec|
        is_first = last_spec.nil? || (last_spec["name"].downcase != spec["name"].downcase)
        spec["first_name_entry"] = is_first
        last_spec = spec
      end

      # create page from template
      template = TemplatePage.new(DOC_TEMPLATE)
      res['content-type'] = 'text/html'
      template.write_html_on res.body,
                             "gem_count" => specs.size.to_s, "specs" => specs,
                             "total_file_count" => total_file_count.to_s
    end

    paths = { "/gems" => "/cache/", "/doc_root" => "/doc/" }
    paths.each do |mount_point, mount_dir|
      s.mount(mount_point, WEBrick::HTTPServlet::FileHandler,
              File.join(@gemdir, mount_dir), true)
    end

    trap("INT") { s.shutdown; exit! }
    trap("TERM") { s.shutdown; exit! }

    s.start
  end

end

