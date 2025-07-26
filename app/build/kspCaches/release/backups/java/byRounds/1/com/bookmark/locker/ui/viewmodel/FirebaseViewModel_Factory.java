package com.bookmark.locker.ui.viewmodel;

import com.bookmark.locker.data.repository.BookmarkRepository;
import com.bookmark.locker.firebase.FirebaseAuthService;
import com.bookmark.locker.firebase.FirebaseBackupService;
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
public final class FirebaseViewModel_Factory implements Factory<FirebaseViewModel> {
  private final Provider<FirebaseAuthService> authServiceProvider;

  private final Provider<FirebaseBackupService> backupServiceProvider;

  private final Provider<BookmarkRepository> bookmarkRepositoryProvider;

  public FirebaseViewModel_Factory(Provider<FirebaseAuthService> authServiceProvider,
      Provider<FirebaseBackupService> backupServiceProvider,
      Provider<BookmarkRepository> bookmarkRepositoryProvider) {
    this.authServiceProvider = authServiceProvider;
    this.backupServiceProvider = backupServiceProvider;
    this.bookmarkRepositoryProvider = bookmarkRepositoryProvider;
  }

  @Override
  public FirebaseViewModel get() {
    return newInstance(authServiceProvider.get(), backupServiceProvider.get(), bookmarkRepositoryProvider.get());
  }

  public static FirebaseViewModel_Factory create(Provider<FirebaseAuthService> authServiceProvider,
      Provider<FirebaseBackupService> backupServiceProvider,
      Provider<BookmarkRepository> bookmarkRepositoryProvider) {
    return new FirebaseViewModel_Factory(authServiceProvider, backupServiceProvider, bookmarkRepositoryProvider);
  }

  public static FirebaseViewModel newInstance(FirebaseAuthService authService,
      FirebaseBackupService backupService, BookmarkRepository bookmarkRepository) {
    return new FirebaseViewModel(authService, backupService, bookmarkRepository);
  }
}
