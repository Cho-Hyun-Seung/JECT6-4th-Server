package com.ject6.boost.testapi;

public class TestPostNotFoundException extends RuntimeException {

    public TestPostNotFoundException(Long id) {
        super("Test post not found. id=" + id);
    }
}
