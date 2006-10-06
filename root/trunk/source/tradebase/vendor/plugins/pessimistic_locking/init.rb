require_dependency "pessimistic_locking"

::ActiveRecord::Base.class_eval do
  include ActiveRecord::PessimisticLocking
end
