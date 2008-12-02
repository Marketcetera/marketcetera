require 'set'
require 'inflector'
require 'active_support/core_ext'
require 'active_support/dependencies'

class Photon

  def Photon.new_instance(file_name)
    require_dependency file_name

    class_name = file_name.split('/').map {|x| Inflector.camelize(x)}.join('::')
    Kernel.eval(class_name+".new")
  end
end
