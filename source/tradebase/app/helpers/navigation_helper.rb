# Works in conjunction with navigation.js to manage the 
# left-side navigation sidebar.

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
    'equities' => 'db_maintenance',
    'dividends' => 'db_maintenance',
    'm_symbols' => 'db_maintenance',
  }
end
