### autograder-gwt

Autograder is an online homework tool used by Clarkson University.

---

### Setting up the Development Environment

We recommend following the [Getting Started with GWT Eclipse Tutorial](http://www.gwtproject.org/usingeclipse.html).

1. Install [_Eclipse IDE for Java Developers Neon 3_](http://www.eclipse.org/downloads/packages/release/Neon/3).

2. Then follow instructions to download and install the [Google Plugin for Eclipse 4.6 (Neon)](https://developers.google.com/eclipse/docs/install-eclipse-4.6).

3. In Eclipse, Help > Eclise Marketplace, install Apache IvyDE.

4. Clone this Git repository in Eclipse.

### Contributing

In this repository, the _master_ branch is a protected which represents the 
live release of the project. Development is done in separate feature or bugfix 
branches, and merge requests are made to integrate development branches into the 
_master_ branch. As soon as a merge request is opened, the branch is built and 
published as a beta build. The link to the live hosting of the beta build is 
added as a comment to the merge request by the build server (example: 
https://airlab.clarkson.edu/apps/beta/\<build_number\>/autograder/). This gives 
developers time to review the changes in a live environment. Once the merge 
request is accepted and merged, the _master_ branch is built and published to 
https://airlab.clarkson.edu/apps/autograder/.

Build server: https://airlab.clarkson.edu:6798/jenkins/
