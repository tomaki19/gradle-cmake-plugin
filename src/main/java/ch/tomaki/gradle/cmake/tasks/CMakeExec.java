package ch.tomaki.gradle.cmake.tasks;

import java.util.ArrayList;
import java.util.List;

import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.AbstractExecTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.internal.os.OperatingSystem;

public abstract class CMakeExec extends AbstractExecTask<CMakeExec> {

  public CMakeExec() {
    super(CMakeExec.class);
  }

  private final List<String> baseCommandLine = new ArrayList<>();

  @Input
  public abstract SetProperty<String> getAdditionalArguments();

  @Internal
  protected List<String> getBaseCommandLine() {
    return baseCommandLine;
  }

  @Override
  protected void exec() {
    final List<String> commandLine = new ArrayList<>(getBaseCommandLine());
    commandLine.addAll(getAdditionalArguments().get());
    if (OperatingSystem.current().isUnix()) {
      setCommandLine("sh", "-c", String.join(" ", commandLine));
    } else {
      setCommandLine("cmd", "/c", String.join(" ", commandLine));
    }
    super.exec();
  }

}
