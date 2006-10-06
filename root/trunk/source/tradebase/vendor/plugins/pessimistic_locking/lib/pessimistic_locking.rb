module ActiveRecord
  module PessimisticLocking
    def self.append_features(base)
      super(base)
      base.extend(ClassMethods)
      class << base
        VALID_FIND_OPTIONS.push(:lock)
        alias_method(:add_limit_without_pessimistic_lock!, :add_limit!)
        alias_method(:add_limit!, :add_limit_with_pessimistic_lock!)
      end
    end

    def lock
      clear_aggregation_cache
      clear_association_cache
      record = self.class.find(self.id, :lock => true)
      @attributes.update(record.instance_variable_get('@attributes'))
    end

    module ClassMethods
      def add_limit_with_pessimistic_lock!(sql, options, scope)
        add_limit_without_pessimistic_lock!(sql, options, scope)
        if options[:lock]
          sql << " FOR UPDATE"
        end
      end
    end
  end
end
