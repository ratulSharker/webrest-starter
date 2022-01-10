# WebRest Starter #

This repository works as a scaffold for spring boot web mvc and rest service. This repository contains very common and basic implementation of authentication, authorization, user profile, user listing etc. It is mainly targeted for a product having admin panel for configuring data and serve rest services based on those configured data.

### Structure ###

There are three main packages 

<details>
  <summary>Common</summary>
  <br/>
  This package contains common implementation for both REST & Web. It contains the configuration of beans, DTO declarations, Enum declaration, Entity declarations, Repository declaration, Service declarations and some Utils declarations.
</details>

<details>
  <summary>REST</summary>
  <br/>
  This package contains the implementation REST service specific. From the birds eye view, REST service's routes and controller declarations resides in this package. It shares the service portion from the Common package.
</details>

<details>
  <summary>Web</summary>
  <br/>
  This package contains Web related implementation. It declares a route file and their controller portion. Templates are served from templates inside of the resource folder. It shares the service implementations from Common package. For templating, <a href="https://www.thymeleaf.org/">Thymeleaf</a> is being used. In the front end <a href="https://jquery.com/">jQuery</a> and <a href="https://getbootstrap.com/">Bootstrap</a> is being used.
</details>


### What is this repository for? ###

* Quick summary
* Version
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

### How do I get set up? ###

* Summary of set up
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact
