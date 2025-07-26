package com.bookmark.locker;

import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedEntryPoint;

@OriginatingElement(
    topLevelClass = BookmarkApplication.class
)
@GeneratedEntryPoint
@InstallIn(SingletonComponent.class)
public interface BookmarkApplication_GeneratedInjector {
  void injectBookmarkApplication(BookmarkApplication bookmarkApplication);
}
