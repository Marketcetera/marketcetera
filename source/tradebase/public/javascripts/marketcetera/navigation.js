/*************************************************************

  navigation.js v0.1
  (c) 2006 Brandon Quintana
  http://www.brandonquintana.com *** bcqwork@aim.com

  last modified: October 30, 2006

 *************************************************************/

var Navigation = Class.create();

Navigation.prototype = {
  initialize: function(s) {
    this.settings = s;
    this.s = {
      stretcherClass: 'stretcher',
      toggleClass: 'display',
      navId: 'navitem'
    };
    for(var key in s) this.s[key] = s[key];

    this.pullNavigation();

    this.navAccordion = new fx.Accordion(this.toggles, this.stretchers, {opacity: true, duration: 400});

    if(!this.checkHash()) this.navAccordion.showThisHideOpen(this.stretchers[0]); 
  },
  checkHash : function() {
    var found = false;
    if ($(this.s['navId'])) {
      for (var i = 0; i < this.toggles.length; i++) {
        if (this.toggles[i].title.indexOf($(this.s['navId']).innerHTML) != -1) {
          this.navAccordion.showThisHideOpen(this.stretchers[i]);
          return true;
        }
      }
    }
    return found;
  },
  pullNavigation : function() {
    this.stretchers = document.getElementsByClassName(this.s['stretcherClass']);
    this.toggles = document.getElementsByClassName(this.s['toggleClass']);
  }
};

EventSelectors.addLoadEvent(function() {
  new Navigation();
});
