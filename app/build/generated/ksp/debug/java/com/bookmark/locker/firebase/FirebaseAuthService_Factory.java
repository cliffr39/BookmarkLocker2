package com.bookmark.locker.firebase;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class FirebaseAuthService_Factory implements Factory<FirebaseAuthService> {
  private final Provider<Context> contextProvider;

  public FirebaseAuthService_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public FirebaseAuthService get() {
    return newInstance(contextProvider.get());
  }

  public static FirebaseAuthService_Factory create(Provider<Context> contextProvider) {
    return new FirebaseAuthService_Factory(contextProvider);
  }

  public static FirebaseAuthService newInstance(Context context) {
    return new FirebaseAuthService(context);
  }
}
