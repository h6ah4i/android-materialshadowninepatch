Material Shadow 9-Patch
===============

This library provides 9-patch based drop shadow for view elements. Works on API level 9 or later.

---

<a href="./pic/gb.png?raw=true"><img src="./pic/gb.png?raw=true" alt="Example on Android 2.3" width="200" /></a>
<a href="./pic/ics.png?raw=true"><img src="./pic/ics.png?raw=true" alt="Example on Android 4.0" width="200" /></a>
<a href="./pic/lollipop.png?raw=true"><img src="./pic/lollipop.png?raw=true" alt="Example on Android 5.0" width="200" /></a>

---

Target platforms
---

- API level 9 or later


Latest version
---

- Version 0.5.0  (February 15, 2015)

Getting started
---

This library is published on jCenter. Just add these lines to `build.gradle`.

```groovy
dependencies {
    compile 'com.h6ah4i.android.materialshadowninepatch:materialshadowninepatch:0.5.0'
}
```

Usage
---

### Layout XML

```xml
<com.h6ah4i.android.materialshadowninepatch.MaterialShadowContainerView
    android:id="@+id/shadow_item_container"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:shadowTranslationZ="2dp"
    app:shadowElevation="4dp">

    <!-- NOTE 1: only 1 child can be accepted -->
    <!-- NOTE 2: margins are required to draw shadow properly -->
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginLeft="8dp"
        android:layout_marginTop="8dp"
        android:layout_marginRight="8dp"
        android:layout_marginBottom="8dp"
        android:background="@android:color/white"
        android:text="Inner content view" />

</com.h6ah4i.android.materialshadowninepatch.MaterialShadowContainerView>
```

### Java code

```java
MaterialShadowContainerView shadowView =
        (MaterialShadowContainerView) findViewById(R.id.shadow_item_container);

float density = getResources().getDisplayMetrics().density;

shadowView.setShadowTranslationZ(density * 2.0f); // 2.0 dp
shadowView.setShadowElevation(density * 4.0f); // 4.0 dp
```

License
---

This library is licensed under the [Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

See [`LICENSE`](LICENSE) for full of the license text.

    Copyright (C) 2015 Haruki Hasegawa

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
