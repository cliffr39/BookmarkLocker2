package com.bookmark.locker.reminder;

import com.bookmark.locker.data.database.BookmarkDatabase;
import com.bookmark.locker.notification.NotificationHelper;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class ReminderReceiver_MembersInjector implements MembersInjector<ReminderReceiver> {
  private final Provider<BookmarkDatabase> databaseProvider;

  private final Provider<NotificationHelper> notificationHelperProvider;

  public ReminderReceiver_MembersInjector(Provider<BookmarkDatabase> databaseProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    this.databaseProvider = databaseProvider;
    this.notificationHelperProvider = notificationHelperProvider;
  }

  public static MembersInjector<ReminderReceiver> create(
      Provider<BookmarkDatabase> databaseProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    return new ReminderReceiver_MembersInjector(databaseProvider, notificationHelperProvider);
  }

  @Override
  public void injectMembers(ReminderReceiver instance) {
    injectDatabase(instance, databaseProvider.get());
    injectNotificationHelper(instance, notificationHelperProvider.get());
  }

  @InjectedFieldSignature("com.bookmark.locker.reminder.ReminderReceiver.database")
  public static void injectDatabase(ReminderReceiver instance, BookmarkDatabase database) {
    instance.database = database;
  }

  @InjectedFieldSignature("com.bookmark.locker.reminder.ReminderReceiver.notificationHelper")
  public static void injectNotificationHelper(ReminderReceiver instance,
      NotificationHelper notificationHelper) {
    instance.notificationHelper = notificationHelper;
  }
}
