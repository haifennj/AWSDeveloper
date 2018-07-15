package org.aws.designer.eclipse.common;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.UserLibrary;
import org.eclipse.jdt.internal.core.UserLibraryManager;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import com.actionsoft.aws.developer.PluginUtil;

public class AWSStartup implements org.eclipse.ui.IStartup {
	public void earlyStartup() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getAdapter(ICommandService.class);
				if (commandService != null) {
					commandService.addExecutionListener(new AWSIExecutionListener(AWSStartup.this));
				}
			}
		});
	}

	private List<File> findAllFileInPath(String rootPath, FilenameFilter filenameFilter) throws IOException {
		List result = new ArrayList(64);
		File rootFile = new File(rootPath);
		rootPath = rootFile.getCanonicalPath();

		LinkedList list = new LinkedList();
		File[] childs = rootFile.listFiles();
		int j;
		if (childs != null) {
			File[] arrayOfFile1;
			j = (arrayOfFile1 = childs).length;
			for (int i = 0; i < j; i++) {
				File child = arrayOfFile1[i];
				list.add(child);
			}
		}

		while (!list.isEmpty()) {
			File wrap = (File) list.removeFirst();
			if ((!wrap.isDirectory()) && (filenameFilter.accept(wrap, wrap.getName()))) {
				result.add(wrap);
			}

			childs = wrap.listFiles();
			if (childs != null) {
				File[] arrayOfFile2;
				int k = (arrayOfFile2 = childs).length;
				for (j = 0; j < k; j++) {
					File child = arrayOfFile2[j];
					list.add(child);
				}
			}
		}

		return result;
	}

	class AWSIExecutionListener implements org.eclipse.core.commands.IExecutionListener {
		private static final String USER_LIBRARY_PREFIX = "org.eclipse.jdt.USER_LIBRARY/";
		private static final String AWS_LIBRARY_NAME = "aws_lib";

		AWSIExecutionListener(AWSStartup aws) {
		}

		AWSIExecutionListener() {
		}

		public void notHandled(String commandId, NotHandledException exception) {
		}

		public void postExecuteFailure(String commandId, ExecutionException exception) {
		}

		public void preExecute(String commandId, ExecutionEvent event) {
		}

		public void postExecuteSuccess(String commandId, Object returnValue) {
			if ("org.eclipse.ui.file.refresh".equals(commandId)) {
				IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();

				IProject[] projects = wsroot.getProjects();

				String[] userLibraryNames = JavaCore.getUserLibraryNames();
				boolean awsExtensionLibraryPresent = false;
				if (userLibraryNames != null && userLibraryNames.length > 0) {
					for (String userLibraryName : userLibraryNames) {
						if (AWS_LIBRARY_NAME.equals(userLibraryName)) {
							awsExtensionLibraryPresent = true;
						}
					}
				}

				if (!awsExtensionLibraryPresent) {
					ClasspathContainerInitializer initializer = JavaCore.getClasspathContainerInitializer("org.eclipse.jdt.USER_LIBRARY");
					IPath containerPath = new Path("org.eclipse.jdt.USER_LIBRARY");
					try {
						initializer.requestClasspathContainerUpdate(containerPath.append(AWS_LIBRARY_NAME), (IJavaProject) null, new IClasspathContainer() {
							public IPath getPath() {
								return (new Path("org.eclipse.jdt.USER_LIBRARY")).append(AWS_LIBRARY_NAME);
							}

							public int getKind() {
								return 1;
							}

							public String getDescription() {
								return AWS_LIBRARY_NAME;
							}

							public IClasspathEntry[] getClasspathEntries() {
								return new IClasspathEntry[0];
							}
						});
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}

				for (IProject iProject : projects) {
					updateAWSFiles(iProject);
				}
			}
		}

		private void updateAWSFiles(IProject project) {
			try {
				if (!project.isOpen()) {
					return;
				}
				UserLibraryManager userLibManager = new UserLibraryManager();
				String[] names = userLibManager.getUserLibraryNames();
				for (String libName : names) {
					if (!libName.contains("org.eclipse.jdt.USER_LIBRARY/")) {
						UserLibrary userLibrary = userLibManager.getUserLibrary(libName);
						if (!userLibrary.isSystemLibrary()) {
							File awsRoot = null;
							IPath refProjectPrefix = null;
							if (AWS_LIBRARY_NAME.equals(libName)) {
								IProject releaseProject = PluginUtil.findAWSReleaseProject();
								refProjectPrefix = releaseProject.getLocation().removeLastSegments(1).removeTrailingSeparator();
								IPath path = releaseProject.getFullPath();
								IPath tmpPath = new Path(refProjectPrefix.toPortableString() + path.toPortableString());
								awsRoot = new File(tmpPath.toPortableString()).getAbsoluteFile();
								if (awsRoot != null) {
									updateAWSLib(project, refProjectPrefix, libName, awsRoot);
								}
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void updateAWSLib(IProject project, IPath refProjectPrefix, String libName, File directory) throws IOException {
			File libDir = new File(directory + "/bin/lib");
			List files = AWSStartup.this.findAllFileInPath(libDir.getCanonicalPath(), new FileSuffixFilter(".jar"));
			List list = new ArrayList(files.size());
			for (int i = 0; i < files.size(); i++) {
				Path path = new Path(((File) files.get(i)).getAbsolutePath());
				if (refProjectPrefix != null) {
					String ps = refProjectPrefix.toPortableString();
					path = new Path(path.toPortableString().substring(ps.length()));
				}
				IClasspathEntry var = JavaCore.newLibraryEntry(path, null, null, true);
				list.add(var);
			}
			libDir = new File(directory + "/bin/jdbc");
			files = AWSStartup.this.findAllFileInPath(libDir.getCanonicalPath(), new FileSuffixFilter(".jar"));
			for (int i = 0; i < files.size(); i++) {
				IPath path = new Path(((File) files.get(i)).getAbsolutePath());
				if (refProjectPrefix != null) {
					String ps = refProjectPrefix.toPortableString();
					path = new Path(path.toPortableString().substring(ps.length()));
				}
				IClasspathEntry var = JavaCore.newLibraryEntry(path, null, null, true);
				list.add(var);
			}

			UserLibraryManager userLibraryManager = new UserLibraryManager();
			IClasspathEntry[] classEntries = (IClasspathEntry[]) list.toArray(new IClasspathEntry[list.size()]);
			userLibraryManager.setUserLibrary(libName, classEntries, false);
		}
	}
}
