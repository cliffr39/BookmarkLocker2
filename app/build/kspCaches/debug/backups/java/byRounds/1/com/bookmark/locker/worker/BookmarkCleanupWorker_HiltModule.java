package com.bookmark.locker.worker;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = BookmarkCleanupWorker.class
)
public interface BookmarkCleanupWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.bookmark.locker.worker.BookmarkCleanupWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(
      BookmarkCleanupWorker_AssistedFactory factory);
}
