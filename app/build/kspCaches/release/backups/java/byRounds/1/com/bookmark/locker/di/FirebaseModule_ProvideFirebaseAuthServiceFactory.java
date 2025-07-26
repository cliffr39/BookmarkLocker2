package com.bookmark.locker.di;

import com.bookmark.locker.firebase.FirebaseAuthService;
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
public final class FirebaseModule_ProvideFirebaseAuthServiceFactory implements Factory<FirebaseAuthService> {
  @Override
  public FirebaseAuthService get() {
    return provideFirebaseAuthService();
  }

  public static FirebaseModule_ProvideFirebaseAuthServiceFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirebaseAuthService provideFirebaseAuthService() {
    return Preconditions.checkNotNullFromProvides(FirebaseModule.INSTANCE.provideFirebaseAuthService());
  }

  private static final class InstanceHolder {
    private static final FirebaseModule_ProvideFirebaseAuthServiceFactory INSTANCE = new FirebaseModule_ProvideFirebaseAuthServiceFactory();
  }
}
