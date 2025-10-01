package com.pharma.taskmanager.workers;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = TaskReminderWorker.class
)
public interface TaskReminderWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.pharma.taskmanager.workers.TaskReminderWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(
      TaskReminderWorker_AssistedFactory factory);
}
