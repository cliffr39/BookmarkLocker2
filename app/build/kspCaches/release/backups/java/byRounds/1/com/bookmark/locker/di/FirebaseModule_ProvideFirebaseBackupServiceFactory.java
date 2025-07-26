package com.bookmark.locker.di;

import com.bookmark.locker.firebase.FirebaseBackupService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class FirebaseModule_ProvideFirebaseBackupServiceFactory implements Factory<FirebaseBackupService> {
  @Override
  public FirebaseBackupService get() {
    return provideFirebaseBackupService();
  }

  public static FirebaseModule_ProvideFirebaseBackupServiceFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirebaseBackupService provideFirebaseBackupService() {
    return Preconditions.checkNotNullFromProvides(FirebaseModule.INSTANCE.provideFirebaseBackupService());
  }

  private static final class InstanceHolder {
    private static final FirebaseModule_ProvideFirebaseBackupServiceFactory INSTANCE = new FirebaseModule_ProvideFirebaseBackupServiceFactory();
  }
}
