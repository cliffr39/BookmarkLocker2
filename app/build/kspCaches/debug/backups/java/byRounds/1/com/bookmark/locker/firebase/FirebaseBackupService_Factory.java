package com.bookmark.locker.firebase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class FirebaseBackupService_Factory implements Factory<FirebaseBackupService> {
  @Override
  public FirebaseBackupService get() {
    return newInstance();
  }

  public static FirebaseBackupService_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirebaseBackupService newInstance() {
    return new FirebaseBackupService();
  }

  private static final class InstanceHolder {
    private static final FirebaseBackupService_Factory INSTANCE = new FirebaseBackupService_Factory();
  }
}
