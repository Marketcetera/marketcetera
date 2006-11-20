/** Collection of useful Marketcetera javascript actions 
 * $Id$
 * @author Toli Kuznets
 */

/* Toggle the show/hide of the date range selection div in the import trade page */
var open = false;

var subset = {
  '#subset' : function(element) {
    if(element.checked == false) new Effect.BlindUp('subset_content', {duration: 0.0});
    if(element.checked == true) open = true;
  },
  '#subset:click': function(element) {
    if (open == false) new Effect.BlindDown('subset_content', {duration: 0.5});
    open = true;
  },
  '#all:click': function(element) {
    new Effect.BlindUp('subset_content', {duration: 0.5});
    open = false;
  }
}

EventSelectors.addLoadEvent(function() {
  EventSelectors.start(subset);
});
