package com.pharma.taskmanager.di;

import android.content.Context;
import com.pharma.taskmanager.data.database.TaskManagerDatabase;
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
public final class DatabaseModule_ProvideTaskManagerDatabaseFactory implements Factory<TaskManagerDatabase> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvideTaskManagerDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public TaskManagerDatabase get() {
    return provideTaskManagerDatabase(contextProvider.get());
  }

  public static DatabaseModule_ProvideTaskManagerDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new DatabaseModule_ProvideTaskManagerDatabaseFactory(contextProvider);
  }

  public static TaskManagerDatabase provideTaskManagerDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideTaskManagerDatabase(context));
  }
}
