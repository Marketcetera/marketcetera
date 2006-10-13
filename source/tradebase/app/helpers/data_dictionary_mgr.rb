# Helper class to get the human names of Quickfix fields from FIX spec
# $Id$
class DataDictionaryMgr
  private_class_method :new
  @@mgr = Quickfix::DataDictionary.new("lib/FIX42.xml")
  
  def DataDictionaryMgr.get_field_name(fieldNumber)
    @@mgr.getFieldName(fieldNumber)
  end
  
  def DataDictionaryMgr.get_value_name(fieldNumber, value)
    result = @@mgr.getValueName(fieldNumber, value)
    return (result.nil?) ? nil : result.gsub('_', ' ')
  end
  
  def DataDictionaryMgr.get_field_tag(fieldName)
    @@mgr.getFieldTag(fieldName)
  end
    
end
