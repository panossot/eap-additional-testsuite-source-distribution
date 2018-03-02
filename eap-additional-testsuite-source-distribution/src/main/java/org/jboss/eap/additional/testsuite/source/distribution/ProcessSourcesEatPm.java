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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author panos
 */
public class ProcessSourcesEatPm {
    
    String versionOrder = "Alpha1,Alpha2,Beat1,Beta2,CR1,CR2,CR3,GA,Final";

    public static void EatPmAnnotationProcessing(String basedir, String sourcePath, HashMap<String,PMFeatureData> pmFeatureDataList) {
        File folder = new File(sourcePath);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return;
        }

        try {
            for (File file : listOfFiles) {
                if (file.isDirectory()) {
                    EatPmAnnotationProcessing(basedir, file.getAbsolutePath(), pmFeatureDataList);
                } else {
                    boolean include = checkFileForAnnotation(file.getAbsolutePath(), "@EATDPM");
                    if (!include) {
                       
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static boolean checkFileForAnnotation(String file, String annotationName) throws ClassNotFoundException {
        String annotationLine = null;
        boolean result = false;
        File f = new File(file);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(annotationName)) {
                    annotationLine = line;
                    int configIndex = annotationLine.indexOf("config=\"");
                    String config = annotationLine.substring(configIndex+8, annotationLine.substring(configIndex+8).indexOf("\""));
                    
                    String[] features = annotationLine.substring(annotationLine.lastIndexOf("features={\"") + 11, annotationLine.lastIndexOf("features={\"") + 11 + annotationLine.substring(annotationLine.lastIndexOf("features={\"") + 11).indexOf("\"}")).split(",");
                    String[] minVersions = null;
                    if (annotationLine.lastIndexOf("minVersions={\"") != -1) {
                        minVersions = annotationLine.substring(annotationLine.lastIndexOf("minVersions={\"") + 14, annotationLine.lastIndexOf("minVersions={\"") + 14 + annotationLine.substring(annotationLine.lastIndexOf("minVersions={\"") + 14).indexOf("\"}")).split(",");
                    }
                    String[] maxVersions = null;
                    if (annotationLine.lastIndexOf("maxVersions={\"") != -1) {
                        maxVersions = annotationLine.substring(annotationLine.lastIndexOf("maxVersions={\"") + 14, annotationLine.lastIndexOf("maxVersions={\"") + 14 + annotationLine.substring(annotationLine.lastIndexOf("maxVersions={\"") + 14).indexOf("\"}")).split(",");
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

}


class PMFeatureData {

    protected String config;
    protected ArrayList<String> feature;
    protected ArrayList<String> version;
    protected int linenum;

    public PMFeatureData() {
        feature = new ArrayList();
        version = new ArrayList();
    }

    public PMFeatureData(String config, ArrayList<String> feature, ArrayList<String> version) {
        this.config = config;
        this.feature = feature;
        this.version = version;
    }
    
    public PMFeatureData(ArrayList<String> feature, ArrayList<String> version) {
        this.feature = feature;
        this.version = version;
    }

    public PMFeatureData(ArrayList<String> feature, ArrayList<String> version, int lineNum) {
        this.feature = feature;
        this.version = version;
        this.linenum = lineNum;
    }

}
