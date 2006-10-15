/** Collection of usefule Marketcetera javascript actions 
 * $Id$
 * @author Toli Kuznets
 */
 
/* Toggles the checkboxes in the form on/off */ 
function toggleCheckboxes(formName, value){
	var boxes = Form.getInputs(formName, 'checkbox')
	options = $A(boxes);
	options.each( function(oneBox){
		oneBox.checked = value;
	});
}	
	
/*  Toggles the date selection fields on/off when date selection radio buttons are switched */
function toggleDivSelection(divName, value) {
    var divs = $(divName)
    if(value)   divs.style.display = "block";
    else divs.style.display = "none";
} 