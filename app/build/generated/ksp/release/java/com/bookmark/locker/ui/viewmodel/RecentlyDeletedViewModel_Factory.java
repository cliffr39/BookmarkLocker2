package com.bookmark.locker.ui.viewmodel;

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
public final class RecentlyDeletedViewModel_Factory implements Factory<RecentlyDeletedViewModel> {
  private final Provider<BookmarkRepository> bookmarkRepositoryProvider;

  public RecentlyDeletedViewModel_Factory(Provider<BookmarkRepository> bookmarkRepositoryProvider) {
    this.bookmarkRepositoryProvider = bookmarkRepositoryProvider;
  }

  @Override
  public RecentlyDeletedViewModel get() {
    return newInstance(bookmarkRepositoryProvider.get());
  }

  public static RecentlyDeletedViewModel_Factory create(
      Provider<BookmarkRepository> bookmarkRepositoryProvider) {
    return new RecentlyDeletedViewModel_Factory(bookmarkRepositoryProvider);
  }

  public static RecentlyDeletedViewModel newInstance(BookmarkRepository bookmarkRepository) {
    return new RecentlyDeletedViewModel(bookmarkRepository);
  }
}
