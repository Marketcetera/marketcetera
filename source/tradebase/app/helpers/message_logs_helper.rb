module MessageLogsHelper

  
  # if the field is present in the message returns the string value of it
  # else returns a space
  def getStringFieldValueIfPresent(inMessage, inField)
    return (inMessage.isSetField(inField) ) ? inMessage.getField(inField).getString() : ''
  end
  def getHeaderStringFieldValueIfPresent(inMessage, inField)
    return (inMessage.getHeader().isSetField(inField) ) ? inMessage.getHeader().getField(inField).getString() : ''
  end
   
  # formats the incoming bigDecimal to trim the digits after the decimal
  # in case they are too wide.
  def formatBigDecimal(number)
    if(number.nil?)
      return nil
    end
    
    if (number.remainder(1).to_s.length > 4)      
      return sprintf('%.4f', number)
    end
    return number.to_s
  end
   
# Human Strings
  StatusFilled = "Filled"
  StatusPartialFilled = "Partial Fill"
  StatusCanceled = "Canceled"
  StatusRejected = "Rejected"
  StatusNew = "New"
  StatusPendingCancel = "Pending Cancel"
  
  # codes
  FilledCode = Quickfix::OrdStatus_FILLED()
  PartialFilledCode = Quickfix::OrdStatus_PARTIALLY_FILLED()
  CanceledCode = Quickfix::OrdStatus_CANCELED()
  RejectedCode = Quickfix::OrdStatus_REJECTED()
  NewCode = Quickfix::OrdStatus_NEW()
  PendingCancelCode = Quickfix::OrdStatus_PENDING_CANCEL()
       
   
   def getHumanOrdStatus(inStatusCode)
    case inStatusCode
      when FilledCode
        return StatusFilled
      when PartialFilledCode
        return StatusPartialFilled
      when CanceledCode
        return StatusCanceled
      when RejectedCode
        return StatusRejected
      when NewCode
        return StatusNew
      when PendingCancelCode
        return StatusPendingCancel
      else return "Unknown OrdStatus: " + inStatusCode
    end
  end 
   
    
end
