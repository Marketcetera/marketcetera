# Marketcetera


Marketcetera is an open-source algorithmic trading platform.
It is designed to support low-latency, high-volume trading across computing clusters.


First released in 2006, Marketcetera has been in continuous use for some of the world's largest trading operations for over 15 years.
It has modules to run trading strategies, define parameters of risk, and provision and monitor distributed trading teams.


## Get started

Review the [documentation](`<DOCS_URL>`/) for the complete guides.

To get started:

1. [Install Marketcetera](`<DOCS_URL>`/docs/category/install). You can install with the pre-built binary, the official Docker container, or by building from source.
1. Connect to Market data. The [Marketcetera API](https://repo.marketcetera.org/javadoc/4.0.2/platform/apidocs/) has a functional area to connect to a market data source.
1. Write a [strategy](/docs/category/strategies) that listens to FIX messages and makes orders based on the conditions.
1. Monitor your strategy and refine it.

## Concepts

- **Strategies.** Marketcetera can run any strategy that compiles to a Java Archive file.
Thus, Java is an obvious choice of language to write the strategies in, but it is not the only.
You also should be able to use one of the many to-Java transpilers to write strategies in the language of your choice.

- **Monitoring**. As your strategies run, you can monitor the orders and FIX activity. Marketcetera has a built-in UI, [Photon](`<DOCS_URL>`/docs/install/photon), that reports messages and trades. You are also free to extend Marketcetera with the reporting tool of your design.

- **Architecture**. Marketcetera is designed for low-latency trades and distributed systems. Most of the directories in this repository correspond to a designated function. To learn more about this, read the [Architecture docs](`<DOCS_URL>`/docs/category/architecture).

## Contributors

- Colin Duplantis, architect
- [Matt Dodson](https://wellshapedwords.com), technical writing
- [Yesica Torrico](https://www.linkedin.com/in/yesica-t-uxui/), UX/UI design
