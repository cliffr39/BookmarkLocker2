package com.bookmark.locker.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.bookmark.locker.data.repository.BookmarkRepository;
import dagger.internal.DaggerGenerated;
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
public final class BookmarkCleanupWorker_Factory {
  private final Provider<BookmarkRepository> bookmarkRepositoryProvider;

  public BookmarkCleanupWorker_Factory(Provider<BookmarkRepository> bookmarkRepositoryProvider) {
    this.bookmarkRepositoryProvider = bookmarkRepositoryProvider;
  }

  public BookmarkCleanupWorker get(Context context, WorkerParameters workerParams) {
    return newInstance(context, workerParams, bookmarkRepositoryProvider.get());
  }

  public static BookmarkCleanupWorker_Factory create(
      Provider<BookmarkRepository> bookmarkRepositoryProvider) {
    return new BookmarkCleanupWorker_Factory(bookmarkRepositoryProvider);
  }

  public static BookmarkCleanupWorker newInstance(Context context, WorkerParameters workerParams,
      BookmarkRepository bookmarkRepository) {
    return new BookmarkCleanupWorker(context, workerParams, bookmarkRepository);
  }
}
