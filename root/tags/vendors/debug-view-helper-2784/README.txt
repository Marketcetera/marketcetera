= debug_view_helper plugin for Rails

debug_view_helper is a plugin that adds easily accessible debug information
into your views. The plugin makes it easy to add a button that will popup a 
new window that displays the following debug data;

* Request Parameters
* Session Variables
* Flash Variables
* Assigned Template Variables

Typically you add code such as the following to the bottom of your layout 
that exposes the debug button in development mode.

<% if RAILS_ENV == 'development' %>
<center><button onclick="show_debug_popup(); return false;">Show debug popup</button></center>
<%= debug_popup %>
<% end %>

Alternatively you can expose the debug information inline via <%= debug_inline %>

== Details

License: Released under the MIT license.
Latest Version: http://www.realityforge.org/svn/public/code/debug-view-helper/trunk/

== Credits

Marten Veldthuis for the initial idea in Epilog. 
Peter Donald <peter at realityforge dot org>.
John Dell for suggestion to add debug_inline feature.
Rob Sharp for minor table style changes.
Chris Wanstrath for bugfix where values contains newlines.
Craig Steinberger for updates to rails 1.2.1.
Joachim Fermstad for modification to support controllers in nested modules.