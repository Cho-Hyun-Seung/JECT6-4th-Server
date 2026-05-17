package com.ject6.boost.testapi;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TestPostService {

    private final TestPostRepository testPostRepository;

    public TestPostService(TestPostRepository testPostRepository) {
        this.testPostRepository = testPostRepository;
    }

    @Transactional
    public TestPostResponse create(TestPostCreateRequest request) {
        TestPost testPost = new TestPost(request.title(), request.content());
        return TestPostResponse.from(testPostRepository.save(testPost));
    }

    public List<TestPostResponse> findAll() {
        return testPostRepository.findAll()
                .stream()
                .map(TestPostResponse::from)
                .toList();
    }

    public TestPostResponse findById(Long id) {
        return testPostRepository.findById(id)
                .map(TestPostResponse::from)
                .orElseThrow(() -> new TestPostNotFoundException(id));
    }
}
