#!/usr/bin/env bash

# Exit immediately if any command in the script fails
set -e

# Configure GIT
git config --global user.email "githubbot@gluonhq.com"
git config --global user.name "Gluon Bot"

# Decrypt encrypted files
openssl aes-256-cbc -K $encrypted_dc87922a4c8c_key -iv $encrypted_dc87922a4c8c_iv -in .ci/secring.gpg.enc -out secring.gpg -d
if [[ ! -s secring.gpg ]]
   then echo "Decryption failed."
   exit 1
fi

# Generate and upload jars
./gradlew publish closeAndReleaseRepository -i -PsonatypeUsername=$SONATYPE_USERNAME -PsonatypePassword=$SONATYPE_PASSWORD -Psigning.keyId=$GPG_KEYNAME -Psigning.password=$GPG_PASSPHRASE -Psigning.secretKeyRingFile=$TRAVIS_BUILD_DIR/secring.gpg

# Upload to S3
touch ~/.s3cfg
s3cmd --no-mime-magic --guess-mime-type --access_key "$AWS_ACCESS_KEY" --secret_key "$AWS_SECRET_KEY" put -P --recursive build/docs/javadoc/ s3://docs.gluonhq.com/connect/javadoc/$TRAVIS_TAG/

# Get index.html
s3cmd --access_key "$AWS_ACCESS_KEY" --secret_key "$AWS_SECRET_KEY" get s3://docs.gluonhq.com/connect/javadoc/index.html
# Replace current version with new released version
sed -i "s,url=.*\",url=/connect/javadoc/$TRAVIS_TAG\",g" index.html
# Update index.html to latest release version
s3cmd --no-mime-magic --guess-mime-type --access_key "$AWS_ACCESS_KEY" --secret_key "$AWS_SECRET_KEY" put -P index.html s3://docs.gluonhq.com/connect/javadoc/

# Update version by 1
newVersion=${TRAVIS_TAG%.*}.$((${TRAVIS_TAG##*.} + 1))
# Replace first occurrence of
# version = 'TRAVIS_TAG' 
# with 
# version = 'newVersion-SNAPSHOT'
sed -i "0,/^version = '$TRAVIS_TAG'/s//version = '$newVersion-SNAPSHOT'/" build.gradle

git commit build.gradle -m "Upgrade version to $newVersion-SNAPSHOT"
git push https://gluon-bot:$GITHUB_PASSWORD@github.com/gluonhq/connect HEAD:master