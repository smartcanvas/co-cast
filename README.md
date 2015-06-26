Co-Cast
===================

Co-cast is a Digital Signage solution that integrates with Smart Canvas (R) to leverage its content to television 
or other castable devices. It's written in Java and deployable on Google App Engine (part of Google Cloud Platform).

- How to install?

Just clone the repository and use your favorite IDE to work on it. It's a maven project, so all build tasks should
be easy to perform. It requires Maven 3.1 or newer. Build using: mvn appengine:devserver or mvn appengine:update.

It uses Polymer, so you need to install/update the components before using it. Here are the steps:

cd <home>/src/main/webapp
bower install

If you don't have bower, install it previously using 'npm'. More information on bower's home-page.
