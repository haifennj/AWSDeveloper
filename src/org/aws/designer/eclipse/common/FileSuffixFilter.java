package org.aws.designer.eclipse.common;

import java.io.File;
import java.io.FilenameFilter;

class FileSuffixFilter implements FilenameFilter {
	private final String suffix;

	public FileSuffixFilter(String suffix) {
		this.suffix = suffix.toLowerCase();
	}

	public boolean accept(File dir, String name) {
		if (name.toLowerCase().endsWith(this.suffix)) {
			return true;
		}
		return false;
	}
}
