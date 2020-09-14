#!/usr/bin/env bash

# Configure GIT
git config --global user.email "githubbot@gluonhq.com"
git config --global user.name "Gluon Bot"

# Generate and upload jars
./gradlew publish -PrepositoryUrl=$mavenPublishURL/releases/ -PrepositoryUsername=$mavenPublishUsername -PrepositoryPassword=$mavenPublishPassword

# Upload to S3
touch ~/.s3cfg
s3cmd --no-mime-magic --guess-mime-type --access_key "$AWS_ACCESS_KEY" --secret_key "$AWS_SECRET_KEY" put -P --recursive build/docs/javadoc/ s3://docs.gluonhq.com/connect/javadoc/$TRAVIS_TAG/

# Get index.html
s3cmd --access_key "$AWS_ACCESS_KEY" --secret_key "$AWS_SECRET_KEY" get s3://docs.gluonhq.com/connect/javadoc/index.html
# Replace current version with new released version
sed -i "s,url=.*\",url=/connect/javadoc/$TRAVIS_TAG\",g" index.html
# Update index.html to latest release version
s3cmd --no-mime-magic --guess-mime-type --access_key "$AWS_ACCESS_KEY" --secret_key "$AWS_SECRET_KEY" put -P index.html s3://docs.gluonhq.com/connect/javadoc/

# Replace first occurrence of
# version = 'TRAVIS_TAG' 
# with 
# version = 'newVersion-SNAPSHOT'
sed -i "0,/^version = '$TRAVIS_TAG'/s//version = '$newVersion-SNAPSHOT'/" build.gradle

git commit build.gradle -m "Upgrade version to $newVersion-SNAPSHOT"
git push https://gluon-bot:$GITHUB_PASSWORD@github.com/gluonhq/connect HEAD:master