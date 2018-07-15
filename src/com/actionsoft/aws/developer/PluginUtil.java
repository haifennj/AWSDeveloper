package com.actionsoft.aws.developer;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

public class PluginUtil {

	private static String OS = System.getProperty("osgi.os").toLowerCase();

	public static boolean isLinux() {
		return OS.contains("linux");
	}

	public static boolean isMacOSX() {
		return OS.contains("macosx") && OS.contains("os") && OS.contains("x");
	}

	public static boolean isWindows() {
		return OS.contains("windows");
	}

	public static IProject findAWSReleaseProject() {
		IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = wsroot.getProjects();
		IProject releaseProject = null;
		for (IProject iProject : projects) {
			if (iProject.getName().equals("release")) {
				releaseProject = iProject;
				break;
			}
		}
		if (releaseProject == null) {
			return null;
		}
		// 检查release的有效性
		//IPath refProjectPrefix = releaseProject.getLocation().removeLastSegments(1).removeTrailingSeparator();
		IPath iPath = releaseProject.getLocation();
		//IPath tmpPath = new Path(refProjectPrefix.toPortableString() + iPath.toPortableString());
		String releasePath = iPath.toPortableString();
		File file_release6_1 = new File(releasePath + "/bin/conf/server.xml");
		File file_release6_2 = new File(releasePath + "/bin/lib/aws-license.jar");
		File file_release5_1 = new File(releasePath + "/bin/system.xml");
		File file_release5_2 = new File(releasePath + "/bin/lib/aws.platform.jar");
		if (file_release6_1.exists() && file_release6_2.exists()) {// AWS6版本
			return releaseProject;
		} else if (file_release5_1.exists() && file_release5_2.exists()) {// AWS5版本
			return releaseProject;
		} else {
			//MessageDialog.openInformation(null, "AWSDeveloper", "当前Project中的[release]的不是一个有效的AWS资源");
			return null;
		}
	}

	public static String getAWSReleaseProjectPath(IProject releaseProject) {
		return "";
	}

}
