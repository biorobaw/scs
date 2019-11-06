package com.github.biorobaw.scs.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class IOUtils {

	public static void copyFile(String src, String dst) {
		try {
			FileUtils.copyFile(new File(src), new File(dst));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	public static void exec(String cmd, String dir) {
		try {

			Process plot = Runtime.getRuntime().exec(cmd, null, new File(dir));

			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						plot.getInputStream()));
				String line = null;
				while ((line = in.readLine()) != null) {
					System.out.println(line);
				}

				BufferedReader err = new BufferedReader(new InputStreamReader(
						plot.getErrorStream()));
				line = null;
				while ((line = err.readLine()) != null) {
					System.out.println(line);
				}

				plot.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void copyResource(URL resource, String dstFile) {
		try {
			FileUtils.copyURLToFile(resource, new File(dstFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void delete(String filename) {
		FileUtils.deleteQuietly(new File(filename));
	}

}
