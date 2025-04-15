#!/bin/bash
# Convert javax.annotation lifecycle annotations to jakarta.annotation

# Find all Java files containing the imported annotation classes
find . -type f -name "*.java" | xargs grep -l "import javax.annotation.PostConstruct" | xargs sed -i '' 's/import javax.annotation.PostConstruct;/import jakarta.annotation.PostConstruct;/g'
find . -type f -name "*.java" | xargs grep -l "import javax.annotation.PreDestroy" | xargs sed -i '' 's/import javax.annotation.PreDestroy;/import jakarta.annotation.PreDestroy;/g'

echo "Converted javax.annotation lifecycle annotations to jakarta.annotation"