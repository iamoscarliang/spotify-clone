package com.oscarliang.spotifyclone.domain.usecase.category;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.oscarliang.spotifyclone.domain.repository.CategoryRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GetAllCategoriesUseCaseTest {

    private CategoryRepository mRepository;
    private GetAllCategoriesUseCase mUseCase;

    @Before
    public void init() {
        mRepository = mock(CategoryRepository.class);
        mUseCase = new GetAllCategoriesUseCase(mRepository);
    }

    @Test
    public void execute() {
        mUseCase.execute();
        verify(mRepository).getAllCategories();
    }

}
