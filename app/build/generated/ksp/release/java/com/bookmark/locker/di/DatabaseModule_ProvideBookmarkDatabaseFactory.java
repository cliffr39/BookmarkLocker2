package com.bookmark.locker.di;

import android.content.Context;
import com.bookmark.locker.data.database.BookmarkDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseModule_ProvideBookmarkDatabaseFactory implements Factory<BookmarkDatabase> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvideBookmarkDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BookmarkDatabase get() {
    return provideBookmarkDatabase(contextProvider.get());
  }

  public static DatabaseModule_ProvideBookmarkDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new DatabaseModule_ProvideBookmarkDatabaseFactory(contextProvider);
  }

  public static BookmarkDatabase provideBookmarkDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBookmarkDatabase(context));
  }
}
