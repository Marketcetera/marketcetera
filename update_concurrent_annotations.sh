#!/bin/bash
# Script to update concurrent annotations from jakarta.annotation.concurrent to net.jcip.annotations

cd /Users/colin/marketcetera/workspaces/sixer-java11/code/marketcetera

# Find all files with jakarta.annotation.concurrent imports and replace them with net.jcip.annotations
find . -name "*.java" -exec grep -l "jakarta.annotation.concurrent" {} \; | while read file; do
  echo "Updating $file"
  # Replace the imports
  sed -i '' 's/import jakarta.annotation.concurrent.Immutable;/import net.jcip.annotations.Immutable;/g' "$file"
  sed -i '' 's/import jakarta.annotation.concurrent.ThreadSafe;/import net.jcip.annotations.ThreadSafe;/g' "$file"
  sed -i '' 's/import jakarta.annotation.concurrent.NotThreadSafe;/import net.jcip.annotations.NotThreadSafe;/g' "$file"
  sed -i '' 's/import jakarta.annotation.concurrent.GuardedBy;/import net.jcip.annotations.GuardedBy;/g' "$file"
done

# Also look for javax.annotation.concurrent
find . -name "*.java" -exec grep -l "javax.annotation.concurrent" {} \; | while read file; do
  echo "Updating $file"
  # Replace the imports
  sed -i '' 's/import javax.annotation.concurrent.Immutable;/import net.jcip.annotations.Immutable;/g' "$file"
  sed -i '' 's/import javax.annotation.concurrent.ThreadSafe;/import net.jcip.annotations.ThreadSafe;/g' "$file"
  sed -i '' 's/import javax.annotation.concurrent.NotThreadSafe;/import net.jcip.annotations.NotThreadSafe;/g' "$file"
  sed -i '' 's/import javax.annotation.concurrent.GuardedBy;/import net.jcip.annotations.GuardedBy;/g' "$file"
done

echo "Concurrent annotations updated to net.jcip.annotations"