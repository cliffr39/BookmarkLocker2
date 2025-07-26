package com.bookmark.locker.ui.viewmodel;

import com.bookmark.locker.ai.AIDescriptionService;
import com.bookmark.locker.data.repository.BookmarkRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class BookmarkViewModel_Factory implements Factory<BookmarkViewModel> {
  private final Provider<BookmarkRepository> repositoryProvider;

  private final Provider<AIDescriptionService> aiServiceProvider;

  public BookmarkViewModel_Factory(Provider<BookmarkRepository> repositoryProvider,
      Provider<AIDescriptionService> aiServiceProvider) {
    this.repositoryProvider = repositoryProvider;
    this.aiServiceProvider = aiServiceProvider;
  }

  @Override
  public BookmarkViewModel get() {
    return newInstance(repositoryProvider.get(), aiServiceProvider.get());
  }

  public static BookmarkViewModel_Factory create(Provider<BookmarkRepository> repositoryProvider,
      Provider<AIDescriptionService> aiServiceProvider) {
    return new BookmarkViewModel_Factory(repositoryProvider, aiServiceProvider);
  }

  public static BookmarkViewModel newInstance(BookmarkRepository repository,
      AIDescriptionService aiService) {
    return new BookmarkViewModel(repository, aiService);
  }
}
