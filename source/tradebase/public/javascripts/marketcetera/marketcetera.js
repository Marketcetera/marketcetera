/** Collection of useful Marketcetera javascript actions 
 * $Id$
 * @author Toli Kuznets
 */

/* Toggle the show/hide of the date range selection div in the import trade page */
var alreadyOpen = false;

var subset = {
  '#subset' : function(element) {
    if(element.checked == false) new Effect.BlindUp('subset_content', {duration: 0.0});
    if(element.checked == true) alreadyOpen = true;
  },
  '#subset:click': function(element) {
    if (alreadyOpen == false) new Effect.BlindDown('subset_content', {duration: 0.5});
    alreadyOpen = true;
  },
  '#all:click': function(element) {
    new Effect.BlindUp('subset_content', {duration: 0.5});
    alreadyOpen = false;
  }
}

EventSelectors.addLoadEvent(function() {
  EventSelectors.start(subset);
});
