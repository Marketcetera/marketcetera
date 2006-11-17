/** Collection of useful Marketcetera javascript actions 
 * $Id$
 * @author Toli Kuznets
 */

/* Toggle the show/hide of the date range selection div in the import trade page */
var subset = {
  '#subset' : function(element) {
    if(element.checked == false) new Effect.BlindUp('subset_content', {duration: 0.0});
  },
  '#subset:click': function(element) {
    new Effect.BlindDown('subset_content', {duration: 0.5});
  },
  '#all:click': function(element) {
    new Effect.BlindUp('subset_content', {duration: 0.5});
  }
}

EventSelectors.addLoadEvent(function() {
  EventSelectors.start(subset);
});
