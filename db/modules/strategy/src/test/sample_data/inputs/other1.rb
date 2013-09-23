require "subfolder/other2"
#####################################################
# Dependent class depends on a class in a subfolder #
#####################################################
class Other1
  def Other1.action1
    return "Other1 action complete"
  end
  def action2
    other2 = Other2.new
    return other2.action1
  end
end
