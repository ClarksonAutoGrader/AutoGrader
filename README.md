### autograder-gwt

Autograder is developed for Clarkson University.

---

### Setting up the Development Environment

We recommend using [_Eclipse IDE for Java Developers_](http://www.eclipse.org/downloads/eclipse-packages/).

1. The following is a list of required plugins for Eclipse. Plugins may be downloaded in Eclipse by going to _Help > Eclipse Marketplace_.
   * Apache IvyDE&trade;
   * GWT (see below)

2. Then follow instructions to download and install the [GWT Eclipse Plugin](http://gwt-plugins.github.io/documentation/gwt-eclipse-plugin/Download.html).

3. Configuring Eclipse:
   * In Eclipse, navigate to _File_ > Import > Git > Projects with Git > Clone URI
   * Paste the following URI: ```git@gitlab.com:Experimental1212/autograder-gwt.git```

### Contributing

In this repository, the _master_ branch is protected. This prevents developers from pushing changes directly to the _master_ branch. Instead, development is done in separate feature or bugfix branches, then and pull requests are made to integrate into the _master_ branch. As soon as a pull request is opened, the branch is built and published as a beta build, https://airlab.clarkson.edu/apps/beta/autograder/. This gives developers time to review the changes in a live environment. Once the pull request is accepted and merged, the _master_ branch is built and published to https://airlab.clarkson.edu/apps/autograder/.
