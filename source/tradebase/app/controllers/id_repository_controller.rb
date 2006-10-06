class IdRepositoryController < ApplicationController
  include IdRepositoryHelper
  
  def get_next_batch
    theID = IdRepository.find(:first, :lock => true)
    @current = theID.nextAllowedID
    theID.nextAllowedID = @current + NumAllowed
    theID.save
    
    @code = "success" # do the right thing here   
    render :layout => false
#    render :xml => theID.to_xml(:skip_instruct => true, :except => [ :id])
  end
end
