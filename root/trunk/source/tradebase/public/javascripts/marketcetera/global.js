/*************************************************************

  global.js v0.1
  (c) 2006 Brandon Quintana
  http://www.brandonquintana.com *** bcqwork@aim.com

  last modified: October 30, 2006

 *************************************************************/

var GlobalScripts = {
  require: function(src) {
    document.write("<script type=\"text/javascript\" src=\"" + src + "\"></script>\n");
  },
  load: function() {
    var path ="/javascripts/";

    this.require(path + "scriptaculous/scriptaculous.js");
    this.require(path + "scriptaculous/prototype.js");
    this.require(path + "marketcetera/moo.fx.js");
    this.require(path + "marketcetera/moo.fx.pack.js");
    this.require(path + "marketcetera/event-selectors.js");
    this.require(path + "marketcetera/navigation.js");
    this.require(path + "marketcetera/marketcetera.js");
  }
}

GlobalScripts.load();
