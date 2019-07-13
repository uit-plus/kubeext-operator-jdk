# java-operator-sdk

> This project is in incubation phase.

SDK for building Kubernetes Operators in Java. Inspired by [operator-sdk](https://github.com/operator-framework/operator-sdk).
In this first iteration we aim to provide a framework which handles the reconciliation loop by dispatching events to
a Controller written by the user of the framework.

The Controller only contains the logic to create, update and delete the actual resources related to the CRD.

## Implementation

This library relies on the amazing [kubernetes-client](https://github.com/fabric8io/kubernetes-client) from fabric8. Most of the heavy lifting is actually done by
kubernetes-client.

## Roadmap

Feature we would like to implement and invite the community to help us implement in the future:

* Testing support
* Class generation from CRD to POJO

