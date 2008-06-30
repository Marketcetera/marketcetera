module MessageLogsHelper
  require 'quickfix'
  
  # if the field is present in the message returns the string value of it
  # else returns a space
  def getStringFieldValueIfPresent(inMessage, inField)
    return (inMessage.isSetField(inField) ) ? inMessage.getField(inField).getString() : ''
  end
  def getHeaderStringFieldValueIfPresent(inMessage, inField)
    return (inMessage.getHeader().isSetField(inField) ) ? inMessage.getHeader().getField(inField).getString() : ''
  end
   
# Human Strings
  unless defined?(StatusFilled)
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
  end       
   
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
