require 'set'
require 'inflector'
require 'active_support/core_ext'
require 'active_support/dependencies'

class Photon

  @@registrations = Hash.new

  def Photon.on_message(message)
    @@registrations.each_value {|obj| obj.on_message(message)}
  end

  def Photon.is_registered?(file_name)
    @@registrations.include?(file_name)
  end

  def Photon.register(file_name)
    require_dependency file_name

    class_name = file_name.split('/').map {|x| Inflector.camelize(x)}.join('::')
    obj = Kernel.eval(class_name+".new")
    @@registrations[file_name] = obj
  end

  def Photon.unregister(file_name)
    @@registrations.delete(file_name)
  end
end
