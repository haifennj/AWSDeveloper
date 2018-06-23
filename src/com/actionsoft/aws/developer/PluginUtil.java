package com.actionsoft.aws.developer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

public class PluginUtil {
	public static IProject findAWSReleaseProject() {
		IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = wsroot.getProjects();
		for (IProject iProject : projects) {
			if (iProject.getName().equals("release")) {
				return iProject;
			}
		}
		return null;
	}

	public static String getAWSReleaseProjectPath() {
		return "";
	}

}
