# Gluon Connect

## Introduction

Gluon Connect is a client-side library that simplifies binding your data from any source and format to your
JavaFX UI controls. It works by retrieving data from a data source and converting that data from a specific format
into JavaFX observable lists and observable objects that can be used directly in JavaFX UI controls. It is designed to
allow developers to easily add support for custom data sources and data formats.

  * Reference Documentation: http://docs.gluonhq.com/connect
  * JavaDoc: http://docs.gluonhq.com/connect/javadoc

## Dependency

### Together with Gluon Mobile

A dependency to Gluon Connect is automatically added when using the [Gluon Mobile](http://gluonhq.com/products/mobile/) dependency.
You also need to configure the four required Charm Down plugins that are used in Gluon Mobile.

    dependencies {
        compile 'com.gluonhq:charm:4.3.2'
    }

    jfxmobile {
        downConfig {
            version '3.2.4'
            plugins 'display', 'lifecycle', 'statusbar', 'storage'
        }
    }

### Standalone

You can also use the library as a standalone dependency. You only need to define the following dependency:

##### Gradle

    dependencies {
        compile 'com.gluonhq:connect:1.4.3'
    }

##### Maven

    <dependency>
        <groupId>com.gluonhq</groupId>
        <artifactId>connect</artifactId>
        <version>1.4.3</version>
    </dependency>
