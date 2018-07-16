package com.actionsoft.aws.developer.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.actionsoft.aws.developer.PluginUtil;

public class LinkAppAction implements IObjectActionDelegate {

	protected IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	private Shell shell;
	protected ISelection currentSelection;
	protected IProject releaseProject;
	protected IPath selectResourcelocation;

	/**
	 * Constructor for Action1.
	 */
	public LinkAppAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
		releaseProject = PluginUtil.findAWSReleaseProject();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if (this.currentSelection == null || this.currentSelection.isEmpty()) {
			return;
		}
		if (releaseProject == null) {
			MessageDialog.openWarning(shell, "提示", "当前工作空间中没有命名为[release]的工程");
			return;
		}
		File awsRoot = releaseProject.getLocation().toFile();
		if (this.currentSelection instanceof ITreeSelection) {
			File file = this.selectResourcelocation.toFile();
			createLink(awsRoot, file);
			try {
				releaseProject.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
			}
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (releaseProject == null) {
			action.setEnabled(false);
			return;
		}
		this.currentSelection = selection;
		if (this.currentSelection instanceof ITreeSelection) {
			ITreeSelection treeSelection = (ITreeSelection) this.currentSelection;
			TreePath[] paths = treeSelection.getPaths();
			for (int i = 0; i < paths.length; i++) {
				TreePath path = paths[i];
				IResource resource = null;
				Object segment = path.getLastSegment();
				if ((segment instanceof IResource))
					resource = (IResource) segment;
				if (resource == null) {
					continue;
				}
				IProject project = resource.getProject();
				if (!project.getName().equals("apps")) {
					action.setEnabled(false);
					return;
				}
				selectResourcelocation = resource.getLocation();
				String targetFilePath = releaseProject.getLocation() + "/apps/install/" + selectResourcelocation.lastSegment();
				File f = new File(targetFilePath);
				if (f.exists()) {
					action.setText("Already Linked");
					action.setEnabled(false);
					return;
				} else {
					action.setText("Link App");
				}
			}
		} else {
			action.setEnabled(false);
		}
	}

	protected void createLink(File awsRoot, File file) {
		String targetFile = awsRoot.getAbsolutePath() + "/apps/install/" + file.getName();
		String sourceFile = file.getPath();
		String cmd = "";
		if (PluginUtil.isMacOSX()) {
			cmd = "ln -s " + sourceFile + " " + targetFile;
			link(cmd);
		} else if (PluginUtil.isWindows()) {
			sourceFile = sourceFile.replaceAll("/", "\\\\");
			targetFile = targetFile.replaceAll("/", "\\\\");
			cmd = "cmd.exe /c mklink /j " + targetFile + " " + sourceFile;
			link(cmd);
		} else if (PluginUtil.isLinux()) {
			cmd = "ln -s " + sourceFile + " " + targetFile;
			link(cmd);
		}
	}

	protected void link(String cmd) {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
