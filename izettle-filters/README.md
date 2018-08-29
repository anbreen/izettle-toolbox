# izettle-filters

## AdminDirectAccess

Configures Dropwizard to serve admin resources from `/system` and
prevents non-direct access (that is, requests going via a load balancer)
to said resources.


### Usage

```java
bootstrap.addBundle(new AdminDirectAccessBundle());
```
