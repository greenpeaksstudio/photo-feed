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

  ./gradlew --quiet spotlessApply

  echo "Recommended changes from spotless have been applied."
  exit $RESULT
fi

##################
# detekt
##################

echo "Running detekt..."
./gradlew detekt
RESULT=$?

exit $RESULT