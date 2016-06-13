/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.eap.additional.testsuite.source.distribution;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 *
 * @author panos
 */
public class ProcessSources {

    public static void AdditionalTestSuiteAnnotationProcessing(String basedir, String sourcePath, String server) {
        File folder = new File(sourcePath);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return;
        }

        try {
            for (File file : listOfFiles) {
                if (file.isDirectory()) {
                    AdditionalTestSuiteAnnotationProcessing(basedir, file.getAbsolutePath(), server);
                } else {
                    ArrayList<FileData> output = checkFileForAnnotation(file.getAbsolutePath(), "@EapAdditionalTestsuite",server);
                    for (FileData dest : output) {
                        System.out.println(basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName);
                        copyWithStreams(file, new File(basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName),false);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static ArrayList<FileData> checkFileForAnnotation(String file, String annotationName,String server) throws ClassNotFoundException {
        String[] destinations = null;
        String annotationLine = null;
        ArrayList<FileData> result = new ArrayList<FileData>();
        String packageName = null;
        File f = new File(file);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("package")) {
                    packageName = line.replaceAll("package ", "").replaceAll(";", "").trim();
                }
                if (line.contains(annotationName)) {
                    annotationLine = line;
                    destinations = annotationLine.split("\"");
                    for (String path : destinations) {
                        if (!path.contains(",") && path.contains("/" + server + "/")) {
                            result.add(new FileData(f.getName(),packageName.replaceAll("\\.", "/"),path));
                        }
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return result;
    }

    private static void copyWithStreams(File aSourceFile, File aTargetFile, boolean aAppend) {
        ensureTargetDirectoryExists(aTargetFile.getParentFile());
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            try {
                byte[] bucket = new byte[32 * 1024];
                inStream = new BufferedInputStream(new FileInputStream(aSourceFile));
                outStream = new BufferedOutputStream(new FileOutputStream(aTargetFile, aAppend));
                int bytesRead = 0;
                while (bytesRead != -1) {
                    bytesRead = inStream.read(bucket); //-1, 0, or more
                    if (bytesRead > 0) {
                        outStream.write(bucket, 0, bytesRead);
                    }
                }
            } finally {
                if (inStream != null) {
                    inStream.close();
                }
                if (outStream != null) {
                    outStream.close();
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void ensureTargetDirectoryExists(File aTargetDir) {
        if (!aTargetDir.exists()) {
            aTargetDir.mkdirs();
        }
    }

}

class FileData {
    protected String fileName;
    protected String packageName;
    protected String fileBaseDir;

    public FileData(String fileName, String packageName, String fileBaseDir) {
        this.fileName = fileName;
        this.packageName = packageName;
        this.fileBaseDir = fileBaseDir;
    }

}
