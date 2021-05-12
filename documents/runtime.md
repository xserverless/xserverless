# Runtime

```mermaid
flowchart LR

i(invoker) ---> cl(classloader) & cl2(classloader)

cl --> c1(classes) & c2(classes)
cl2 --> c3(classes) & c4(classes)
```

