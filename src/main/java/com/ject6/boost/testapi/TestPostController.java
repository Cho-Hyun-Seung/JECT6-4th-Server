package com.ject6.boost.testapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test-posts")
public class TestPostController {

    private final TestPostService testPostService;

    public TestPostController(TestPostService testPostService) {
        this.testPostService = testPostService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TestPostResponse create(@RequestBody TestPostCreateRequest request) {
        return testPostService.create(request);
    }

    @GetMapping
    public List<TestPostResponse> findAll() {
        return testPostService.findAll();
    }

    @GetMapping("/{id}")
    public TestPostResponse findById(@PathVariable Long id) {
        return testPostService.findById(id);
    }
}
