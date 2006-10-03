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