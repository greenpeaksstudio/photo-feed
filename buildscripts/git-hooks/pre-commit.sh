#!/bin/sh


##################
# Spotless
##################

echo "Running Spotless..."
./gradlew spotlessCheck
RESULT=$?

if [ $RESULT -ne 0 ]; then
  echo ""
  echo "Spotless found format violations, so aborting the commit..."

  # using "> /dev/null/" because --quiet doesn't suppress ALL non-error output
  ./gradlew spotlessApply > /dev/null

  echo "Recommended changes from spotless have been applied."
  exit $RESULT
fi

exit $RESULT