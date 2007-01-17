# generic helper for all display-related helper code
module DisplayHelper

  # Helper function for making sure that if an empty string comes in to display,
  # we alwyas return an &nbsp otherwise in HTML empty divs will end up collapsing
  # and we will be misaligned.
  # see ticket:74 for more info
  def df(str)
    if(str.blank?)
       return "&nbsp;"
    end
    h(str)
  end
end