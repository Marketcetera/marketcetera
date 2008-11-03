// Copyright (c) 2005-2006 Justin Palmer (http://encytemedia.com)
// Examples and documentation (http://encytemedia.com/event-selectors)
// Inspired by the work of Ben Nolan's Behaviour (http://bennolan.com/behaviour)
// Modified by Leo Plaw August 2006 (http://leo-plaw.guildmedia.net)

var EventSelectors = {
	version: '1.0_pre',
	cache: [],
	rules: {},

	register: function(newRules){
		for (var property in newRules) { this.rules[property] = newRules[property]; }
	},

  start: function(rules) {
		this.register(rules);
		this.timer = new Array();
		this._extendRules();
		this.assign();
	},

	assign: function() {
		var observer = null;
		this._unloadCache();
		this.rules._each(function(rule) {
			var selectors = $A(rule.key.split(','));
			selectors.each(function(selector) {
				var pair = selector.split(/:(?=[a-z]+$)/);
				var event = pair[1];
				$$(pair[0]).each(function(element) {
					if(pair[1] == '' || pair.length == 1) return rule.value(element);
					if(event.toLowerCase() == 'loaded') {
						this.timer[pair[0]] = setInterval(this._checkLoaded.bind(this, element, pair[0], rule), 15);
					} else {
						observer = function(event) {
							var element = this;					// Event.element(event);
							if (element.nodeType == 3)	// Safari Bug (Fixed in Webkit)
								element = element.parentNode;
							rule.value($(element), event);
						}
						this.cache.push([element, event, observer]);
						Event.observe(element, event, observer);
					}
				}.bind(this));
			}.bind(this));
		}.bind(this));
	},

	apply: function() {
		this.assign();
	},

	addRules: function(newRules) {
		Object.extend(newRules, this.rules);
	},

	addLoadEvent: function(func){
		Event.observe(window, 'load', func);
	},

	_unloadCache: function() {
		if (!this.cache) return;
		for (var i = 0; i < this.cache.length; i++) {
			Event.stopObserving.apply(this, this.cache[i]);
			this.cache[i][0] = null;
		}
		this.cache = [];
	},

	_checkLoaded: function(element, timer, rule) {
		var node = $(element);
		if(element.tagName != 'undefined') {
			clearInterval(this.timer[timer]);
			rule.value(node);
		}
	},

	_extendRules: function() {
		Object.extend(this.rules, {
		 _each: function(iterator) {
			 for (key in this) {
				 if(key == '_each') continue;
				 var value = this[key];
				 var pair = [key, value];
				 pair.key = key;
				 pair.value = value;
				 iterator(pair);
			 }
		 }
		});
	}
}

// Remove/Comment this if you do not wish to reapply Rules automatically
// on Ajax request.
Ajax.Responders.register({
	onComplete: function() { EventSelectors.apply();}
})