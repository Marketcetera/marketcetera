# open source under a BSD license
#
# (c) 2006 WorldWide Conferencing, LLC
# (c) 2006 Robin H. Johnson <robbat2@gentoo.org>
#
# All rights reserved.
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#    * Redistributions of source code must retain the above copyright notice,
#    this list of conditions and the following disclaimer.
#    * Redistributions in binary form must reproduce the above copyright
#    notice, this list of conditions and the following disclaimer in the
#    documentation and/or other materials provided with the distribution.
#    * Neither the name of WorldWide Conferencing, LLC nor the names of its
#    contributors may be used to endorse or promote products derived from this
#    software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
#
# ChangeLog:
# robbat2, 07 May 2006:
# - added support for MATCH clause.
# - added support for multiple columns in local table and foreign table.
# - added del_fkey for downgrading in migrations.
# - start to add some documentation.
# bweaver, 11 July 2006 <brockweaver@gmail.com>:
# - changed del_fkey to remove_fkey for consistency w/ add_index remove_index
# - changed name of implicit index to include "_fkey_index" to ensure
#   uniqueness when another index exists on the same column (think habtm)
#
# Usage:
# include MigratePlus in your Migration
# add_fkey :table, :column, :ftable, :fcolumn = :id, :index = true
#
# :match = ('FULL'|'SIMPLE'|'PARTIAL')
# :on_delete = '...'
# :on_update = '...'

module MigratePlus

    def MigratePlus.included(other_mod)
        super(other_mod)

        other_mod.module_eval() do
            def self.add_fkey(table, column, ftable, options = {})
                MigratePlus.add_local_fkey(self, table, column, ftable, options)
            end
            def self.remove_fkey(table, column, ftable, options = {})
                MigratePlus.remove_local_fkey(self, table, column, ftable, options)
            end
        end
    end

    def MigratePlus.fkey_name(table, column, options) #:nodoc:
        options[:name] || "#{table}_#{Array(column).join("_")}_fkey" 
    end

    def MigratePlus.fkey_index_name(table, column, options) #:nodoc:
        options[:name] || "#{fkey_name(table, column, options)}_index" 
    end

    # Adds a new foreign key constraint from +:table+ to +:ftable+. +column+
    # can be a single Symbol, or an Array of Symbols.
    #
    # The constraint will be named after the table and the column names,
    # unless you pass +:name+ as an option.
    #
    # The 'id' will be foreign column unless +:fcolumn+ is passed as an option.
    # +:fcolumn+ may be a single Symbol, or an Array of Symbols.
    #
    # +:index+ is a boolean to control adding an index if needed, on +:column+,
    # and defaults to TRUE.
    #
    # +:match+ may be used to specify handling of NULLs (allowed values are
    # 'FULL', 'SIMPLE', 'PARTIAL').
    #
    # +:on_delete+ and +:on_update+ allow usage of database-specific logic.
    #
    # ===== Examples
    # ====== Creating a simple foreign key
    #  add_fkey(:goods, :supplier, :suppliers)
    # generates
    #  ALTER TABLE goods ADD CONSTRAINT goods_supplier_fkey
    #      FOREIGN KEY (supplier) REFERENCES suppliers (id)
    # ====== Creating a foreign key on multiple columns
    #  TODO
    # ====== Creating a named index
    #  TODO

    def MigratePlus.add_local_fkey(migrate, table, column, ftable, options = {})
        fkey = fkey_name(table, column, options)

        fcolumn = options[:fcolumn] || :id
        index = options.has_key?(:index) ? options[:index] : true

        column_string = "#{Array(column).join(", ")}" 
        fcolumn_string = "#{Array(fcolumn).join(", ")}" 

        migration = "ALTER TABLE #{table} ADD CONSTRAINT #{fkey} " 
        migration += "FOREIGN KEY (#{column_string}) " 
        migration += "REFERENCES #{ftable} (#{fcolumn_string}) " 

        # TODO: add error checking?
        migration += "MATCH #{options[:match]} " if options.has_key?(:match)
        migration += "ON DELETE #{options[:on_delete]} " if options.has_key?(:on_delete)
        migration += "ON UPDATE #{options[:on_update]} " if options.has_key?(:on_update)

        migrate.execute migration

        # changed name of index to be unique from any other index generated
        # by ActiveRecord::ConnectionAdapters::SchemaStatements.add_index
        migrate.add_index( table, [column], 
            :name => fkey_index_name(table, column, options)) if index
    end

    def MigratePlus.remove_local_fkey(migrate, table, column, ftable, options = {})
        fkey = fkey_name(table, column, options)

        migration = "ALTER TABLE #{table} DROP FOREIGN KEY #{fkey}" 
        migrate.execute migration

        index = options.has_key?(:index) ? options[:index] : true

        # changed name of index to be unique from any other index generated
        # by ActiveRecord::ConnectionAdapters::SchemaStatements.add_index
        migrate.remove_index( table, 
            :name => fkey_index_name(table, column, options)) if index
    end

end