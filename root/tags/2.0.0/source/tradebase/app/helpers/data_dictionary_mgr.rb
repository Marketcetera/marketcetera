# Helper class to get the human names of Quickfix fields from FIX spec
# $Id$
class DataDictionaryMgr
  private_class_method :new
  @@mgr = Quickfix::DataDictionary.new("#{RAILS_ROOT}/lib/FIX42.xml")
  

  def DataDictionaryMgr.get_field_name(field)
    @@mgr.getFieldName(field.getField)
  end
  
  # pass in quickfix field and value constant, get back human value
  def DataDictionaryMgr.get_value_name(field, value)
    result = @@mgr.getValueName(field.getField, value)
    return (result.nil?) ? nil : result.gsub('_', ' ')
  end
  
  # pass in a Quickfix field name (SIde), get back the numerical side for it
  def DataDictionaryMgr.get_field_tag(fieldName)
    @@mgr.getFieldTag(fieldName)
  end
    
end
