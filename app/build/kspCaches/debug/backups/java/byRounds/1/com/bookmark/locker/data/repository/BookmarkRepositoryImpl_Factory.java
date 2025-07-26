package com.bookmark.locker.data.repository;

import com.bookmark.locker.data.dao.BookmarkDao;
import com.bookmark.locker.data.dao.FolderDao;
import com.bookmark.locker.data.dao.TagDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class BookmarkRepositoryImpl_Factory implements Factory<BookmarkRepositoryImpl> {
  private final Provider<BookmarkDao> bookmarkDaoProvider;

  private final Provider<FolderDao> folderDaoProvider;

  private final Provider<TagDao> tagDaoProvider;

  public BookmarkRepositoryImpl_Factory(Provider<BookmarkDao> bookmarkDaoProvider,
      Provider<FolderDao> folderDaoProvider, Provider<TagDao> tagDaoProvider) {
    this.bookmarkDaoProvider = bookmarkDaoProvider;
    this.folderDaoProvider = folderDaoProvider;
    this.tagDaoProvider = tagDaoProvider;
  }

  @Override
  public BookmarkRepositoryImpl get() {
    return newInstance(bookmarkDaoProvider.get(), folderDaoProvider.get(), tagDaoProvider.get());
  }

  public static BookmarkRepositoryImpl_Factory create(Provider<BookmarkDao> bookmarkDaoProvider,
      Provider<FolderDao> folderDaoProvider, Provider<TagDao> tagDaoProvider) {
    return new BookmarkRepositoryImpl_Factory(bookmarkDaoProvider, folderDaoProvider, tagDaoProvider);
  }

  public static BookmarkRepositoryImpl newInstance(BookmarkDao bookmarkDao, FolderDao folderDao,
      TagDao tagDao) {
    return new BookmarkRepositoryImpl(bookmarkDao, folderDao, tagDao);
  }
}
