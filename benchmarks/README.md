# `:benchmarks`

## Module dependency graph

<!--region graph-->
```mermaid
---
config:
  layout: elk
  elk:
    nodePlacementStrategy: SIMPLE
---
graph TB
  subgraph :feature
    direction TB
    subgraph :feature:settings
      direction TB
      :feature:settings:impl[impl]:::android-library
    end
    subgraph :feature:store
      direction TB
      :feature:store:api[api]:::android-library
      :feature:store:impl[impl]:::android-library
    end
    subgraph :feature:cart
      direction TB
      :feature:cart:api[api]:::android-library
      :feature:cart:impl[impl]:::android-library
    end
    subgraph :feature:search
      direction TB
      :feature:search:api[api]:::android-library
      :feature:search:impl[impl]:::android-library
    end
    subgraph :feature:chat
      direction TB
      :feature:chat:api[api]:::android-library
      :feature:chat:impl[impl]:::android-library
    end
    subgraph :feature:trending
      direction TB
      :feature:trending:api[api]:::android-library
      :feature:trending:impl[impl]:::android-library
    end
    subgraph :feature:product
      direction TB
      :feature:product:api[api]:::android-library
      :feature:product:impl[impl]:::android-library
    end
    subgraph :feature:category
      direction TB
      :feature:category:api[api]:::android-library
      :feature:category:impl[impl]:::android-library
    end
  end
  subgraph :sync
    direction TB
    :sync:work[work]:::android-library
  end
  subgraph :core
    direction TB
    :core:common[common]:::jvm-library
    :core:data[data]:::android-library
    :core:database[database]:::android-library
    :core:datastore[datastore]:::android-library
    :core:datastore-proto[datastore-proto]:::android-library
    :core:designsystem[designsystem]:::android-library
    :core:domain[domain]:::android-library
    :core:model[model]:::jvm-library
    :core:network[network]:::android-library
    :core:notifications[notifications]:::android-library
    :core:ui[ui]:::android-library
  end
  :benchmarks[benchmarks]:::android-test
  :app[app]:::android-application

  :app -.->|baselineProfile| :benchmarks
  :app -.-> :core:common
  :app -.-> :core:data
  :app -.-> :core:ui
  :app -.-> :feature:cart:api
  :app -.-> :feature:cart:impl
  :app -.-> :feature:store:api
  :app -.-> :feature:store:impl
  :app -.-> :feature:chat:api
  :app -.-> :feature:chat:impl
  :app -.-> :feature:trending:api
  :app -.-> :feature:trending:impl
  :app -.-> :feature:search:api
  :app -.-> :feature:search:impl
  :app -.-> :feature:settings:impl
  :app -.-> :feature:category:api
  :app -.-> :feature:category:impl
  :app -.-> :feature:product:api
  :app -.-> :feature:product:impl
  :app -.-> :sync:work
  :benchmarks -.->|testedApks| :app
  :core:data --> :core:common
  :core:data --> :core:database
  :core:data --> :core:datastore
  :core:data --> :core:network
  :core:data -.-> :core:notifications
  :core:database --> :core:model
  :core:datastore -.-> :core:common
  :core:datastore --> :core:datastore-proto
  :core:datastore --> :core:model
  :core:domain --> :core:data
  :core:domain --> :core:model
  :core:network --> :core:common
  :core:network --> :core:model
  :core:notifications -.-> :core:common
  :core:notifications --> :core:model
  :core:ui --> :core:designsystem
  :core:ui --> :core:model
  :feature:cart -.-> :core:data
  :feature:cart -.-> :core:designsystem
  :feature:cart -.-> :core:ui
  :feature:category -.-> :core:data
  :feature:category -.-> :core:designsystem
  :feature:category --> :core:ui
  :feature:category -.-> :core:ui
  :feature:chat -.-> :core:data
  :feature:chat -.-> :core:designsystem
  :feature:chat -.-> :core:ui
  :feature:product -.-> :core:data
  :feature:product -.-> :core:designsystem
  :feature:product -.-> :core:ui
  :feature:search -.-> :core:data
  :feature:search -.-> :core:designsystem
  :feature:search -.-> :core:domain
  :feature:search -.-> :core:ui
  :feature:settings -.-> :core:data
  :feature:settings -.-> :core:designsystem
  :feature:settings -.-> :core:ui
  :feature:store -.-> :core:data
  :feature:store -.-> :core:designsystem
  :feature:store -.-> :core:domain
  :feature:store -.-> :core:notifications
  :feature:store -.-> :core:ui
  :feature:trending -.-> :core:data
  :feature:trending -.-> :core:designsystem
  :feature:trending -.-> :core:domain
  :feature:trending -.-> :core:ui
  
  :feature:cart:api --> :core:navigation
  :feature:cart:impl -.-> :core:data
  :feature:cart:impl -.-> :core:designsystem
  :feature:cart:impl -.-> :core:ui
  :feature:cart:impl -.-> :feature:bookmarks:api
  :feature:cart:impl -.-> :feature:category:api
  :feature:store:api --> :core:navigation
  :feature:store:impl -.-> :core:designsystem
  :feature:store:impl -.-> :core:domain
  :feature:store:impl -.-> :core:notifications
  :feature:store:impl -.-> :core:ui
  :feature:store:impl -.-> :feature:store:api
  :feature:store:impl -.-> :feature:category:api
  :feature:chat:impl -.-> :core:designsystem
  :feature:chat:impl -.-> :core:domain
  :feature:chat:impl -.-> :core:notifications
  :feature:chat:impl -.-> :core:ui
  :feature:chat:impl -.-> :feature:chat:api
  :feature:chat:impl -.-> :feature:category:api
  :feature:trending:api --> :core:navigation
  :feature:trending:impl -.-> :core:designsystem
  :feature:trending:impl -.-> :core:domain
  :feature:trending:impl -.-> :core:ui
  :feature:trending:impl -.-> :feature:trending:api
  :feature:trending:impl -.-> :feature:category:api
  :feature:search:api -.-> :core:domain
  :feature:search:api --> :core:navigation
  :feature:search:impl -.-> :core:designsystem
  :feature:search:impl -.-> :core:domain
  :feature:search:impl -.-> :core:ui
  :feature:search:impl -.-> :feature:interests:api
  :feature:search:impl -.-> :feature:search:api
  :feature:search:impl -.-> :feature:category:api
  :feature:settings:impl -.-> :core:data
  :feature:settings:impl -.-> :core:designsystem
  :feature:settings:impl -.-> :core:ui
  :feature:category:api -.-> :core:designsystem
  :feature:category:api --> :core:navigation
  :feature:category:api -.-> :core:ui
  :feature:category:impl -.-> :core:data
  :feature:category:impl -.-> :core:designsystem
  :feature:category:impl -.-> :core:ui
  :feature:category:impl -.-> :feature:category:api
  :feature:product:impl -.-> :core:designsystem
  :feature:product:impl -.-> :core:domain
  :feature:product:impl -.-> :core:notifications
  :feature:product:impl -.-> :core:ui
  :feature:product:impl -.-> :feature:product:api
  :sync:work -.-> :core:data
  :sync:work -.-> :core:notifications

classDef android-application fill:#CAFFBF,stroke:#000,stroke-width:2px,color:#000;
classDef android-feature fill:#FFD6A5,stroke:#000,stroke-width:2px,color:#000;
classDef android-library fill:#9BF6FF,stroke:#000,stroke-width:2px,color:#000;
classDef android-test fill:#A0C4FF,stroke:#000,stroke-width:2px,color:#000;
classDef jvm-library fill:#BDB2FF,stroke:#000,stroke-width:2px,color:#000;
classDef unknown fill:#FFADAD,stroke:#000,stroke-width:2px,color:#000;
```

<details><summary>📋 Graph legend</summary>

```mermaid
graph TB
  application[application]:::android-application
  feature[feature]:::android-feature
  library[library]:::android-library
  jvm[jvm]:::jvm-library

  application -.-> feature
  library --> jvm

classDef android-application fill:#CAFFBF,stroke:#000,stroke-width:2px,color:#000;
classDef android-feature fill:#FFD6A5,stroke:#000,stroke-width:2px,color:#000;
classDef android-library fill:#9BF6FF,stroke:#000,stroke-width:2px,color:#000;
classDef android-test fill:#A0C4FF,stroke:#000,stroke-width:2px,color:#000;
classDef jvm-library fill:#BDB2FF,stroke:#000,stroke-width:2px,color:#000;
classDef unknown fill:#FFADAD,stroke:#000,stroke-width:2px,color:#000;
```

</details>
<!--endregion-->
