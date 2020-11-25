<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/othneildrew/Best-README-Template">
    <img src="images/logo.png" alt="Logo">
  </a>

  <h3 align="center">Marketcetera Automated Trading Platform</h3>

  <p align="center">
    Open-source FIX-based algorithmic trading platform
    <br />
    <a href="https://confluence.marketcetera.com/x/AoAN"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/colinduplantis/marketcetera/issues">Report Bug</a>
    ·
    <a href="https://github.com/colinduplantis/marketcetera/issues">Request Feature</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
## Table of Contents

* [About the Project](#about-the-project)
  * [Built With](#built-with)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Usage](#usage)
* [Roadmap](#roadmap)
* [Contributing](#contributing)
* [License](#license)
* [Contact](#contact)
* [Acknowledgements](#acknowledgements)



<!-- ABOUT THE PROJECT -->
## About The Project

By taking an open source approach, Marketcetera gives you total control over your trading platform at a fraction of the cost of traditional proprietary commercial software offerings or in-house solutions. You’ll have a robust, extensible software foundation on which to execute your unique strategies, whether you use the platform as is or you choose to customize it to meet your needs. Marketcetera gives you complete transparency into the source code, meaning you have total control over what you do with the product. Use, modify or enhance the source code to meet your business objectives, without paying license fees. You’ll get to market more quickly because there’s no waiting for vendors, and you won’t have to begin your development efforts from scratch. Plus, you’ll maintain complete control – and confidentiality – of your proprietary trading strategies.

## Built With
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Hibernate](https://hibernate.org/)
* [Java 8](https://openjdk.java.net/)
* [Vaadin](https://vaadin.com/)
* [Esper](https://www.espertech.com/)
* [Tensor Flow](https://www.tensorflow.org/)

<!-- GETTING STARTED -->
## Getting Started

### Pre-Built Packages
* [Download](https://confluence.marketcetera.com/x/YoAV) the installer for your platform. There are two installers to download and run: one for the server-side components and one for the user interface, named Photon. The last Photon release is 3.1.0 and is compatible with the current 3.x server-side packages. Download one server-side package and one Photon package.
* Run the installers. There are a few choices, but it’s OK to just accept the defaults the first time through.
* Start DARE, the Deploy Anywhere Routing Engine. This is the main server-side package and is required.
* If desired, start the Strategy Engine. On Windows, there is a Start Menu option, you can also run from the command line on any platform. The Strategy Engine will connect to the local instance of DARE. The Strategy Engine, along with providing the ability to execute strategies, also serves as the Market Data Nexus in this configuration, that is, it supplies market data. As configured, the market data is from our cloud test exchange. Later on, you can switch to real market data, though there are typically costs associated with that.
* Start Photon, the user interface component. You’ll be prompted to log in. The local instance of DARE, to which you’ll be logging in, has a user for testing purposes: trader/trader (that’s username: ‘trader’ and password: ‘trader’).

### Build from Source

#### Prerequisites

* Maven 3
* Java 8

#### Build

* Clone Github repo
```sh
$ mkdir -p marketcetera/code
$ cd workspaces/marketcetera/code
$ git clone https://github.com/colinduplantis/marketcetera.git
```
* Build
```sh
$ mvn -DskipTests clean install
```
* Choose an IDE. For historical reasons, relating to our user interface, Photon, being developed on the Eclipse Rich Client Platform (RCP), we have usually used Eclipse. You don't have to, you can use whatever IDE you want and it can use whatever theme you want. These instructions are for preparing Eclipse to load your workspace. Substitute your own instructions for other IDEs.
```sh
$ mvn eclipse:eclipse
```
* Run tests to make sure your environment is set up correctly
```sh
$ mvn clean install
```

<!-- USAGE EXAMPLES -->
## Usage

Use this space to show useful examples of how a project can be used. Additional screenshots, code examples and demos work well in this space. You may also link to more resources.

_For more examples, please refer to the [Documentation](https://example.com)_



<!-- ROADMAP -->
## Roadmap

See the [open issues](https://github.com/othneildrew/Best-README-Template/issues) for a list of proposed features (and known issues).



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE` for more information.



<!-- CONTACT -->
## Contact

Your Name - [@your_twitter](https://twitter.com/your_username) - email@example.com

Project Link: [https://github.com/your_username/repo_name](https://github.com/your_username/repo_name)



<!-- ACKNOWLEDGEMENTS -->
## Acknowledgements
* [GitHub Emoji Cheat Sheet](https://www.webpagefx.com/tools/emoji-cheat-sheet)
* [Img Shields](https://shields.io)
* [Choose an Open Source License](https://choosealicense.com)
* [GitHub Pages](https://pages.github.com)
* [Animate.css](https://daneden.github.io/animate.css)
* [Loaders.css](https://connoratherton.com/loaders)
* [Slick Carousel](https://kenwheeler.github.io/slick)
* [Smooth Scroll](https://github.com/cferdinandi/smooth-scroll)
* [Sticky Kit](http://leafo.net/sticky-kit)
* [JVectorMap](http://jvectormap.com)
* [Font Awesome](https://fontawesome.com)





<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/colinduplantis/marketcetera.svg?style=flat-square
[contributors-url]: https://github.com/colinduplantis/marketcetera/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/colinduplantis/marketcetera.svg?style=flat-square
[forks-url]: https://github.com/colinduplantis/marketcetera/network/members
[stars-shield]: https://img.shields.io/github/stars/colinduplantis/marketcetera.svg?style=flat-square
[stars-url]: https://github.com/colinduplantis/marketcetera/stargazers
[issues-shield]: https://img.shields.io/github/issues/colinduplantis/marketcetera.svg?style=flat-square
[issues-url]: https://github.com/colinduplantis/marketcetera/issues
[license-shield]: https://img.shields.io/github/license/colinduplantis/marketcetera.svg?style=flat-square
[license-url]: https://github.com/colinduplantis/marketcetera/LICENSE
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=flat-square&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/colin-duplantis-a767142
[product-screenshot]: images/screenshot.png
