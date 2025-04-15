#!/bin/bash
# Script to convert javax.annotation.concurrent annotations to edu.umd.cs.findbugs.annotations.SuppressFBWarnings

# Replace NotThreadSafe annotation
find . -type f -name "*.java" | xargs grep -l "import javax.annotation.concurrent.NotThreadSafe" | xargs sed -i '' 's/import javax.annotation.concurrent.NotThreadSafe;/import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;/g'
find . -type f -name "*.java" | xargs grep -l "@NotThreadSafe" | xargs sed -i '' 's/@NotThreadSafe/@SuppressFBWarnings(value="IS2_INCONSISTENT_SYNC", justification="Not thread safe")/g'

# Replace ThreadSafe annotation
find . -type f -name "*.java" | xargs grep -l "import javax.annotation.concurrent.ThreadSafe" | xargs sed -i '' 's/import javax.annotation.concurrent.ThreadSafe;/import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;/g'
find . -type f -name "*.java" | xargs grep -l "@ThreadSafe" | xargs sed -i '' 's/@ThreadSafe/@SuppressFBWarnings(value="IS2_INCONSISTENT_SYNC", justification="Thread safe")/g'

# Replace GuardedBy annotation
find . -type f -name "*.java" | xargs grep -l "import javax.annotation.concurrent.GuardedBy" | xargs sed -i '' 's/import javax.annotation.concurrent.GuardedBy;/import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;/g'
find . -type f -name "*.java" | xargs grep -l "@GuardedBy" | xargs sed -i '' 's/@GuardedBy(\("[^"]*"\))/@SuppressFBWarnings(value="IS2_INCONSISTENT_SYNC", justification="Guarded by $1")/g'

# Replace Immutable annotation
find . -type f -name "*.java" | xargs grep -l "import javax.annotation.concurrent.Immutable" | xargs sed -i '' 's/import javax.annotation.concurrent.Immutable;/import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;/g'
find . -type f -name "*.java" | xargs grep -l "@Immutable" | xargs sed -i '' 's/@Immutable/@SuppressFBWarnings(value="IS2_INCONSISTENT_SYNC", justification="Immutable")/g'

echo "Converted javax.annotation.concurrent annotations to SpotBugs annotations."