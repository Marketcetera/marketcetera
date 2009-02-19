# Works in conjunction with navigation.js to manage the 
# left-side navigation sidebar.
# You need to add the mappings here once you add new controllers or sections to navigation bar

module NavigationHelper
  
  NAVIGATION_MAPPING = {
    'positions' => 'position', 
    'trades' => 'trades', 
    'welcome' => 'trades',
    'message_logs' => 'trades',
    'queries' => 'reports',
    'positions_queries' => 'reports',
    'accounts' => 'db_maintenance',
    'currencies' => 'db_maintenance',
    'currency_pairs' => 'db_maintenance',
    'equities' => 'db_maintenance',
    'dividends' => 'db_maintenance',
    'm_symbols' => 'db_maintenance',
    'pnl' => 'pnl',
    'marks' => 'pnl',
    'import_marks' => 'pnl',
    'diagnostics' => 'help',
    'update_ors' => 'help'
  }
end
