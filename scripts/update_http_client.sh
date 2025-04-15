#!/bin/bash

# Script to update HTTP client imports from Apache HTTP Client 4 to 5
# Usage: ./update_http_client.sh <directory>

if [ -z "$1" ]; then
  echo "Usage: $0 <directory>"
  exit 1
fi

DIR_PATH="$1"

# Create a backup directory
BACKUP_DIR="${DIR_PATH}/http_backup_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"

echo "Backing up files to: $BACKUP_DIR"

# Find all Java files with org.apache.http imports
FILES_TO_CONVERT=$(grep -l "import org.apache.http" $(find "$DIR_PATH" -name "*.java") 2>/dev/null)

# Function to convert a file
convert_file() {
  local file="$1"
  echo "Converting: $file"
  
  # Create backup
  cp "$file" "$BACKUP_DIR/$(basename "$file")"
  
  # Replace imports
  # Core package changes
  sed -i '' 's/import org.apache.http.HttpEntity;/import org.apache.hc.core5.http.HttpEntity;/g' "$file"
  sed -i '' 's/import org.apache.http.HttpResponse;/import org.apache.hc.core5.http.HttpResponse;/g' "$file"
  sed -i '' 's/import org.apache.http.StatusLine;/import org.apache.hc.core5.http.StatusLine;/g' "$file"
  sed -i '' 's/import org.apache.http.entity.StringEntity;/import org.apache.hc.core5.http.io.entity.StringEntity;/g' "$file"
  
  # Client package changes
  sed -i '' 's/import org.apache.http.client.methods.HttpPost;/import org.apache.hc.client5.http.classic.methods.HttpPost;/g' "$file"
  sed -i '' 's/import org.apache.http.client.methods.CloseableHttpResponse;/import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;/g' "$file"
  sed -i '' 's/import org.apache.http.client.entity.UrlEncodedFormEntity;/import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;/g' "$file"
  
  # Implementation package changes
  sed -i '' 's/import org.apache.http.impl.client.CloseableHttpClient;/import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;/g' "$file"
  sed -i '' 's/import org.apache.http.impl.client.HttpClients;/import org.apache.hc.client5.http.impl.classic.HttpClients;/g' "$file"
  
  # Generic imports that might be used
  sed -i '' 's/import org.apache.http./import org.apache.hc.core5.http./g' "$file"
}

echo "Found $(echo "$FILES_TO_CONVERT" | wc -l | tr -d ' ') files to convert"

# Process each file
for file in $FILES_TO_CONVERT; do
  convert_file "$file"
done

echo "Conversion complete. Backup files are in $BACKUP_DIR"
echo "Warning: You may need to manually review HTTP client code as API has changed between 4.x and 5.x versions."