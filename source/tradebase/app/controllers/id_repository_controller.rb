class IdRepositoryController < ApplicationController
  include IdRepositoryHelper
  
  def get_next_batch
    theID = IdRepository.find(:first, :lock => true)
    @current = theID.nextAllowedID
    theID.nextAllowedID = @current + NumAllowed
    theID.save
    
    @num_allowed = NumAllowed
    render :layout => false
  end
end
