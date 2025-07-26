package com.bookmark.locker.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class BookmarkCleanupWorker_AssistedFactory_Impl implements BookmarkCleanupWorker_AssistedFactory {
  private final BookmarkCleanupWorker_Factory delegateFactory;

  BookmarkCleanupWorker_AssistedFactory_Impl(BookmarkCleanupWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public BookmarkCleanupWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<BookmarkCleanupWorker_AssistedFactory> create(
      BookmarkCleanupWorker_Factory delegateFactory) {
    return InstanceFactory.create(new BookmarkCleanupWorker_AssistedFactory_Impl(delegateFactory));
  }
}
