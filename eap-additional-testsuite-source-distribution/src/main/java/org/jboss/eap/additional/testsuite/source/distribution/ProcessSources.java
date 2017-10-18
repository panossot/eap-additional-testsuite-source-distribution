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

    public static void AdditionalTestSuiteAnnotationProcessing(String basedir, String sourcePath, String server, String version, String versionOrderDir) {
        File folder = new File(sourcePath);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return;
        }

        try {
            for (File file : listOfFiles) {
                if (file.isDirectory()) {
                    AdditionalTestSuiteAnnotationProcessing(basedir, file.getAbsolutePath(), server, version, versionOrderDir);
                } else {
                    ArrayList<FileData> output = checkFileForAnnotation(file.getAbsolutePath(), "@EapAdditionalTestsuite",server);
                    for (FileData dest : output) {
                        if (dest.minVersion!=null)
                        {
                            String[] versionRelease = version.split("-");
                            int verRelease1 = 0;
                            String[] verPart = versionRelease[0].split("\\.");
                            if (verPart.length > 2) {
                                verRelease1 = Integer.parseInt(verPart[0] + verPart[1] + verPart[2]);
                            }
                            String[] subVersions = dest.minVersion.split("-");
                            verPart = subVersions[0].split("\\.");
                            int verRelease2 = 0;
                            if (verPart.length > 2) {
                                verRelease2 = Integer.parseInt(verPart[0] + verPart[1] + verPart[2]);
                            }
                            
                            int verRelease3 = 0;
                            
                            if (dest.maxVersion!=null) {
                                String[] subVersionsMax = dest.maxVersion.split("-");
                                verPart = subVersionsMax[0].split("\\.");

                                if (verPart.length > 2) {
                                    verRelease3 = Integer.parseInt(verPart[0] + verPart[1] + verPart[2]);
                                }
                            }
                           
                            if (subVersions.length > 1 && verRelease1==verRelease2) {
                                File versionFolder = new File(basedir + "/" + versionOrderDir + "/" + server + "/" + subVersions[0]);
                                if (versionFolder.exists()) {
                                    String versionsString = readFile(basedir + "/" + versionOrderDir + "/" + server + "/" + subVersions[0]);
                                    if (versionsString!=null && versionsString.contains(subVersions[1])) {
                                        String[] versions = versionsString.substring(versionsString.indexOf(subVersions[1])).split(",");
                                        for (String versionPart : versionRelease) {
                                            if (versionPart.contains(".")) {
                                                String[] versionNums = versionPart.split("\\.");
                                                String lastPart = versionNums[versionNums.length-1];
                                                if (!lastPart.matches("[0-9]+")) {
                                                    for (String ver : versions) {
                                                        if (lastPart.contains(ver) || lastPart.compareTo(ver)==0) {
                                                            System.out.println(basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName);
                                                            copyWithStreams(file, new File(basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName),false);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (verRelease1>=verRelease2 && (verRelease3==0 || verRelease1<=verRelease3)) {
                                System.out.println(basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName);
                                copyWithStreams(file, new File(basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName),false);
                            }
                        } else {
                            System.out.println(basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName);
                            copyWithStreams(file, new File(basedir + "/" + dest.fileBaseDir + "/" + dest.packageName + "/" + dest.fileName),false);
                        }
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
        String packageName = "";
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
                            if (!path.contains("#") && !path.contains("*"))
                                result.add(new FileData(f.getName(),packageName.replaceAll("\\.", "/"),path,null,null));
                            else if (!path.contains("*")) {
                                String[] pathVersion = path.split("#");
                                result.add(new FileData(f.getName(),packageName.replaceAll("\\.", "/"),pathVersion[0],pathVersion[1],null));
                            } else {
                                String[] pathVersion = path.split("\\*");
                                String[] pathVersion2 = pathVersion[0].split("#");
                                result.add(new FileData(f.getName(),packageName.replaceAll("\\.", "/"),pathVersion2[0],pathVersion2[1],pathVersion[1]));
                            }
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
    
    private static String readFile(String file) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        try {
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
        } finally {
            br.close();
        }
        
        return sb.toString();
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
    protected String minVersion;
    protected String maxVersion;

    public FileData(String fileName, String packageName, String fileBaseDir, String minVersion, String maxVersion) {
        this.fileName = fileName;
        this.packageName = packageName;
        this.fileBaseDir = fileBaseDir;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
    }

}
