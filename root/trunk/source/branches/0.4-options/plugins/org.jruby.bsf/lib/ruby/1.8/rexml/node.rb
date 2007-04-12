require "rexml/parseexception"

module REXML
	# Represents a node in the tree.  Nodes are never encountered except as
	# superclasses of other objects.  Nodes have siblings.
	module Node
		# @return the next sibling (nil if unset)
		def next_sibling_node
			return nil if @parent.nil?
			@parent[ @parent.index(self) + 1 ]
		end

		# @return the previous sibling (nil if unset)
		def previous_sibling_node
			return nil if @parent.nil?
			ind = @parent.index(self)
			return nil if ind == 0
			@parent[ ind - 1 ]
		end

		def to_s indent=-1
			rv = ""
			write rv,indent
			rv
		end

		def indent to, ind
 			if @parent and @parent.context and not @parent.context[:indentstyle].nil? then
 				indentstyle = @parent.context[:indentstyle]
 			else
 				indentstyle = '  '
 			end
 			to << indentstyle*ind unless ind<1
		end

		def parent?
			false;
		end


		# Visit all subnodes of +self+ recursively
		def each_recursive(&block) # :yields: node
			self.elements.each {|node|
				block.call(node)
				node.each_recursive(&block)
			}
		end

		# Find (and return) first subnode (recursively) for which the block 
    # evaluates to true. Returns +nil+ if none was found.
		def find_first_recursive(&block) # :yields: node
      each_recursive {|node|
        return node if block.call(node)
      }
      return nil
    end

    # Returns the index that +self+ has in its parent's elements array, so that
    # the following equation holds true:
    #
    #   node == node.parent.elements[node.index_in_parent]
    def index_in_parent
      parent.index(self)+1
    end
	end
end
